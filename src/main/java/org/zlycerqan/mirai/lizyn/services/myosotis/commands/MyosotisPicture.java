package org.zlycerqan.mirai.lizyn.services.myosotis.commands;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.services.myosotis.MyosotisDatabase;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Consumer;

public class MyosotisPicture {

    public static String COMMAND = "my";

    private static long recallTime = 30;

    public static void setRecallTime(long recallTime_) {
        recallTime = recallTime_;
    }

    public static Consumer<Event> makeFriendMessageEventCommand(@NotNull ExecutorManager executorManager, MyosotisDatabase myosotisDatabase, ArrayList<String> urls) {
        return origin -> executorManager.getExecutor().execute(() -> {
            FriendMessageEvent event = (FriendMessageEvent) origin;
            boolean isCoolDown = false;
            try {
                isCoolDown = myosotisDatabase.checkCoolDown(event.getFriend().getId());
            } catch (SQLException e) {
                event.getFriend().sendMessage("Database error.");
            }
            if (!isCoolDown) {
                event.getFriend().sendMessage("cooling.");
                return ;
            }
            try {
                myosotisDatabase.updateCoolDown(event.getFriend().getId(), new Date());
            } catch (SQLException ignore) {
            }
            for (String i : urls) {
                try {
                    URL url = new URL(i);
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(2 * 1000);
                    Image image = Contact.uploadImage(event.getFriend(), connection.getInputStream());
                    event.getFriend().sendMessage(image).recallIn(recallTime * 1000L);
                    return ;
                } catch (IOException ignored) {
                }
            }
            event.getFriend().sendMessage("No picture server was configured.");
        });
    }

    public static Consumer<Event> makeGroupMessageEventCommand(@NotNull ExecutorManager executorManager, MyosotisDatabase myosotisDatabase, ArrayList<String> urls) {
        return origin -> executorManager.getExecutor().execute(() -> {
            GroupMessageEvent event = (GroupMessageEvent) origin;
            boolean isCoolDown = false;
            try {
                isCoolDown = myosotisDatabase.checkCoolDown(event.getSender().getId());
            } catch (SQLException e) {
                event.getGroup().sendMessage("Database error.");
            }
            if (!isCoolDown) {
                event.getGroup().sendMessage("cooling.");
                return ;
            }
            try {
                myosotisDatabase.updateCoolDown(event.getSender().getId(), new Date());
            } catch (SQLException ignore) {
            }
            for (String i : urls) {
                try {
                    URL url = new URL(i);
                    URLConnection connection = url.openConnection();
                    connection.setConnectTimeout(2 * 1000);
                    Image image = Contact.uploadImage(event.getGroup(), connection.getInputStream());
                    event.getGroup().sendMessage(image).recallIn(recallTime * 1000L);
                    return ;
                } catch (IOException ignored) {
                }
            }
            event.getGroup().sendMessage("No picture server was configured.");
        });
    }
}
