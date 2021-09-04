package org.zlycerqan.mirai.lizyn.services.chat.commands;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageContent;
import net.mamoe.mirai.message.data.PlainText;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.services.chat.Chat;
import org.zlycerqan.mirai.lizyn.services.chat.ChatDatabase;
import org.zlycerqan.mirai.lizyn.services.chat.ChatPermission;

import java.sql.SQLException;
import java.util.function.Consumer;

public class Tell {

    public static String COMMAND = "#tell";

    public static Consumer<Event> makeFriendMessageEventCommand(ExecutorManager executorManager, ChatDatabase chatDatabase, Chat tool) {
        return origin -> executorManager.getExecutor().execute(() -> {
            FriendMessageEvent event = (FriendMessageEvent) origin;
            int permission = 0;
            final String databaseError = "Database error!";
            try {
                permission = chatDatabase.getPermission(event.getFriend().getId());
            } catch (SQLException e) {
                event.getFriend().sendMessage(databaseError);
                tool.getLogger().error(tool.decorateLog(databaseError));
            }
            if (permission != ChatPermission.SUPER_ROOT && permission != ChatPermission.ROOT) {
                event.getFriend().sendMessage("Permission dined.");
                return ;
            }
            MessageChain messages = event.getMessage();
            MessageContent messageContent = messages.get(PlainText.Key);
            if (messageContent == null) {
                return ;
            }
            String firstPlainText = messageContent.contentToString();
            String[] args = firstPlainText.split(" ");
            if (args.length < 3) {
                event.getFriend().sendMessage("Parameters error.");
                return ;
            }
            int type; // 0 friend 1 group
            long target;
            if (args[1].equals("f") || args[1].equals("friend")) {
                type = 0;
                target = Long.parseLong(args[2]);
            } else if (args[1].equals("g") || args[1].equals("group")) {
                type = 1;
                target = Long.parseLong(args[2]);
            } else {
                event.getFriend().sendMessage("Parameters error.");
                return ;
            }
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            int count = 0;
            if (args.length >= 4) {
                int beginPos = 3;
                for (int i = 0; i < 3; ++ i) {
                    beginPos += args[i].length();
                }
                String t = firstPlainText.substring(beginPos);
                if (t.length() != 0) {
                    messageChainBuilder.add(t);
                    count = 1;
                }
            }
            for (int i = 2; i < messages.size(); ++ i) {
                messageChainBuilder.add(messages.get(i));
            }
            if (count == 0 && messageChainBuilder.size() == 0) {
                event.getFriend().sendMessage("Parameters error.");
            } else {
                Bot bot = event.getBot();
                if (type == 0) {
                    Friend friend = bot.getFriend(target);
                    if (friend == null) {
                        event.getFriend().sendMessage("Can not find friend " + target + ".");
                    } else {
                        friend.sendMessage(messageChainBuilder.asMessageChain());
                        event.getFriend().sendMessage("Tell successfully.");
                    }
                } else {
                    Group group = bot.getGroup(target);
                    if (group == null) {
                        event.getFriend().sendMessage("Can not find group " + target + ".");
                    } else {
                        group.sendMessage(messageChainBuilder.asMessageChain());
                        event.getFriend().sendMessage("Tell successfully.");
                    }
                }
            }
        });
    }
}
