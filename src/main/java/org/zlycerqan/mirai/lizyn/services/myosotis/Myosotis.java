package org.zlycerqan.mirai.lizyn.services.myosotis;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfoBuilder;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;
import org.zlycerqan.mirai.lizyn.core.database.UnsupportedDatabaseTypeException;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.service.SimpleService;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.LoadConfigErrorException;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.ServiceNotStartException;
import org.zlycerqan.mirai.lizyn.services.myosotis.commands.MyosotisPicture;

import java.util.ArrayList;
import java.util.Map;

public class Myosotis extends SimpleService {

    public static String SERVICE_NAME = "Myosotis";

    public Myosotis(Map<?, ?> config, MiraiLogger miraiLogger, DatabaseManager databaseManager, ExecutorManager executorManager) throws ServiceNotStartException {
        super(config, miraiLogger, databaseManager, executorManager);
    }

    public ArrayList<String> pictureServerURLs;

    public MyosotisDatabase myosotisDatabase;

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void loadConfig(Map<?, ?> config) throws LoadConfigErrorException {
        String coolDown = (String) config.get("cool_down");
        int cd;
        if (coolDown != null) {
            cd = Integer.parseInt(coolDown);
        } else {
            cd = 0;
        }
        Map<?, ?> db = (Map<?, ?>) config.get("database");
        if (db != null) {
            try {
                myosotisDatabase = new MyosotisDatabase(DatabaseInfoBuilder.buildDatabaseInfoFromConfig(db), getDatabaseManager(), getLogger(), cd);
            } catch (UnsupportedDatabaseTypeException e) {
                throw new LoadConfigErrorException(decorateLog(e.getMessage()));
            }
        } else {
            throw new LoadConfigErrorException(decorateLog("Database cannot be null!"));
        }
        ArrayList<String> urls = (ArrayList<String>) config.get("picture_server_url");
        if (urls != null) {
            pictureServerURLs = urls;
        }
        String recallTime = (String) config.get("recall_time");
        if (recallTime != null) {
            MyosotisPicture.setRecallTime(Integer.parseInt(recallTime));
        }
    }

    @Override
    public void loadCommand() {
        addCommand(MyosotisPicture.COMMAND, FriendMessageEvent.class, MyosotisPicture.makeFriendMessageEventCommand(getExecutorManager(), myosotisDatabase, pictureServerURLs));

        addCommand(MyosotisPicture.COMMAND, GroupMessageEvent.class, MyosotisPicture.makeGroupMessageEventCommand(getExecutorManager(), myosotisDatabase, pictureServerURLs));
    }

    @Override
    public String getCommand(Event event) {
        if (event.getClass().equals(FriendMessageEvent.class)) {
            if (((FriendMessageEvent) event).getMessage().contentToString().equals(MyosotisPicture.COMMAND)) {
                return MyosotisPicture.COMMAND;
            }
        } else if (event.getClass().equals(GroupMessageEvent.class)) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            if (groupMessageEvent.getMessage().contentToString().equals(MyosotisPicture.COMMAND) && myosotisDatabase.checkGroup(groupMessageEvent.getGroup().getId())) {
                return MyosotisPicture.COMMAND;
            }
        }
        return "";
    }
}
