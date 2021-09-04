package org.zlycerqan.mirai.lizyn.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DatabaseManager {

    private final Map<DatabaseInfo, ArrayList<Connection>> pool = new ConcurrentHashMap<>();
    private final Map<DatabaseInfo, Integer> poolPos = new ConcurrentHashMap<>();
    private final int maxConnectionNumber;


    public DatabaseManager(int maxConnectionNumber) {
        this.maxConnectionNumber = maxConnectionNumber;
    }

    public synchronized Connection getConnection(DatabaseInfo databaseInfo) throws SQLException {
        ArrayList<Connection> list = pool.computeIfAbsent(databaseInfo, k -> new ArrayList<>());
        Connection connection;
        poolPos.putIfAbsent(databaseInfo, 0);
        if (list.size() < maxConnectionNumber) {
            try {
                Class.forName(databaseInfo.getDriverName());
        } catch (ClassNotFoundException ignored) { }
        connection = DriverManager.getConnection(databaseInfo.getUrl(), databaseInfo.getUser(), databaseInfo.getPassword());
        list.add(connection);
    } else {
        int pos = poolPos.get(databaseInfo);
        connection = list.get(pos);
        if (!connection.isValid(2000)) {
                connection.close();
                connection = DriverManager.getConnection(databaseInfo.getUrl(), databaseInfo.getUser(), databaseInfo.getPassword());
                list.set(pos, connection);
            }
            poolPos.put(databaseInfo, (pos + 1) % list.size());
        }
        return connection;
    }

    public void close() throws SQLException {
        for (Map.Entry<DatabaseInfo, ArrayList<Connection>> entry : pool.entrySet()) {
            for (Connection connection : entry.getValue()) {
                connection.close();
            }
        }
    }
}
