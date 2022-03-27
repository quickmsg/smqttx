package io.github.quickmsg.common.ack;

import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.message.mqtt.RetryMessage;
import io.netty.util.HashedWheelTimer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author luxurong
 */
public class TimeAckManager extends HashedWheelTimer implements AckManager {

    private final  int retrySize;

    private final  int retryPeriod;

    private final Map<Long, Ack> ackMap = new ConcurrentHashMap<>();

    public TimeAckManager(long tickDuration, TimeUnit unit, int ticksPerWheel, int retrySize, int retryPeriod) {
        super( tickDuration, unit, ticksPerWheel);
        this.retrySize = retrySize;
        this.retryPeriod = retryPeriod;
    }

    @Override
    public void addAck(Ack ack) {
        ackMap.put(ack.getId(),ack);
        this.newTimeout(ack,ack.getTimed(),ack.getTimeUnit());
    }

    @Override
    public Ack getAck(Long id) {
        return ackMap.get(id);
    }

    @Override
    public void deleteAck(Long id) {
        ackMap.remove(id);
    }

    @Override
    public void doRetry(long id,  RetryMessage retrymessage) {
        RetryAck retryAck = new RetryAck(id, retrySize, retryPeriod, () -> {
            ContextHolder.getReceiveContext().getProtocolAdaptor().chooseProtocol(retrymessage);
        }, this);
        retryAck.start();
    }

}
