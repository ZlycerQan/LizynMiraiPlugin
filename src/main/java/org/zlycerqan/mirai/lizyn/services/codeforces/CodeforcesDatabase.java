package org.zlycerqan.mirai.lizyn.services.codeforces;

import net.mamoe.mirai.utils.MiraiLogger;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfo;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;

public class CodeforcesDatabase {

    private final DatabaseManager databaseManager;
    private final DatabaseInfo databaseInfo;
    private final MiraiLogger logger;

    public CodeforcesDatabase(DatabaseInfo databaseInfo, DatabaseManager databaseManager, MiraiLogger miraiLogger) {
        this.databaseManager = databaseManager;
        this.databaseInfo = databaseInfo;
        this.logger = miraiLogger;
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS codeforces_friend_id(friend_id BIGINT UNSIGNED PRIMARY KEY);");
            statement.execute("CREATE TABLE IF NOT EXISTS codeforces_group_id(group_id BIGINT UNSIGNED PRIMARY KEY);");
            statement.execute("CREATE TABLE IF NOT EXISTS codeforces_contest_sent(contest_id INT UNSIGNED, time INT UNSIGNED);");
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public synchronized Long[] getFriendIds() {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM codeforces_friend_id;");
            ArrayList<Long> arrayList = new ArrayList<>();
            while (resultSet.next()) {
                arrayList.add(resultSet.getLong(1));
            }
            statement.close();
            return arrayList.toArray(new Long[0]);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return new Long[0];
        }
    }

    public synchronized Long[] getGroupIds() {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM codeforces_group_id;");
            ArrayList<Long> arrayList = new ArrayList<>();
            while (resultSet.next()) {
                arrayList.add(resultSet.getLong(1));
            }
            statement.close();
            return arrayList.toArray(new Long[0]);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return new Long[0];
        }
    }

    public synchronized void addSendRecord(int contestId, int time) {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO codeforces_contest_sent(contest_id, time) VALUES(?, ?);");
            preparedStatement.setInt(1, contestId);
            preparedStatement.setInt(2, time);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public synchronized void addFriend(long friendId) {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO codeforces_friend_id(friend_id) VALUES(?);");
            preparedStatement.setLong(1, friendId);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public synchronized void addGroup(long groupId) {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO codeforces_group_id(group_id) VALUES(?);");
            preparedStatement.setLong(1, groupId);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public synchronized void deleteFriend(long friendId) {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM codeforces_friend_id WHERE friend_id = ?;");
            preparedStatement.setLong(1, friendId);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public synchronized void deleteGroup(long groupId) {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM codeforces_group_id WHERE group_id = ?;");
            preparedStatement.setLong(1, groupId);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public synchronized boolean checkFriend(long friendId) {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM codeforces_friend_id WHERE friend_id = ?;");
            preparedStatement.setLong(1, friendId);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean result = resultSet.next();
            resultSet.close();
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public synchronized boolean checkGroup(long groupId) {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM codeforces_group_id WHERE group_id = ?;");
            preparedStatement.setLong(1, groupId);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean result = resultSet.next();
            resultSet.close();
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public synchronized boolean checkIsSent(int contestId, int time) {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT contest_id FROM codeforces_contest_sent WHERE contest_id = ? AND time = ?;");
            preparedStatement.setLong(1, contestId);
            preparedStatement.setLong(2, time);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean result = resultSet.next();
            resultSet.close();
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
