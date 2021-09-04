package org.zlycerqan.mirai.lizyn.services.score.commands;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.services.score.ScoreDatabase;
import org.zlycerqan.mirai.lizyn.services.score.connection.UserConnection;

import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;

public class AddAccount {

    public static String COMMAND = "#cos score add";

    public static Consumer<Event> makeFriendMessageEventCommand(@NotNull ExecutorManager executorManager, ScoreDatabase scoreDatabase, Map<Long, UserConnection> userConnectionMap) {
        return origin -> executorManager.getExecutor().execute(() -> {
            FriendMessageEvent event = (FriendMessageEvent) origin;
            String[] args = event.getMessage().contentToString().split(" ");
            if (args.length == 5) {
                String id = args[3];
                String password = args[4];
                if (id.length() != 10) {
                    event.getFriend().sendMessage("Account id error.");
                    return;
                }
                try {
                    if (scoreDatabase.getAccount(event.getFriend().getId()) == null) {
                        scoreDatabase.addAccount(event.getFriend().getId(), id, password);
                        event.getFriend().sendMessage("Add account successfully.");
                    } else {
                        scoreDatabase.updateAccount(event.getFriend().getId(), id, password);
                        userConnectionMap.remove(event.getFriend().getId());
                        event.getFriend().sendMessage("Update account successfully.");
                    }
                } catch (SQLException e) {
                    event.getFriend().sendMessage("Database error.");
                }
            } else {
                event.getFriend().sendMessage("Parameters error.");
            }
        });
    }
}
