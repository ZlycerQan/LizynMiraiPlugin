package org.zlycerqan.mirai.lizyn.services.libduty;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfoBuilder;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;
import org.zlycerqan.mirai.lizyn.core.database.UnsupportedDatabaseTypeException;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.service.SimpleService;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.LoadConfigErrorException;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.ServiceNotStartException;
import org.zlycerqan.mirai.lizyn.core.utils.MessageUtils;

import java.sql.SQLException;
import java.util.*;

public class LibDuty extends SimpleService {

    public static String SERVICE_NAME = "LibDuty";

    public LibDuty(Map<?, ?> config, MiraiLogger miraiLogger, DatabaseManager databaseManager, ExecutorManager executorManager) throws ServiceNotStartException {
        super(config, miraiLogger, databaseManager, executorManager);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    private LibDutyDatabase libDutyDatabase;
    private long botId;
    private long targetGroup;
    private Date[] dateList;
    private String endTimeString;
    private Date endDate;
    private final String replyText = "值日完成";

    @Override
    public void loadConfig(Map<?, ?> config) throws LoadConfigErrorException {
        Map<?, ?> db = (Map<?, ?>) config.get("database");
        if (db != null) {
            try {
                libDutyDatabase = new LibDutyDatabase(DatabaseInfoBuilder.buildDatabaseInfoFromConfig(db), getDatabaseManager(), getLogger());
            } catch (UnsupportedDatabaseTypeException e) {
                throw new LoadConfigErrorException(decorateLog(e.getMessage()));
            }
        } else {
            throw new LoadConfigErrorException(decorateLog("Database cannot be null!"));
        }
        botId = Long.parseLong((String) config.get("bot"));
        targetGroup = Long.parseLong((String) config.get("group"));
        ArrayList<String> tms = (ArrayList<String>) config.get("time");
        dateList = new Date[tms.size()];
        Date now = new Date();
        for (int i = 0; i < dateList.length; ++ i) {
            dateList[i] = LibDutyUtils.transDate(tms.get(i));
            if (dateList[i].getTime() <= now.getTime()) {
                LibDutyUtils.nextDay(dateList[i]);
            }
        }
        endTimeString = (String) config.get("end_time");
        endDate = LibDutyUtils.transDate(endTimeString);
        if (endDate.getTime() <= now.getTime()) {
            LibDutyUtils.nextDay(endDate);
        }
        initTimer();
    }

    void initTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public synchronized void run() {
                for (Date date : dateList) {
                    Date now = new Date();
                    if (date.getTime() <= now.getTime()) {
                        try {
                            if (libDutyDatabase.checkIfFinishByDate(endDate)) {
                                LibDutyUtils.nextDay(date);
                                continue;
                            }
                        } catch (SQLException e) {
                            getLogger().error(e.getMessage());
                        }
                        ArrayList<String> names = new ArrayList<>();
                        try {
                            names = libDutyDatabase.getName(LibDutyUtils.getCurrentDayOfWeek());
                        } catch (SQLException e) {
                            getLogger().error(e.getMessage());
                        }
                        if (names.size() == 0) {
                            LibDutyUtils.nextDay(date);
                            continue;
                        }
                        StringBuilder stringBuilder = new StringBuilder("今日值日：\n");
                        for (String name : names) {
                            stringBuilder.append(name);
                            stringBuilder.append('\n');
                        }
                        stringBuilder.append("完成后请艾特我并回复\"").append(replyText).append("\"");
                        if (sendMessage(stringBuilder.toString())) {
                            LibDutyUtils.nextDay(date);
                        }
                    }
                }
            }
        }, 5000L, 5000L);
        Timer endTimer = new Timer();
        endTimer.schedule(new TimerTask() {
            @Override
            public synchronized void run() {
                Date now = new Date();
                if (endDate.getTime() <= now.getTime()) {
                    ArrayList<Long> ids = new ArrayList<>();
                    try {
                        ids = libDutyDatabase.getId(LibDutyUtils.getCurrentDayOfWeek());
                    } catch (SQLException e) {
                        getLogger().error(e.getMessage());
                    }
                    if (ids.size() == 0) {
                        LibDutyUtils.nextDay(endDate);
                        return ;
                    }
                    Date to = endDate;
                    Date from = new Date();
                    from.setTime(to.getTime() - 24L * 60L * 60L * 1000L);
                    boolean isFinish = false;
                    for (long id : ids) {
                        try {
                            if (libDutyDatabase.checkIfFinish(id, from, to)) {
                                isFinish = true;
                                break;
                            }
                        } catch (SQLException e) {
                            getLogger().error(e.getMessage());
                        }
                    }
                    ArrayList<String> names = new ArrayList<>();
                    try {
                        names = libDutyDatabase.getName(LibDutyUtils.getCurrentDayOfWeek());
                    } catch (SQLException e) {
                        getLogger().error(e.getMessage());
                    }
                    if (names.size() == 0) {
                        return ;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    if (isFinish) {
                        stringBuilder.append("今日值日已完成\n");
                    } else {
                        stringBuilder.append("今日值日未完成 T_T\n");
                    }
                    stringBuilder.append("值日成员：\n");
                    for (String name : names) {
                        stringBuilder.append(name);
                        stringBuilder.append('\n');
                    }
                    if (sendMessage(stringBuilder.toString())) {
                        LibDutyUtils.nextDay(endDate);
                    }
                }
            }
        }, 5000L, 5000L);
    }

    private synchronized boolean sendMessage(String text) {
        Bot bot = Bot.findInstance(botId);
        if (bot == null) {
            return false;
        }
        Group group = bot.getGroup(targetGroup);
        if (group == null) {
            return false;
        }
        getExecutor().execute(() -> group.sendMessage(text));
        return true;
    }

    final private String finishDutyCommand = "finish duty";

    @Override
    public void loadCommand() {
        addCommand(finishDutyCommand, GroupMessageEvent.class, origin -> getExecutor().execute(() -> {
            GroupMessageEvent event = (GroupMessageEvent) origin;
            String text = MessageUtils.getPlainText(event.getMessage()).trim();
            if (text.equals(replyText)) {
                try {
                    if (libDutyDatabase.checkFriend(event.getSender().getId(), LibDutyUtils.getCurrentDayOfWeek())) {
                        Date to = new Date();
                        Date from = LibDutyUtils.transDate(endTimeString);
                        if (from.getTime() < to.getTime()) {
                            event.getGroup().sendMessage("超过时间限制 >_<");
                        }
                        from.setTime(from.getTime() - 24L * 60L * 60L * 1000L);
                        if (libDutyDatabase.checkIfFinish(event.getSender().getId(), from, to)) {
                            event.getGroup().sendMessage("你已完成今日值日，无需重复回复 >_<");
                        } else {
                            libDutyDatabase.addHistory(event.getSender().getId());
                            if (event.getSender().getId() == 2020593962L) {
                                event.getGroup().sendMessage(":)");
                            } else if (event.getSender().getId() == 2601268730L) {
                                event.getGroup().sendMessage(":(");
                            } else {
                                event.getGroup().sendMessage("好");
                            }
                        }
                    } else {
                        event.getGroup().sendMessage("您不是今日值日生 >_<");
                    }
                } catch (SQLException e) {
                    getLogger().error(e.getMessage());
                }
            }
        }));
    }

    @Override
    public String getCommand(Event event) {
        if (event.getClass() == GroupMessageEvent.class) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            if (groupMessageEvent.getGroup().getId() != targetGroup) {
                return "";
            }
            long[] ats = MessageUtils.getAtList(groupMessageEvent.getMessage());
            if (ats.length != 1 || ats[0] != botId) {
                return "";
            }
            String text = MessageUtils.getPlainText(groupMessageEvent.getMessage()).trim();
            if (text.equals(replyText)) {
                return finishDutyCommand;
            }
        }
        return "";
    }
}
