package io.github.quickmsg.common.ack;

import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.message.mqtt.RetryMessage;
import io.netty.util.HashedWheelTimer;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author luxurong
 */
public class TimeAckManager extends HashedWheelTimer implements AckManager {

    private final  int retrySize;

    private final  int retryPeriod;

    private final Map<Integer,Map<Long, Ack>> ackMap = new ConcurrentHashMap<>();

    public TimeAckManager(long tickDuration, TimeUnit unit, int ticksPerWheel, int retrySize, int retryPeriod) {
        super( tickDuration, unit, ticksPerWheel);
        this.retrySize = retrySize;
        this.retryPeriod = retryPeriod;
    }

    @Override
    public void addAck(Ack ack) {
        Map<Long, Ack> ackCache = ackMap.computeIfAbsent(ack.getChannelId(),id->new ConcurrentHashMap<>());
        ackCache.put(ack.getId(),ack);
        this.newTimeout(ack,ack.getTimed(),ack.getTimeUnit());
    }

    @Override
    public Ack getAck(int channelId,Long id) {
        return  Optional.ofNullable(ackMap.get(channelId))
               .map(cache->cache.get(id))
               .orElse(null);
    }

    @Override
    public void deleteAck(int channelId,Long id) {
        Optional.ofNullable(ackMap.get(channelId))
                        .ifPresent(longAckMap -> longAckMap.remove(id));
    }

    @Override
    public void doRetry(long id,  RetryMessage retrymessage) {
        RetryAck retryAck = new RetryAck(retrymessage.getMqttChannel().getId(),id, retrySize, retryPeriod,() -> {
            ContextHolder.getReceiveContext().getProtocolAdaptor().chooseProtocol(retrymessage);
        }, this);
        retryAck.start();
    }

}
