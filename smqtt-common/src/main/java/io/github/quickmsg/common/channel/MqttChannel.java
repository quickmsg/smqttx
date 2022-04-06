package io.github.quickmsg.common.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.enums.ChannelStatus;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.message.mqtt.ConnectMessage;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.message.mqtt.RetryMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author luxurong
 */
@Getter
@Setter
@Slf4j
public class MqttChannel {

    private Integer id;

    private Connection connection;

    private ChannelStatus status;

    private long activeTime;

    private long authTime;

    private ConnectMessage connectMessage;

    private String address;

    @JsonIgnore
    private Set<SubscribeTopic> topics;

    @JsonIgnore
    private transient AtomicInteger atomicInteger;

    public static MqttChannel init(Connection connection) {
        MqttChannel mqttChannel = new MqttChannel();
        mqttChannel.setTopics(new CopyOnWriteArraySet<>());
        mqttChannel.setAtomicInteger(new AtomicInteger(0));
        mqttChannel.setActiveTime(System.currentTimeMillis());
        mqttChannel.setConnection(connection);
        mqttChannel.setStatus(ChannelStatus.INIT);
        mqttChannel.setAddress(connection.address().toString());
        mqttChannel.setId((int) ContextHolder.getReceiveContext().getIntegrate().getGlobalCounter("channel-id").incrementAndGet());
        connection.onReadIdle(2000, connection::dispose);
        return mqttChannel;
    }


    public Mono<Void> close() {
        return Mono.fromRunnable(() -> {
            if (this.connectMessage.isCleanSession()) {
                this.topics.clear();
            }
            if (!this.connection.isDisposed()) {
                this.connection.dispose();
            }
        });
    }

    public void registryClose(Consumer<MqttChannel> consumer) {
        this.connection.onDispose(() -> consumer.accept(this));
    }


    public int generateMessageId() {
        return atomicInteger.incrementAndGet();
    }

    public long generateRetryId(int messageId) {
        return (long) id << 4 | messageId;
    }


    @Data
    @Builder
    public static class Will {

        private boolean isRetain;

        private String willTopic;

        private MqttQoS mqttQoS;

        private byte[] willMessage;

        public  PublishMessage toPublishMessage() {
            PublishMessage publishMessage =new PublishMessage();
            publishMessage.setBody(this.willMessage);
            publishMessage.setTopic(this.willTopic);
            publishMessage.setRetain(this.isRetain);
            publishMessage.setQos(this.mqttQoS.value());
            return publishMessage;
        }


    }

    public void sendPublish(MqttQoS mqttQoS, PublishMessage message) {
        switch (mqttQoS) {
            case AT_MOST_ONCE:
                this.write(message.buildMqttMessage(mqttQoS, 0)).subscribe();
                break;
            case EXACTLY_ONCE:
            case AT_LEAST_ONCE:
            default:
                int messageId = this.generateMessageId();
                RetryMessage retryMessage = new RetryMessage(messageId, System.currentTimeMillis(), message.isRetain(), message.getTopic(), MqttQoS.valueOf(message.getQos()), message.getBody(), this, ContextHolder.getReceiveContext());
                ContextHolder.getReceiveContext().getAckManager().doRetry(messageId, retryMessage);
                this.write(message.buildMqttMessage(mqttQoS, messageId)).subscribe();
                break;
        }
    }



    /**
     * write message
     *
     * @param mqttMessage #{@link MqttMessage}
     * @return mono
     */
    public Mono<Void> write(MqttMessage mqttMessage) {
        if (this.connection.channel().isActive() && this.connection.channel().isWritable()) {
            return connection.outbound().sendObject(Mono.just(mqttMessage)).then();
        } else {
            return Mono.empty();
        }
    }


}
