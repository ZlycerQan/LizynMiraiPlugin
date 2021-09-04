package org.zlycerqan.mirai.lizyn.services.chat;

import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfo;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatDatabase {

    private final DatabaseManager databaseManager;
    private final DatabaseInfo databaseInfo;

    public ChatDatabase(DatabaseInfo databaseInfo, @NotNull DatabaseManager databaseManager, MiraiLogger miraiLogger) {
        this.databaseManager = databaseManager;
        this.databaseInfo = databaseInfo;
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS chat_permission(friend_id BIGINT UNSIGNED PRIMARY KEY, permission TINYINT);");
            statement.close();
        } catch (SQLException e) {
            miraiLogger.error(e.getMessage());
        }
    }

    public synchronized void setSuperRoot(long friendId) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement p1 = connection.prepareStatement("SELECT friend_id FROM chat_permission WHERE permission = ?;");
        p1.setInt(1, ChatPermission.SUPER_ROOT);
        ResultSet r1 = p1.executeQuery();
        if (r1.next()) {
            long target = r1.getLong(1);
            r1.close();
            p1.close();
            PreparedStatement p2 = connection.prepareStatement("DELETE FROM chat_permission WHERE friend_id = ?;");
            p2.setLong(1, target);
            p2.execute();
            p2.close();
        }
        if (!r1.isClosed()) {
            r1.close();
            p1.close();
        }
        PreparedStatement p3 = connection.prepareStatement("INSERT INTO chat_permission(friend_id, permission) VALUES(?, ?);");
        p3.setLong(1, friendId);
        p3.setInt(2, ChatPermission.SUPER_ROOT);
        p3.execute();
        p3.close();
    }

    public synchronized void addRoot(long friendId) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        int permission = getPermission(friendId);
        PreparedStatement preparedStatement;
        if (permission == ChatPermission.NOT_EXIST) {
            preparedStatement = connection.prepareStatement("INSERT INTO chat_permission(friend_id, permission) VALUES(?, ?);");
            preparedStatement.setLong(1, friendId);
            preparedStatement.setInt(2, ChatPermission.ROOT);
        } else {
            preparedStatement = connection.prepareStatement("UPDATE chat_permission SET permission = ? WHERE friend_id = ?;");
            preparedStatement.setInt(1, ChatPermission.ROOT);
            preparedStatement.setLong(2, friendId);
        }
        preparedStatement.execute();
        preparedStatement.close();    }

    public synchronized void deleteRoot(long friendId) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE chat_permission SET permission = ? WHERE friend_id = ?;");
        preparedStatement.setInt(1, ChatPermission.USER);
        preparedStatement.setLong(2, friendId);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public synchronized void addUser(long friendId) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO chat_permission(friend_id, permission) VALUES(?, ?);");
        preparedStatement.setLong(1, friendId);
        preparedStatement.setInt(2, ChatPermission.USER);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public synchronized void deleteUser(long friendId) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM chat_permission WHERE friend_id = ?;");
        preparedStatement.setLong(1, friendId);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public synchronized int getPermission(long friendId) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT permission FROM chat_permission WHERE friend_id = ?;");
        preparedStatement.setLong(1, friendId);
        ResultSet resultSet = preparedStatement.executeQuery();
        int result;
        if (resultSet.next()) {
            result = resultSet.getInt(1);
        } else {
            result = ChatPermission.NOT_EXIST;
        }
        resultSet.close();
        return result;
    }

}
