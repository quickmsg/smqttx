package io.github.quickmsg.common.log;

import lombok.Getter;

/**
 * @author luxurong
 */

@Getter
public enum LogEvent {

    PUBLISH("PUBLISH"),
    WRITE("WRITE"),
    CLUSTER("CLUSTER"),
    CONNECT("CONNECT"),
    CLOSE("CLOSE"),
    SUBSCRIBE("SUBSCRIBE"),
    UNSUBSCRIBE("UNSUBSCRIBE"),
    BRIDGE("BRIDGE"),
    DISCONNECT("DISCONNECT"),
    RETRY("RETRY"),

    ;


    private final String name;

    LogEvent(String name) {
        this.name = name;
    }
}
