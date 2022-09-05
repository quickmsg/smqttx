package io.github.quickmsg.common.log;

/**
 * @author luxurong
 */
public enum loggerEvent {

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

    loggerEvent(String name) {
        this.name = name;
    }
}
