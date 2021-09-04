package org.zlycerqan.mirai.lizyn.services;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.service.Service;
import org.zlycerqan.mirai.lizyn.core.service.SimpleService;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.ServiceNotStartException;
import org.zlycerqan.mirai.lizyn.core.utils.ConfigUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class BotEventListener extends SimpleListenerHost {

    private static final ArrayList<Class<?>> SERVICES = new ArrayList<>();
    private static final Map<Class<?>, ArrayList<Object>> EVENT_SOLVERS = new HashMap<>();
    private static final Map<Class<?>, Object> serviceInstances = new HashMap<>();
    private void gatherService() {
        Reflections reflections = new Reflections("org.zlycerqan.mirai.lizyn.services");
        SERVICES.addAll(reflections.getSubTypesOf(SimpleService.class));
    }

    public BotEventListener(Map<?, ?> config, MiraiLogger miraiLogger, DatabaseManager databaseManager, ExecutorManager executorManager) {
        gatherService();
        for (Class<?> service : SERVICES) {
            try {
                serviceInstances.put(service, service.getConstructor(Map.class, MiraiLogger.class, DatabaseManager.class, ExecutorManager.class).newInstance(ConfigUtils.getConfigFromAll(config, (String) service.getField("SERVICE_NAME").get(String.class)), miraiLogger, databaseManager, executorManager));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | NoSuchFieldException e) {
                miraiLogger.error(e.getMessage());
            } catch (InvocationTargetException e) {
                if (!(e.getTargetException() instanceof ServiceNotStartException)) {
                    miraiLogger.error(e.getMessage());
                }
            }
        }
        serviceInstances.forEach((service, instance) -> ((Service) instance).getListenedEventClasses().forEach(event -> EVENT_SOLVERS.computeIfAbsent(event, k -> new ArrayList<>()).add(instance)));
    }

    @EventHandler
    public void onMessageEvent(@NotNull MessageEvent event) {
        ArrayList<Object> list = EVENT_SOLVERS.get(event.getClass());
        if (list != null) {
            for (Object service : list) {
                Consumer<Event> consumer = ((Service) service).solveEvent(event);
                if (consumer != null) {
                    consumer.accept(event);
                    return ;
                }
            }
        }
    }

    @EventHandler
    public void onNewFriendRequestEvent(@NotNull NewFriendRequestEvent event) {
        event.accept();
    }

    @EventHandler
    public void onBotInvitedJoinGroupRequestEvent(@NotNull BotInvitedJoinGroupRequestEvent event) {
        event.accept();
    }
}
