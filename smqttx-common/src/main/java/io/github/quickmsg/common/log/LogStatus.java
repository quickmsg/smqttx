package io.github.quickmsg.common.log;

/**
 * @author luxurong
 */
public enum LogStatus {
    SUCCESS("success"),

    FAILED("failed"),

    ERROR("error"),
    ;




    private final String name;

    LogStatus(String name) {
        this.name = name;
    }
}
