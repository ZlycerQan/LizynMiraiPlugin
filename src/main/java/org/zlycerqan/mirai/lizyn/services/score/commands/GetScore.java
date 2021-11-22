package org.zlycerqan.mirai.lizyn.services.score.commands;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.services.score.Account;
import org.zlycerqan.mirai.lizyn.services.score.ScoreDatabase;
import org.zlycerqan.mirai.lizyn.services.score.connection.UserConnection;
import org.zlycerqan.mirai.lizyn.services.score.connection.exception.CookiesErrorException;
import org.zlycerqan.mirai.lizyn.services.score.connection.exception.PasswordWrongException;
import org.zlycerqan.mirai.lizyn.services.score.connection.exception.ServerErrorException;
import org.zlycerqan.mirai.lizyn.services.score.connection.model.scoremodel.ScoreModel;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

public class GetScore {

    public static String COMMAND = "gs";

    private static String makeResponse(ArrayList<ScoreModel> arr) {
        if (arr == null || arr.size() == 0) {
            return "ScoreModel list is null.";
        }
        StringBuilder stringBuilder = new StringBuilder();
        double sxf = 0, s = 0;
        for (ScoreModel scoreModel : arr) {
            stringBuilder.append("Class: ").append(scoreModel.getKcmc()).append('\n');
            stringBuilder.append("Score: ").append(scoreModel.getCj()).append('\n');
            stringBuilder.append("Credit: ").append(scoreModel.getXf()).append('\n');
            stringBuilder.append("----------\n");
            int cj;
            try {
                cj = Integer.parseInt(scoreModel.getCj());
            } catch (NumberFormatException e) {
                continue;
            }
            double xf = Double.parseDouble(scoreModel.getXf());
            sxf += xf;
            s += cj * xf;
        }
        s /= sxf;
        s = (s - 50) / 10;
        stringBuilder.append("GPA: ").append(String.format("%.2f", s));
        return stringBuilder.toString();
    }

    public static Consumer<Event> makeFriendMessageEventCommand(@NotNull ExecutorManager executorManager, ScoreDatabase scoreDatabase, Map<Long, UserConnection> userConnectionMap) {
        return origin -> executorManager.getExecutor().execute(() -> {
            FriendMessageEvent event = (FriendMessageEvent) origin;
            long friendId = event.getFriend().getId();
            try {
                Account account = scoreDatabase.getAccount(friendId);
                if (account == null) {
                    event.getFriend().sendMessage("Can not find your account.");
                } else {
                    UserConnection userConnection = userConnectionMap.get(friendId);
                    if (userConnection == null) {
                        try {
                            userConnection = new UserConnection(account.getId(), account.getPassword());
                            userConnectionMap.put(friendId, userConnection);
                        } catch (PasswordWrongException e) {
                            userConnectionMap.remove(friendId);
                            event.getFriend().sendMessage("Password error.");
                            scoreDatabase.addHistory(event.getFriend().getId(), 0);
                            return ;
                        } catch (ServerErrorException e) {
                            event.getFriend().sendMessage("Website server error.");
                            scoreDatabase.addHistory(event.getFriend().getId(), 0);
                            return ;
                        }
                    }
                    try {
                        event.getFriend().sendMessage(makeResponse(userConnection.fetchScoreModelList("2021",
                                "3",
                                "false",
                                String.valueOf((new Date()).getTime()),
                                "15",
                                "1",
                                "",
                                "asc")));
                        scoreDatabase.addHistory(event.getFriend().getId(), 1);
                        return ;
                    } catch (CookiesErrorException e) {
                        userConnectionMap.remove(friendId);
                        event.getFriend().sendMessage("Cookie error.");
                    } catch (ServerErrorException e) {
                        event.getFriend().sendMessage("Website server error.");
                    } catch (PasswordWrongException e) {
                        userConnectionMap.remove(friendId);
                        event.getFriend().sendMessage("Password error.");
                    } catch (IOException e) {
                        event.getFriend().sendMessage("Server error.");
                    }
                    scoreDatabase.addHistory(event.getFriend().getId(), 0);
                }
            } catch (SQLException e) {
                event.getFriend().sendMessage("Database error.");
                try {
                    scoreDatabase.addHistory(event.getFriend().getId(), 0);
                } catch (SQLException ignore) {
                }
            }
        });
    }

}
