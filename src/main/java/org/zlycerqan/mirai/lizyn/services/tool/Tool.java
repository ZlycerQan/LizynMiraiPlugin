package org.zlycerqan.mirai.lizyn.services.tool;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.service.SimpleService;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.LoadConfigErrorException;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.ServiceNotStartException;
import org.zlycerqan.mirai.lizyn.core.utils.MessageUtils;
import org.zlycerqan.mirai.lizyn.services.tool.commands.Rand;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Tool extends SimpleService {

    public static final String SERVICE_NAME = "Tool";

    public Tool(Map<?, ?> config, MiraiLogger miraiLogger, DatabaseManager databaseManager, ExecutorManager executorManager) throws ServiceNotStartException {
        super(config, miraiLogger, databaseManager, executorManager);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public void loadConfig(@NotNull Map<?, ?> config) throws LoadConfigErrorException {
    }

    @Override
    public void loadCommand() {
        addCommand(Rand.COMMAND, FriendMessageEvent.class, Rand.makeFriendMessageEventCommand(getExecutorManager()));

        addCommand(Rand.COMMAND, GroupMessageEvent.class, Rand.makeGroupMessageEventCommand(getExecutorManager()));
    }

    @Override
    public String getCommand(Event event) {
        if (event.getClass() == FriendMessageEvent.class) {
            FriendMessageEvent friendMessageEvent = (FriendMessageEvent) event;
            String message = friendMessageEvent.getMessage().contentToString().trim();
            if (message.startsWith(Rand.COMMAND)) {
                return Rand.COMMAND;
            }
        } else if (event.getClass() == GroupMessageEvent.class) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            MessageChain messages = groupMessageEvent.getMessage();
            long[] atList = MessageUtils.getAtList(messages);
            if (atList.length == 1 && atList[0] == groupMessageEvent.getBot().getId()) {
                String text = MessageUtils.getPlainText(messages).trim();
                if (text.startsWith(Rand.COMMAND)) {
                    return Rand.COMMAND;
                }
            }
        }
        return "";
    }
}
