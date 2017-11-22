package org.fh.gae.net.error;

public enum ErrCode {
    SUCCESS(0, "success"),

    NO_AUTH(1001, "authentication info requreid"),
    BLOCKED(1002, "blocked"),
    NONE_EXIST(1003, "none exist"),
    INVALID_TOKEN(1004, "invalid token"),
    SERVER_BUSY(1005, "server is busy"),
    SERVER_ERROR(1006, "system error"),

    INVALID_ARG(2004, "invalid args");

    private int code;
    private String msg;

    ErrCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return this.code;
    }

    public String msg() {
        return this.msg;
    }
}
