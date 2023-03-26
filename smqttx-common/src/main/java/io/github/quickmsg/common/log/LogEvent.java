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
    PING("PING"),

    PUBLISH_ACK("PUBLISH_ACK"),

    RETRY("RETRY"),

    HEART_TIMEOUT("HEART_TIMEOUT"),

    SYSTEM("SYSTEM"),

    PUBLISH_REC("PUBLISH_REC"),

    PUBLISH_REL("PUBLISH_REL"),

    PUBLISH_COMP("PUBLISH_COMP"),

    ;


    private final String name;

    LogEvent(String name) {
        this.name = name;
    }
}
