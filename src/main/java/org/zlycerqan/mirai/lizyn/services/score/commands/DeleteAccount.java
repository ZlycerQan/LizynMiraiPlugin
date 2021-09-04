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

public class DeleteAccount {
    public static String COMMAND = "#cos score delete me";

    public static Consumer<Event> makeFriendMessageEventCommand(@NotNull ExecutorManager executorManager, ScoreDatabase scoreDatabase, Map<Long, UserConnection> userConnectionMap) {
        return origin -> executorManager.getExecutor().execute(() -> {
            FriendMessageEvent event = (FriendMessageEvent) origin;
            try {
                if (scoreDatabase.getAccount(event.getFriend().getId()) == null) {
                    event.getFriend().sendMessage("You have not added account.");
                } else {
                    scoreDatabase.deleteAccount(event.getFriend().getId());
                    userConnectionMap.remove(event.getFriend().getId());
                    event.getFriend().sendMessage("Delete account successfully.");
                }
            } catch (SQLException e) {
                event.getFriend().sendMessage("Database error.");
            }
        });
    }
}
