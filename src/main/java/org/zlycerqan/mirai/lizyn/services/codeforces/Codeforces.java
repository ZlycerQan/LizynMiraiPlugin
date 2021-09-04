package org.zlycerqan.mirai.lizyn.services.codeforces;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.PluginData;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfoBuilder;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;
import org.zlycerqan.mirai.lizyn.core.database.UnsupportedDatabaseTypeException;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.service.SimpleService;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.LoadConfigErrorException;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.ServiceNotStartException;
import org.zlycerqan.mirai.lizyn.core.utils.IOUtils;
import org.zlycerqan.mirai.lizyn.core.utils.MessageUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class Codeforces extends SimpleService {

    public static final String SERVICE_NAME = "Codeforces";

    public Codeforces(Map<?, ?> config, MiraiLogger miraiLogger, DatabaseManager databaseManager, ExecutorManager executorManager) throws ServiceNotStartException {
        super(config, miraiLogger, databaseManager, executorManager);
    }

    private static final String CACHE_DIRECTION = PluginData.cachePath + "\\" + SERVICE_NAME;
    private static final String CONTESTS_PICTURE_CACHE_FILENAME = "contests_cache.jpg";

    private CodeforcesDatabase codeforcesDatabase;
    private int[] noticeTime;
    private ArrayList<ContestInfo> contestsCache = new ArrayList<>();
    private long botId;

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void loadConfig(@NotNull Map<?, ?> config) throws LoadConfigErrorException {
        if (!IOUtils.checkDirectionIfExists(CACHE_DIRECTION)) {
            if (!IOUtils.createDirections(CACHE_DIRECTION)) {
                getLogger().error(decorateLog("Create cache direction error!"));
            }
        }
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

    private void initFlushContestInfosTimer() {
        try {
            contestsCache = CodeforcesInfoBuilder.getCurrentOrUpcomingContestsInfo();
        } catch (IOException ignore) {
        }
        try {
            assert contestsCache != null;
            CodeforcesUtils.saveContestPicture(CACHE_DIRECTION + "\\" + CONTESTS_PICTURE_CACHE_FILENAME, ContestsPictureBuilder.builderContestsPicture(contestsCache));
        } catch (IOException e) {
            getLogger().error(e.getMessage());
        }
        long flushContestsPeriod = 10 * 1000L;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ArrayList<ContestInfo> contestInfos;
                try {
                    contestInfos = CodeforcesInfoBuilder.getCurrentOrUpcomingContestsInfo();
                } catch (IOException ignore) {
                    contestInfos = contestsCache;
                }
                contestsCache = contestInfos;
                assert contestsCache != null;
                try {
                    CodeforcesUtils.saveContestPicture(CACHE_DIRECTION + "\\" + CONTESTS_PICTURE_CACHE_FILENAME, ContestsPictureBuilder.builderContestsPicture(contestsCache));
                } catch (IOException e) {
                    getLogger().error(decorateLog(e.getMessage()));
                }
                Date now = new Date();
                boolean needSend = false;
                for (ContestInfo i : contestsCache) {
                    for (int j : noticeTime) {
                        if (i.getStart().getTime() - now.getTime() < j * 1000L) {
                            if (!codeforcesDatabase.checkIsSent(i.getId(), j)) {
                                needSend = true;
                                codeforcesDatabase.addSendRecord(i.getId(), j);
                            }
                        }
                    }
                }
                if (needSend) {
                    sendContestTodo();
                }
            }
        }, flushContestsPeriod, flushContestsPeriod);
    }

    private synchronized void sendContestTodo() {
        Bot bot = Bot.findInstance(botId);
        assert bot != null;
        File file = new File(CACHE_DIRECTION + "\\" + CONTESTS_PICTURE_CACHE_FILENAME);
        ExternalResource resource = ExternalResource.create(file);
        Long[] friendIds = codeforcesDatabase.getFriendIds();
        for (long i : friendIds) {
            Friend friend = bot.getFriend(i);
            if (friend == null) {
                continue;
            }
            Image image = friend.uploadImage(resource);
            friend.sendMessage(image);
        }
        Long[] groupIds = codeforcesDatabase.getGroupIds();
        for (long i : groupIds) {
            Group group = bot.getGroup(i);
            if (group == null) {
                continue;
            }
            Image image = group.uploadImage(resource);
            group.sendMessage(image);
        }
        try {
            resource.close();
        } catch (IOException e) {
            getLogger().error(decorateLog(e.getMessage()));
        }
    }

    @Override
    public void loadCommand() {
        addCommand("contest", FriendMessageEvent.class, makeFriendCommandContest());
        addCommand("#start codeforces service", FriendMessageEvent.class, makeFriendCommandStartCodeforcesService());
        addCommand("#stop codeforces service", FriendMessageEvent.class, makeFriendCommandStopCodeforcesService());

        addCommand("contest", GroupMessageEvent.class, makeGroupCommandContest());
        addCommand("#start codeforces service", GroupMessageEvent.class, makeGroupCommandStartCodeforcesService());
        addCommand("#stop codeforces service", GroupMessageEvent.class, makeGroupCommandStopCodeforcesService());
    }

    @Override
    public String getCommand(@NotNull Event event) {
        if (event.getClass() == FriendMessageEvent.class) {
            FriendMessageEvent friendMessageEvent = (FriendMessageEvent) event;
            String text = friendMessageEvent.getMessage().contentToString().trim();
            switch (text) {
                case "#start codeforces service":
                case "#stop codeforces service":
                    return text;
                case "contest": {
                    if (codeforcesDatabase.checkFriend(friendMessageEvent.getFriend().getId())) {
                        return text;
                    } else {
                        return "";
                    }
                }
                default:
                    return "";
            }
        } else if (event.getClass() == GroupMessageEvent.class) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            MessageChain messages = groupMessageEvent.getMessage();
            if (messages.contentToString().trim().equals("contest") && codeforcesDatabase.checkGroup(groupMessageEvent.getGroup().getId())) {
                return "contest";
            }
            long[] atList = MessageUtils.getAtList(messages);
            if (atList.length != 1 || atList[0] != botId) {
                return "";
            }
            String text = MessageUtils.getPlainText(messages).trim();
            switch (text) {
                case "#start codeforces service":
                case "#stop codeforces service":
                    return text;
                default:
                    return "";
            }
        } else {
            return "";
        }
    }

    @NotNull
    @Contract(pure = true)
    private Consumer<Event> makeFriendCommandStartCodeforcesService() {
        return event -> getExecutor().execute(() -> {
            FriendMessageEvent friendMessageEvent = (FriendMessageEvent) event;
            long friendId = friendMessageEvent.getFriend().getId();
            String text;
            if (!codeforcesDatabase.checkFriend(friendId)) {
                codeforcesDatabase.addFriend(friendId);
                text = "Service started successfully.";
            } else {
                text = "Service has been started.";
            }
            friendMessageEvent.getFriend().sendMessage(text);
        });
    }

    @NotNull
    @Contract(pure = true)
    private Consumer<Event> makeGroupCommandStartCodeforcesService() {
        return event -> getExecutor().execute(() -> {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            long groupId = groupMessageEvent.getGroup().getId();
            At at = new At(groupMessageEvent.getSender().getId());
            String text;
            if (groupMessageEvent.getPermission() == MemberPermission.MEMBER) {
                text = "Permission Dined.";
            }
            else if (!codeforcesDatabase.checkGroup(groupId)) {
                codeforcesDatabase.addGroup(groupId);
                text = "Service started successfully.";
            } else {
                text = "Service has been started.";
            }
            groupMessageEvent.getGroup().sendMessage(at.plus(text));
        });
    }

    @NotNull
    @Contract(pure = true)
    private Consumer<Event> makeFriendCommandStopCodeforcesService() {
        return event -> getExecutor().execute(() -> {
            FriendMessageEvent friendMessageEvent = (FriendMessageEvent) event;
            long friendId = friendMessageEvent.getFriend().getId();
            String text;
            if (codeforcesDatabase.checkFriend(friendId)) {
                codeforcesDatabase.deleteFriend(friendId);
                text = "Service stopped successfully.";
            } else {
                text = "Service not started.";
            }
            friendMessageEvent.getFriend().sendMessage(text);
        });
    }

    @NotNull
    @Contract(pure = true)
    private Consumer<Event> makeGroupCommandStopCodeforcesService() {
        return event -> getExecutor().execute(() -> {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            long groupId = groupMessageEvent.getGroup().getId();
            At at = new At(groupMessageEvent.getSender().getId());
            String text;
            if (groupMessageEvent.getPermission() == MemberPermission.MEMBER) {
                text = "Permission Dined.";
            }
            else if (codeforcesDatabase.checkGroup(groupId)) {
                codeforcesDatabase.deleteGroup(groupId);
                text = "Service stopped successfully.";
            } else {
                text = "Service not started.";
            }
            groupMessageEvent.getGroup().sendMessage(at.plus(text));
        });
    }

    @NotNull
    @Contract(pure = true)
    private Consumer<Event> makeFriendCommandContest() {
        return event -> getExecutor().execute(() -> {
            FriendMessageEvent friendMessageEvent = (FriendMessageEvent) event;
            Image image = Contact.uploadImage(friendMessageEvent.getFriend(), new File(CACHE_DIRECTION + "\\" + CONTESTS_PICTURE_CACHE_FILENAME));
            friendMessageEvent.getFriend().sendMessage(image);
        });
    }

    @NotNull
    @Contract(pure = true)
    private Consumer<Event> makeGroupCommandContest() {
        return (event) -> getExecutor().execute(() -> {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            Image image = Contact.uploadImage(groupMessageEvent.getGroup(), new File(CACHE_DIRECTION + "\\" + CONTESTS_PICTURE_CACHE_FILENAME));
            At at = new At(groupMessageEvent.getSender().getId());
            groupMessageEvent.getGroup().sendMessage(at.plus(image));
        });
    }
}
