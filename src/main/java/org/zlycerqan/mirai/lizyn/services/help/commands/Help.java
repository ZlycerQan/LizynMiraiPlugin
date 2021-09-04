package org.zlycerqan.mirai.lizyn.services.help.commands;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.utils.IOUtils;

import java.io.IOException;
import java.util.function.Consumer;

public class Help {

    public static String COMMAND = "#help";

    public static Consumer<Event> makeFriendMessageEventCommand(@NotNull ExecutorManager executorManager, String helpFilePath) {
        return origin -> executorManager.getExecutor().execute(() -> {
            FriendMessageEvent event = (FriendMessageEvent) origin;
            try {
                String text = IOUtils.readFromFile(helpFilePath);
                if (text.length() == 0) {
                    event.getFriend().sendMessage("Server error.");
                } else {
                    event.getFriend().sendMessage(text);
                }
            } catch (IOException ignore) {
            }
        });
    }
}
