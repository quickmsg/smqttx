package io.github.quickmsg.common.handler;

import io.netty.handler.traffic.AbstractTrafficShapingHandler;

/**
 * @author luxurong
 */
public interface TrafficHandlerLoader {

    /**
     * return TrafficHandlerLoader
     *
     * @return {@link AbstractTrafficShapingHandler}
     */
    AbstractTrafficShapingHandler get();
}
