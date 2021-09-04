package org.zlycerqan.mirai.lizyn.services.libduty;

import net.mamoe.mirai.utils.MiraiLogger;
import org.jetbrains.annotations.NotNull;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseInfo;
import org.zlycerqan.mirai.lizyn.core.database.DatabaseManager;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LibDutyDatabase {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private final DatabaseManager databaseManager;
    private final DatabaseInfo databaseInfo;

    public LibDutyDatabase(DatabaseInfo databaseInfo, @NotNull DatabaseManager databaseManager, MiraiLogger miraiLogger) {
        this.databaseManager = databaseManager;
        this.databaseInfo = databaseInfo;
        try {
            Connection connection = databaseManager.getConnection(databaseInfo);
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS libduty_info(friend_id BIGINT UNSIGNED PRIMARY KEY, name CHAR(10), day TINYINT);");
            statement.execute("CREATE TABLE IF NOT EXISTS libduty_history(hid INT AUTO_INCREMENT PRIMARY KEY, friend_id BIGINT UNSIGNED, date DATETIME);");
            statement.close();
        } catch (SQLException e) {
            miraiLogger.error(e.getMessage());
        }
    }

    public synchronized ArrayList<String> getName(int day) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM libduty_info WHERE day = ?;");
        preparedStatement.setInt(1, day);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<String> arrayList = new ArrayList<>();
        for (; resultSet.next(); ) {
            arrayList.add(resultSet.getString(1));
        }
        resultSet.close();
        preparedStatement.close();
        return arrayList;
    }

    public synchronized ArrayList<Long> getId(int day) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT friend_id FROM libduty_info WHERE day = ?;");
        preparedStatement.setInt(1, day);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<Long> arrayList = new ArrayList<>();
        for (; resultSet.next(); ) {
            arrayList.add(resultSet.getLong(1));
        }
        resultSet.close();
        preparedStatement.close();
        return arrayList;
    }

    public synchronized boolean checkFriend(long friend_id, int day) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT friend_id FROM libduty_info WHERE day = ? AND friend_id = ?;");
        preparedStatement.setInt(1, day);
        preparedStatement.setLong(2, friend_id);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean result = resultSet.next();
        resultSet.close();
        preparedStatement.close();
        return result;
    }

    public synchronized boolean checkIfFinish(long friend_id, Date from, Date to) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT friend_id FROM libduty_history WHERE date >= ? AND date <= ? AND friend_id = ?;");
        preparedStatement.setString(1, simpleDateFormat.format(from));
        preparedStatement.setString(2, simpleDateFormat.format(to));
        preparedStatement.setLong(3, friend_id);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean result = resultSet.next();
        resultSet.close();
        preparedStatement.close();
        return result;
    }

    public synchronized void addHistory(long friend_id) throws SQLException {
        Connection connection = databaseManager.getConnection(databaseInfo);
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO libduty_history(friend_id, date) VALUES(?, ?);");
        preparedStatement.setLong(1, friend_id);
        preparedStatement.setString(2, simpleDateFormat.format(new Date()));
        preparedStatement.execute();
        preparedStatement.close();
    }

    public synchronized boolean checkIfFinishByDate(Date date) throws SQLException {
        Date from = new Date();
        from.setTime(date.getTime() - 24L * 60L * 60L * 1000L);
        boolean isFinish = false;
        ArrayList<Long> ids = this.getId(LibDutyUtils.getCurrentDayOfWeek());
        if (ids.size() == 0) {
            return true;
        }
        for (long id : ids) {
            if (this.checkIfFinish(id, from, date)) {
                isFinish = true;
                break;
            }
        }
        return isFinish;
    }
}
