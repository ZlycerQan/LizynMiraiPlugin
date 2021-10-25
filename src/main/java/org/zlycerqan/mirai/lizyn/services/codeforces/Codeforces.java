package org.zlycerqan.mirai.lizyn.services.codeforces;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfoBuilder;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;
import org.zlycerqan.mirai.lizyn.core.database.UnsupportedDatabaseTypeException;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.service.SimpleService;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.LoadConfigErrorException;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.ServiceNotStartException;
import org.zlycerqan.mirai.lizyn.core.utils.MessageUtils;
import org.zlycerqan.mirai.lizyn.services.codeforces.commands.StartCodeforcesService;
import org.zlycerqan.mirai.lizyn.services.codeforces.commands.StopCodeforcesService;
import org.zlycerqan.mirai.lizyn.services.codeforces.model.Contest;

import java.util.*;

public class Codeforces extends SimpleService {

    public static final String SERVICE_NAME = "Codeforces";

    public Codeforces(Map<?, ?> config, MiraiLogger miraiLogger, DatabaseManager databaseManager, ExecutorManager executorManager) throws ServiceNotStartException {
        super(config, miraiLogger, databaseManager, executorManager);
    }

    private CodeforcesDatabase codeforcesDatabase;

    private int[] noticeTime;

    private List<Contest> contestsCache;

    private String contestsText;

    private ContestUtils contestUtils;

    private long botId;

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void loadConfig(Map<?, ?> config) throws LoadConfigErrorException {
        contestUtils = new ContestUtils();
        Map<?, ?> db = (Map<?, ?>) config.get("database");
        if (db != null) {
            try {
                codeforcesDatabase = new CodeforcesDatabase(DatabaseInfoBuilder.buildDatabaseInfoFromConfig(db), getDatabaseManager(), getLogger());
            } catch (UnsupportedDatabaseTypeException e) {
                throw new LoadConfigErrorException(e.getMessage());
            }
        }
        ArrayList<String> sd = (ArrayList<String>) config.get("notice_time");
        if (sd == null) {
            throw new LoadConfigErrorException(decorateLog("notice_time cannot be null!"));
        } else {
            noticeTime = new int[sd.size()];
            for (int i = 0; i < noticeTime.length; ++ i) {
                noticeTime[i] = Integer.parseInt(sd.get(i));
            }
        }
        botId = Long.parseLong((String) config.get("bot"));
        initFlushContestInfosTimer();
    }

    private synchronized void updateContestsCache() {
        contestsCache = contestUtils.getCurrentOrUpcomingContests();
        if (contestsCache == null) {
            contestsText = "Error.";
        } else if (contestsCache.size() == 0) {
            contestsText = "No contests now.";
        } else {
            contestsText = CodeforcesUtils.formatContestList(contestsCache);
        }
    }

    private void initFlushContestInfosTimer() {
        updateContestsCache();
        long flushContestsPeriod = 10 * 1000L;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateContestsCache();
                boolean needSend = false;
                ArrayList<Contest> contests = new ArrayList<>();
                int now = (int) ((new Date()).getTime() / 1000L);
                for (Contest i : contestsCache) {
                    for (int j : noticeTime) {
                        if (i.getStartTimeSeconds() - now > 0 && i.getStartTimeSeconds() - now < j) {
                            if (!codeforcesDatabase.checkIsSent(i.getId(), j)) {
                                needSend = true;
                                contests.add(i);
                                codeforcesDatabase.addSendRecord(i.getId(), j);
                            }
                        }
                    }
                }
                if (needSend) {
                    sendContestTodo(CodeforcesUtils.formatContestList(contests));
                }
            }
        }, flushContestsPeriod, flushContestsPeriod);
    }

    private synchronized void sendContestTodo(String text) {
        Bot bot = Bot.findInstance(botId);
        assert bot != null;
        Long[] friendIds = codeforcesDatabase.getFriendIds();
        for (long i : friendIds) {
            Friend friend = bot.getFriend(i);
            if (friend == null) {
                continue;
            }
            friend.sendMessage(text);
        }
        Long[] groupIds = codeforcesDatabase.getGroupIds();
        for (long i : groupIds) {
            Group group = bot.getGroup(i);
            if (group == null) {
                continue;
            }
            group.sendMessage(text);
        }
    }

    @Override
    public void loadCommand() {
        addCommand("contest", FriendMessageEvent.class, org.zlycerqan.mirai.lizyn.services.codeforces.commands.Contest.makeFriendMessageEventCommand(this));
        addCommand("#start codeforces service", FriendMessageEvent.class, StartCodeforcesService.makeFriendMessageEventCommand(this));
        addCommand("#stop codeforces service", FriendMessageEvent.class, StopCodeforcesService.makeFriendMessageEventCommand(this));

        addCommand("contest", GroupMessageEvent.class, org.zlycerqan.mirai.lizyn.services.codeforces.commands.Contest.makeGroupMessageEventCommand(this));
        addCommand("#start codeforces service", GroupMessageEvent.class, StartCodeforcesService.makeGroupMessageEventCommand(this));
        addCommand("#stop codeforces service", GroupMessageEvent.class, StopCodeforcesService.makeGroupMessageEventCommand(this));
    }

    @Override
    public String getCommand(Event event) {
        if (event.getClass() == FriendMessageEvent.class) {
            FriendMessageEvent friendMessageEvent = (FriendMessageEvent) event;
            String text = friendMessageEvent.getMessage().contentToString().trim();
            if (text.equals(StartCodeforcesService.command) || text.equals(StopCodeforcesService.command)) {
                return text;
            }
            if (text.equalsIgnoreCase(org.zlycerqan.mirai.lizyn.services.codeforces.commands.Contest.command)) {
                if (codeforcesDatabase.checkFriend(friendMessageEvent.getFriend().getId())) {
                    return org.zlycerqan.mirai.lizyn.services.codeforces.commands.Contest.command;
                } else {
                    return "";
                }
            }
            return "";
        } else if (event.getClass() == GroupMessageEvent.class) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            MessageChain messages = groupMessageEvent.getMessage();
            if (messages.contentToString().toLowerCase().trim().equals(org.zlycerqan.mirai.lizyn.services.codeforces.commands.Contest.command) && codeforcesDatabase.checkGroup(groupMessageEvent.getGroup().getId())) {
                return org.zlycerqan.mirai.lizyn.services.codeforces.commands.Contest.command;
            }
            long[] atList = MessageUtils.getAtList(messages);
            if (atList.length != 1 || atList[0] != botId) {
                return "";
            }
            String text = MessageUtils.getPlainText(messages).trim();
            if (text.equals(StartCodeforcesService.command) || text.equals(StopCodeforcesService.command)) {
                return text;
            }
            return "";
        } else {
            return "";
        }
    }

    public String getContestsText() {
        return contestsText;
    }

    public CodeforcesDatabase getCodeforcesDatabase() {
        return codeforcesDatabase;
    }

}
