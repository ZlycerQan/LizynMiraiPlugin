package org.zlycerqan.mirai.lizyn.core.service.exceptions;

public class LoadConfigErrorException extends Exception {

    private final String reason;

    public LoadConfigErrorException(String reason) {
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return reason;
    }
}
