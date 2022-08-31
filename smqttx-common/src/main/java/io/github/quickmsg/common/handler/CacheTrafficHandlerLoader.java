package io.github.quickmsg.common.handler;

import io.netty.handler.traffic.AbstractTrafficShapingHandler;

/**
 * @author luxurong
 */
public class CacheTrafficHandlerLoader implements TrafficHandlerLoader {


    private final AbstractTrafficShapingHandler trafficShapingHandler;

    public CacheTrafficHandlerLoader(AbstractTrafficShapingHandler trafficShapingHandler) {
        this.trafficShapingHandler = trafficShapingHandler;
    }

    @Override
    public AbstractTrafficShapingHandler get() {
        return this.trafficShapingHandler;
    }
}
