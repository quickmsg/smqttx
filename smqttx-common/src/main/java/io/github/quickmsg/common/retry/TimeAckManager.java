package io.github.quickmsg.common.retry;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.mqtt.RetryMessage;
import io.netty.util.HashedWheelTimer;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author luxurong
 */
public class TimeAckManager extends HashedWheelTimer implements RetryManager {

    private final  int retrySize;

    private final  int retryPeriod;

    private final Map<MqttChannel,Map<Integer, RetryTask>> retryMap = new ConcurrentHashMap<>();

    public TimeAckManager(long tickDuration, TimeUnit unit, int ticksPerWheel, int retrySize, int retryPeriod) {
        super( tickDuration, unit, ticksPerWheel);
        this.retrySize = retrySize;
        this.retryPeriod = retryPeriod;
    }


    @Override
    public void doRetry(MqttChannel mqttChannel, RetryMessage retrymessage) {
        RetryTask retryTask = new RetryTask(retrymessage,retrySize,retryPeriod);
        retryTask.setTimeout(this.newTimeout(retryTask,retryPeriod,TimeUnit.SECONDS));
        Map<Integer, RetryTask> retryTaskMap =retryMap.computeIfAbsent(mqttChannel,channel->new ConcurrentHashMap<>());
        retryTaskMap.put(retrymessage.getMessageId(),retryTask);
    }

    @Override
    public void cancelRetry(MqttChannel mqttChannel, int messageId) {
        Optional.ofNullable(retryMap.get(mqttChannel))
                .flatMap(retryMap -> Optional.ofNullable(retryMap.remove(messageId)))
                .ifPresent(RetryTask::cancel);
    }

    @Override
    public void clearRetry(MqttChannel mqttChannel) {
        Optional.ofNullable(retryMap.remove(mqttChannel))
                .ifPresent(retryMap-> retryMap.values().forEach(RetryTask::cancel));
    }
}
