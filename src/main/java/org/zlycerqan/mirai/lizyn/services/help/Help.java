package org.zlycerqan.mirai.lizyn.services.help;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.service.SimpleService;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.LoadConfigErrorException;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.ServiceNotStartException;

import java.util.Map;

public class Help extends SimpleService {

    public static String SERVICE_NAME = "Help";

    public Help(Map<?, ?> config, MiraiLogger miraiLogger, DatabaseManager databaseManager, ExecutorManager executorManager) throws ServiceNotStartException {
        super(config, miraiLogger, databaseManager, executorManager);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    private String helpFilePath;

    @Override
    public void loadConfig(Map<?, ?> config) throws LoadConfigErrorException {
        helpFilePath = (String) config.get("help_file_path");
    }

    @Override
    public void loadCommand() {
        addCommand(org.zlycerqan.mirai.lizyn.services.help.commands.Help.COMMAND, FriendMessageEvent.class, org.zlycerqan.mirai.lizyn.services.help.commands.Help.makeFriendMessageEventCommand(getExecutorManager(), helpFilePath));
    }

    @Override
    public String getCommand(Event event) {
        if (event.getClass().equals(FriendMessageEvent.class)) {
            if (((FriendMessageEvent) event).getMessage().contentToString().equals(org.zlycerqan.mirai.lizyn.services.help.commands.Help.COMMAND) && helpFilePath != null) {
                return org.zlycerqan.mirai.lizyn.services.help.commands.Help.COMMAND;
            }
        }
        return "";
    }
}
