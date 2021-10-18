package org.zlycerqan.mirai.lizyn.services.codeforces.commands;

import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.services.codeforces.Codeforces;
import org.zlycerqan.mirai.lizyn.services.codeforces.CodeforcesDatabase;

import java.util.function.Consumer;

public class StartCodeforcesService {

    public final static String command = "#start codeforces service";

    public static Consumer<Event> makeFriendMessageEventCommand(Codeforces service) {
        return origin -> service.getExecutor().execute(() -> {
            FriendMessageEvent event = (FriendMessageEvent) origin;
            long friendId = event.getFriend().getId();
            String text;
            if (!service.getCodeforcesDatabase().checkFriend(friendId)) {
                service.getCodeforcesDatabase().addFriend(friendId);
                text = "Service started successfully.";
            } else {
                text = "Service has been started.";
            }
            event.getFriend().sendMessage(text);
        });
    }

    public static Consumer<Event> makeGroupMessageEventCommand(Codeforces service) {
        return origin -> service.getExecutor().execute(() -> {
            GroupMessageEvent event = (GroupMessageEvent) origin;
            long groupId = event.getGroup().getId();
            At at = new At(event.getSender().getId());
            String text;
            if (event.getPermission() == MemberPermission.MEMBER) {
                text = "Permission Dined.";
            } else if (!service.getCodeforcesDatabase().checkGroup(groupId)) {
                service.getCodeforcesDatabase().addGroup(groupId);
                text = "Service started successfully.";
            } else {
                text = "Service has been started.";
            }
            event.getGroup().sendMessage(at.plus(text));
        });
    }
}
