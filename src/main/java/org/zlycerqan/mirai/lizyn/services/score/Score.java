package org.zlycerqan.mirai.lizyn.services.score;

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
import org.zlycerqan.mirai.lizyn.services.score.commands.AddAccount;
import org.zlycerqan.mirai.lizyn.services.score.commands.DeleteAccount;
import org.zlycerqan.mirai.lizyn.services.score.commands.GetScore;
import org.zlycerqan.mirai.lizyn.services.score.connection.UserConnection;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Score extends SimpleService {

    public static String SERVICE_NAME = "Score";

    private ScoreDatabase scoreDatabase;

    public Score(Map<?, ?> config, MiraiLogger miraiLogger, DatabaseManager databaseManager, ExecutorManager executorManager) throws ServiceNotStartException {
        super(config, miraiLogger, databaseManager, executorManager);
    }

    public Map<Long, UserConnection> userConnectionMap;

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void loadConfig(Map<?, ?> config) throws LoadConfigErrorException {
        Map<?, ?> db = (Map<?, ?>) config.get("database");
        if (db != null) {
            try {
                scoreDatabase = new ScoreDatabase(DatabaseInfoBuilder.buildDatabaseInfoFromConfig(db), getDatabaseManager());
            } catch (UnsupportedDatabaseTypeException | SQLException e) {
                throw new LoadConfigErrorException(decorateLog(e.getMessage()));
            }
        } else {
            throw new LoadConfigErrorException(decorateLog("Database cannot be null!"));
        }
        userConnectionMap = new ConcurrentHashMap<>();
    }

    @Override
    public void loadCommand() {
        addCommand(GetScore.COMMAND, FriendMessageEvent.class, GetScore.makeFriendMessageEventCommand(getExecutorManager(), scoreDatabase, userConnectionMap));
        addCommand(AddAccount.COMMAND, FriendMessageEvent.class, AddAccount.makeFriendMessageEventCommand(getExecutorManager(), scoreDatabase, userConnectionMap));
        addCommand(DeleteAccount.COMMAND, FriendMessageEvent.class, DeleteAccount.makeFriendMessageEventCommand(getExecutorManager(), scoreDatabase, userConnectionMap));
    }

    @Override
    public String getCommand(Event event) {
        if (event.getClass().equals(FriendMessageEvent.class)) {
            FriendMessageEvent friendMessageEvent = (FriendMessageEvent) event;
            String text = friendMessageEvent.getMessage().contentToString().trim();
            if (text.equals(GetScore.COMMAND)) {
                return GetScore.COMMAND;
            } else if (text.startsWith(AddAccount.COMMAND)) {
                return AddAccount.COMMAND;
            } else if (text.equals(DeleteAccount.COMMAND)) {
                return DeleteAccount.COMMAND;
            }
        }
        return "";
    }
}
