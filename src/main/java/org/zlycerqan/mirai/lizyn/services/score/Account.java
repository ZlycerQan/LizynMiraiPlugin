package org.zlycerqan.mirai.lizyn.services.score;

public class Account {

    private final String id;

    private final String password;

    public Account(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

}
