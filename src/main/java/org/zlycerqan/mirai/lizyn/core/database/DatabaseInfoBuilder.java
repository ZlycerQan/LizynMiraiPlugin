package org.zlycerqan.mirai.lizyn.core.database;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseInfoBuilder {

    public enum DatabaseType {
        MYSQL,
        SQLSERVER
    }

    private static final Map<DatabaseType, String> databaseTypeNames = new ConcurrentHashMap<>();
    private static final Map<DatabaseType, String> driverNames = new ConcurrentHashMap<>();

    static {
        driverNames.put(DatabaseType.MYSQL, "com.mysql.cj.jdbc.Driver");
        driverNames.put(DatabaseType.SQLSERVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver");

        databaseTypeNames.put(DatabaseType.MYSQL, "mysql");
        databaseTypeNames.put(DatabaseType.SQLSERVER, "sqlserver");
    }

    @NotNull
    @Contract(pure = true)
    private static String makeDatabaseUrl(DatabaseType type, String host, int port, String databaseName) {
        return "jdbc:" + databaseTypeNames.get(type) + "://"
                + host + ":"
                + port + "/"
                + databaseName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    @NotNull
    @Contract("_, _, _, _, _, _ -> new")
    public static DatabaseInfo buildDatabaseInfo(@NotNull DatabaseType type, String host, int port, String databaseName, String user, String password) {
        return new DatabaseInfo(driverNames.get(type), makeDatabaseUrl(type, host, port, databaseName), user, password);
    }


    public static DatabaseInfo buildDatabaseInfoFromConfig(@NotNull Map<?, ?> config) throws UnsupportedDatabaseTypeException {
        String type = (String) config.get("type");
        if (type == null) {
            return null;
        }
        String host = (String) config.get("host");
        String port = (String) config.get("port");
        String dbName = (String) config.get("db_name");
        String user = (String) config.get("user");
        String password = (String) config.get("password");
        if (host == null || port == null || dbName == null || user == null || password == null) {
            return null;
        }
        switch (type.toLowerCase(Locale.ROOT)) {
            case "mysql":
                return buildDatabaseInfo(DatabaseType.MYSQL, host, Integer.parseInt(port), dbName, user, password);
            case "sqlserver":
                return buildDatabaseInfo(DatabaseType.SQLSERVER, host, Integer.parseInt(port), dbName, user, password);
            default:
                throw new UnsupportedDatabaseTypeException(type);
        }
    }
}