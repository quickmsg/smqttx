package io.github.quickmsg.common.integrate.ack;

import io.netty.util.HashedWheelTimer;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author luxurong
 */
public class TimeWholeAckManager extends HashedWheelTimer implements AckManager {

    public TimeWholeAckManager(ThreadFactory threadFactory, long tickDuration, TimeUnit unit, int ticksPerWheel, boolean leakDetection, long maxPendingTimeouts) {
        super(threadFactory, tickDuration, unit, ticksPerWheel, leakDetection, maxPendingTimeouts);
    }
}
