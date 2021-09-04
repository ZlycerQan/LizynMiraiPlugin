package org.zlycerqan.mirai.lizyn.services.chat;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfoBuilder;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;
import org.zlycerqan.mirai.lizyn.core.database.UnsupportedDatabaseTypeException;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.service.SimpleService;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.LoadConfigErrorException;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.ServiceNotStartException;
import org.zlycerqan.mirai.lizyn.services.chat.commands.Tell;

import java.sql.SQLException;
import java.util.Map;

public class Chat extends SimpleService  {


    public static final String SERVICE_NAME = "Chat";

    private ChatDatabase chatDatabase;

    long botId;

    public Chat(Map<?, ?> config, MiraiLogger miraiLogger, DatabaseManager databaseManager, ExecutorManager executorManager) throws ServiceNotStartException {
        super(config, miraiLogger, databaseManager, executorManager);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void loadConfig(Map<?, ?> config) throws LoadConfigErrorException {
        botId = (Long.parseLong((String) config.get("bot")));
        Map<?, ?> db = (Map<?, ?>) config.get("database");
        if (db != null) {
            try {
                chatDatabase = new ChatDatabase(DatabaseInfoBuilder.buildDatabaseInfoFromConfig(db), getDatabaseManager(), getLogger());
            } catch (UnsupportedDatabaseTypeException e) {
                throw new LoadConfigErrorException(decorateLog(e.getMessage()));
            }
        } else {
            throw new LoadConfigErrorException(decorateLog("Database cannot be null!"));
        }
        String rootId = (String) config.get("super_root");
        if (rootId == null) {
            throw new LoadConfigErrorException(decorateLog("super root cannot be null!"));
        } else {
            try {
                chatDatabase.setSuperRoot(Long.parseLong(rootId));
            } catch (SQLException e) {
                getLogger().error(decorateLog(e.getMessage()));
                throw new LoadConfigErrorException(decorateLog(e.getMessage()));
            }
        }
    }

    @Override
    public void loadCommand() {
        addCommand(Tell.COMMAND, FriendMessageEvent.class, Tell.makeFriendMessageEventCommand(getExecutorManager(), chatDatabase, this));

    }

    @Override
    public String getCommand(Event event) {
        if (event.getClass() == FriendMessageEvent.class) {
            FriendMessageEvent friendMessageEvent = (FriendMessageEvent) event;
            String message = friendMessageEvent.getMessage().contentToString().trim();
            if (message.startsWith(Tell.COMMAND)) {
                return Tell.COMMAND;
            }
        }
        return "";
    }
}
