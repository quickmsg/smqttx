package io.github.quickmsg.common.event;

import io.github.quickmsg.common.event.acceptor.MessageEvent;

/**
 * @author luxurong
 * @date 2021/11/14 15:16
 * @description
 */
public class NoneEvent extends MessageEvent {


    private NoneEvent() {

    }
    public final static NoneEvent INSTANCE = new NoneEvent();

    @Override
    public String getType() {
        return null;
    }

    @Override
    public long getTimestamp() {
        return 0;
    }
}
