package org.zlycerqan.mirai.lizyn.services.codeforces.commands;

import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import org.zlycerqan.mirai.lizyn.services.codeforces.Codeforces;

import java.util.function.Consumer;

public class StopCodeforcesService {

    public final static String command = "#stop codeforces service";

    public static Consumer<Event> makeFriendMessageEventCommand(Codeforces service) {
        return origin -> service.getExecutor().execute(() -> {
            FriendMessageEvent event = (FriendMessageEvent) origin;
            long friendId = event.getFriend().getId();
            String text;
            if (service.getCodeforcesDatabase().checkFriend(friendId)) {
                service.getCodeforcesDatabase().deleteFriend(friendId);
                text = "Service stopped successfully.";
            } else {
                text = "Service not started.";
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
            }
            else if (service.getCodeforcesDatabase().checkGroup(groupId)) {
                service.getCodeforcesDatabase().deleteGroup(groupId);
                text = "Service stopped successfully.";
            } else {
                text = "Service not started.";
            }
            event.getGroup().sendMessage(at.plus(text));
        });
    }
}
