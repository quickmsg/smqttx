package io.github.quickmsg.common.event;

import io.github.quickmsg.common.event.acceptor.MessageEvent;

/**
 * @author luxurong
 */
public class NoneEvent extends MessageEvent {


    private NoneEvent() {

    }
    public final static NoneEvent INSTANCE = new NoneEvent();

    @Override
    public long getTimestamp() {
        return 0;
    }
}
