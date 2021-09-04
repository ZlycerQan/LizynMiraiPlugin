package org.zlycerqan.mirai.lizyn;

import com.esotericsoftware.yamlbeans.YamlException;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;
import org.zlycerqan.mirai.lizyn.core.executor.ExecutorManager;
import org.zlycerqan.mirai.lizyn.core.utils.ConfigUtils;
import org.zlycerqan.mirai.lizyn.services.BotEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public final class LizynMiraiPlugin extends JavaPlugin {

    public static final LizynMiraiPlugin INSTANCE = new LizynMiraiPlugin();

    private LizynMiraiPlugin() {
        super(new JvmPluginDescriptionBuilder(PluginData.id, PluginData.version)
                .name(PluginData.name)
                .info(PluginData.information)
                .author(PluginData.author)
                .build());
    }

    private DatabaseManager databaseManager;
    private ExecutorManager executorManager;
    private Map<?, ?> config;

    void initDirAndConfig() throws IOException {
        File dir = new File(PluginData.name);
        if (!dir.exists()) {
            dir.mkdir();
        }
        dir = new File(PluginData.cachePath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        dir = new File(PluginData.configPath);
        if (!dir.exists()) {
            dir.createNewFile();
        }
    }

    void init() throws IOException {
        initDirAndConfig();
        config = ConfigUtils.getConfigFromFile(PluginData.configPath);
        Map<?, ?> systemConfig = (Map<?, ?>) config.get("SystemConfig");
        String qq = (String) systemConfig.get("bot");
        if (qq != null) {
            config.forEach((key, value) -> {
                Map<String, Object> res = (Map<String, Object>) value;
                res.putIfAbsent("bot", qq);
            });
        } else {
            getLogger().error("bot cannot be null");
        }
        try {
            String threadNumber =  (String) systemConfig.get("threads");
            if (threadNumber != null) {
                executorManager = new ExecutorManager(Integer.parseInt(threadNumber));
            }
        } catch (NumberFormatException e) {
            getLogger().error(e.getMessage());
        }
        try {
            String maxDatabaseConnectionNumber = (String) systemConfig.get("max_db_connections");
            if (maxDatabaseConnectionNumber != null) {
                databaseManager = new DatabaseManager(Integer.parseInt(maxDatabaseConnectionNumber));
            }
        } catch (NumberFormatException e) {
            getLogger().error(e.getMessage());
        }
        if (executorManager == null) {
            executorManager = new ExecutorManager(10);
        }
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(3);
        }
    }

    @Override
    public void onEnable() {
        try {
            init();
            GlobalEventChannel.INSTANCE.registerListenerHost(new BotEventListener(config, getLogger(), databaseManager, executorManager));
            getLogger().info("Plugin loaded!");
        } catch (IOException e) {
            getLogger().error(e.getMessage());
            getLogger().error("Plugin failed to load! T_T");
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        try {
            databaseManager.close();
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
        }
    }
}
