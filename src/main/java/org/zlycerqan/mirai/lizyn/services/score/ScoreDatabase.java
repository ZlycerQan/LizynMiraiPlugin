package org.zlycerqan.mirai.lizyn.services.score;

import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfo;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoreDatabase {

    private final DatabaseManager databaseManager;

    private final DatabaseInfo databaseInfo;

    private final SimpleDateFormat simpleDateFormat;

    public ScoreDatabase(DatabaseInfo databaseInfo, @NotNull DatabaseManager databaseManager) throws SQLException {
        this.databaseInfo = databaseInfo;
        this.databaseManager = databaseManager;
        this.simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Connection connection = databaseManager.getConnection(databaseInfo);
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS score_account(friend_id BIGINT UNSIGNED PRIMARY KEY, id CHAR(10), password CHAR(50));");
        statement.execute("CREATE TABLE IF NOT EXISTS score_history(hid INT UNSIGNED AUTO_INCREMENT PRIMARY KEY, friend_id BIGINT UNSIGNED, status TINYINT, date DATETIME);");
    }

    public synchronized Account getAccount(long friendId) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, password FROM score_account WHERE friend_id = ?;");
        preparedStatement.setLong(1, friendId);
        ResultSet resultSet = preparedStatement.executeQuery();
        String id = null, password = null;
        for (; resultSet.next(); ) {
            id = resultSet.getString(1);
            password = resultSet.getString(2);
        }
        resultSet.close();
        preparedStatement.close();
        if (id == null) {
            return null;
        } else {
            return new Account(id, password);
        }
    }

    public synchronized void addHistory(long friendId, int status) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO score_history(friend_id, status, date) VALUES(?, ?, ?);");
        preparedStatement.setLong(1, friendId);
        preparedStatement.setInt(2, status);
        preparedStatement.setString(3, simpleDateFormat.format(new Date()));
        preparedStatement.execute();
        preparedStatement.close();
    }

    public synchronized void addAccount(long friendId, String id, String password) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO score_account(friend_id, id, password) VALUES(?, ?, ?);");
        preparedStatement.setLong(1, friendId);
        preparedStatement.setString(2, id);
        preparedStatement.setString(3, password);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public synchronized void updateAccount(long friendId, String id, String password) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE score_account SET id = ?, password = ? WHERE friend_id = ?;");
        preparedStatement.setString(1, id);
        preparedStatement.setString(2, password);
        preparedStatement.setLong(3, friendId);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public synchronized void deleteAccount(long friendId) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM score_account WHERE friend_id = ?;");
        preparedStatement.setLong(1, friendId);
        preparedStatement.execute();
        preparedStatement.close();
    }

}
