package org.zlycerqan.mirai.lizyn.services.myosotis;

import net.mamoe.mirai.utils.MiraiLogger;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfo;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyosotisDatabase {

    private final int defaultCoolDown;

    private final SimpleDateFormat toDateSimpleDateFormat;

    private final SimpleDateFormat toStringSimpleDateFormat;

    private final DatabaseManager databaseManager;

    private final DatabaseInfo databaseInfo;

    public MyosotisDatabase(DatabaseInfo databaseInfo, DatabaseManager databaseManager, MiraiLogger miraiLogger, int defaultCoolDown) {
        this.databaseManager = databaseManager;
        this.databaseInfo = databaseInfo;
        this.toDateSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.toStringSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        this.defaultCoolDown = defaultCoolDown;
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS myosotis_group(group_id BIGINT UNSIGNED PRIMARY KEY);");
            statement.execute("CREATE TABLE IF NOT EXISTS myosotis_cool_down(friend_id BIGINT UNSIGNED PRIMARY KEY, last DATETIME, cool_down INT);");
            statement.close();
        } catch (SQLException e) {
            miraiLogger.error(e.getMessage());
        }
    }

    public boolean checkGroup(long groupId) {
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT group_id FROM myosotis_group;");
            for (; resultSet.next(); ) {
                if (resultSet.getLong(1) == groupId) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean checkCoolDown(long friendId) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT last, cool_down FROM myosotis_cool_down WHERE friend_id = ?;");
        preparedStatement.setLong(1, friendId);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean result;
        if (resultSet.next()) {
            String text = resultSet.getString(1);
            int coolDown = resultSet.getInt(2);
            Date date = null;
            try {
                date = toDateSimpleDateFormat.parse(text);
            } catch (ParseException ignored) {
            }
            if (date == null) {
                result = true;
            } else {
                Date now = new Date();
                result = now.getTime() - date.getTime() >= coolDown * 1000L;
            }
        } else {
            createCoolDown(friendId);
            result = true;
        }
        resultSet.close();
        preparedStatement.close();
        return result;
    }

    public void updateCoolDown(long friendId, Date date) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE myosotis_cool_down SET last = ? WHERE friend_id = ?;");
        preparedStatement.setString(1, toStringSimpleDateFormat.format(date));
        preparedStatement.setLong(2, friendId);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public void createCoolDown(long friendId) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO myosotis_cool_down(friend_id, last, cool_down) VALUES(?, ?, ?);");
        preparedStatement.setLong(1, friendId);
        preparedStatement.setString(2, toStringSimpleDateFormat.format(new Date()));
        preparedStatement.setInt(3, defaultCoolDown);
        preparedStatement.execute();
        preparedStatement.close();
    }
}
