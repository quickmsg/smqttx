package io.github.quickmsg.common.event;

/**
 * @author luxurong
 */
public interface Event {

    String getType();

    Action getAction();

    long getTimestamp();
}
