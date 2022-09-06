package io.github.quickmsg.common.log;

import lombok.Getter;

/**
 * @author luxurong
 */
@Getter
public enum LogStatus {
    SUCCESS("success"),

    FAILED("failed"),
    ;




    private final String name;

    LogStatus(String name) {
        this.name = name;
    }
}
