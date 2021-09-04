package org.zlycerqan.mirai.lizyn.core.database;

import java.util.Objects;

public final class DatabaseInfo {

    private final String driverName;
    private final String url;
    private final String user;
    private final String password;

    DatabaseInfo(String driverName, String url, String user, String password) {
        this.driverName = driverName;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseInfo that = (DatabaseInfo) o;
        return Objects.equals(driverName, that.driverName) && Objects.equals(url, that.url) && Objects.equals(user, that.user) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverName, url, user, password);
    }

    @Override
    public String toString() {
        return "DatabaseInfo{" +
                "driverName='" + driverName + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getDriverName() {
        return driverName;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
