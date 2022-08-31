package io.github.quickmsg.common.log;

/**
 * @author luxurong
 */
public enum loggerEvent {

    READ("read"),
    WRITE("write"),
    CLUSTER("cluster"),
    MESSAGE("message"),
    ;


    private final String name;

    loggerEvent(String name) {
        this.name = name;
    }
}
