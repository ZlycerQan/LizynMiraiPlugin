package org.zlycerqan.mirai.lizyn.services.codeforces.commands;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.zlycerqan.mirai.lizyn.services.codeforces.Codeforces;

import java.util.function.Consumer;

public class Contest {

    public final static String command = "contest";

    public static Consumer<Event> makeFriendMessageEventCommand(Codeforces service) {
        return origin -> service.getExecutor().execute(() -> {
            FriendMessageEvent event = (FriendMessageEvent) origin;
            event.getFriend().sendMessage(service.getContestsText());
        });
    }

    public static Consumer<Event> makeGroupMessageEventCommand(Codeforces service) {
        return origin -> service.getExecutor().execute(() -> {
            GroupMessageEvent event = (GroupMessageEvent) origin;
            event.getGroup().sendMessage(service.getContestsText());
        });
    }
}
