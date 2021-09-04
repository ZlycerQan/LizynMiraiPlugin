package org.zlycerqan.mirai.lizyn.core.service;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfo;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.LoadConfigErrorException;
import org.zlycerqan.mirai.lizyn.core.service.exceptions.ServiceNotStartException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public abstract class SimpleService implements Service {

    public ServiceStatus serviceStatus = ServiceStatus.SUCCESS;

    private final Map<Class<?>, Map<String, Consumer<Event>>> commands = new ConcurrentHashMap<>();

    private final DatabaseManager databaseManager;

    private final ExecutorManager executorManager;

    private final MiraiLogger logger;

    public SimpleService(Map<?, ?> config, MiraiLogger miraiLogger, DatabaseManager databaseManager, ExecutorManager executorManager) throws ServiceNotStartException {
        this.databaseManager = databaseManager;
        this.executorManager = executorManager;
        this.logger = miraiLogger;
        try {
            String start = (String) config.get("if_start");
            if (start != null && start.equals("false")) {
                serviceStatus = ServiceStatus.NOT_START;
                throw new ServiceNotStartException();
            }
            loadConfig(config);
        } catch (LoadConfigErrorException e) {
            logger.error(e.getMessage());
            serviceStatus = ServiceStatus.ERROR;
        }
        loadCommand();
    }

    public abstract String getServiceName();

    public abstract void loadConfig(Map<?, ?> config) throws LoadConfigErrorException;

    public abstract void loadCommand();

    public abstract String getCommand(Event event);

    // TODO add Hot Swap service function

//    public void onEnable() {
//        serviceStatus = ServiceStatus.SUCCESS;
//    }
//
//    public void onDisable() {
//        serviceStatus = ServiceStatus.STOP;
//    }
//
//    public void onRestart() {
//
//    }


    public final MiraiLogger getLogger() {
        return logger;
    }

    public final DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public final ExecutorManager getExecutorManager() {
        return executorManager;
    }

    public final Connection getDatabaseConnection(DatabaseInfo databaseInfo) throws SQLException {
        return databaseManager.getConnection(databaseInfo);
    }

    public final Executor getExecutor() {
        return executorManager.getExecutor();
    }

    @NotNull
    public final String decorateLog(String message) {
        return getServiceName() + ": " + message;
    }

    @NotNull
    public final ArrayList<Class<?>> getListenedEventClasses() {
        ArrayList<Class<?>> events = new ArrayList<>();
        commands.forEach((event, solvers) -> events.add(event));
        return events;
    }

    public final void addCommand(String command, Class<?> eventClass, Consumer<Event> solver) {
        commands.computeIfAbsent(eventClass, k -> new ConcurrentHashMap<>()).put(command, solver);
    }

    @Nullable
    public final Consumer<Event> solveEvent(Event event) {
        if (serviceStatus == ServiceStatus.SUCCESS) {
            Map<String, Consumer<Event>> cmd = commands.get(event.getClass());
            if (cmd != null) {
                return cmd.get(getCommand(event));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
