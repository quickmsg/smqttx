package io.github.quickmsg.common.log;

/**
 * @author luxurong
 * @date 2021/11/5 16:54
 * @description
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
