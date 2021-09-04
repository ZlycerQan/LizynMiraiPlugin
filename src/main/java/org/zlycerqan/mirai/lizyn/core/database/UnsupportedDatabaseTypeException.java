package org.zlycerqan.mirai.lizyn.core.database;

public class UnsupportedDatabaseTypeException extends Exception {

    private String name;
    public UnsupportedDatabaseTypeException(String name) {
        this.name = name;
    }
    @Override
    public String getMessage() {
        return "Unsupported database type: " + name;
    }
}
