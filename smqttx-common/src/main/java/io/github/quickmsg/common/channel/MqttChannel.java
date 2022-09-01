package io.github.quickmsg.common.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.context.ContextHolder;
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

    private String clientId;


    private Connection connection;

    private long activeTime;

    private String authTime;

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
        mqttChannel.setAddress(connection.address().toString().substring(1));
        mqttChannel.setId((int) ContextHolder.getReceiveContext().getIntegrate().getGlobalCounter("channel-id").incrementAndGet());
        connection.onReadIdle(2000, connection::dispose);
        return mqttChannel;
    }


    public void close() {
        if (!this.connection.isDisposed()) {
            this.connection.dispose();
        }
    }

    public void registryClose(Consumer<MqttChannel> consumer) {
        this.connection.onDispose(() -> consumer.accept(this));
    }


    public int generateMessageId() {
        return atomicInteger.incrementAndGet();
    }


    @Data
    public static class Auth {

        private String username;

        private byte[] password;


    }

    @Data
    @Builder
    public static class Will {

        private boolean isRetain;

        private String willTopic;

        private MqttQoS mqttQoS;

        private byte[] willMessage;

        public PublishMessage toPublishMessage() {
            PublishMessage publishMessage = new PublishMessage();
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
                this.write(message.buildMqttMessage(mqttQoS, 0));
                break;
            case EXACTLY_ONCE:
            case AT_LEAST_ONCE:
            default:
                int messageId = this.generateMessageId();
                RetryMessage retryMessage = new RetryMessage(messageId, System.currentTimeMillis(), message.isRetain(), message.getTopic(), MqttQoS.valueOf(message.getQos()), message.getBody(),this);
                ContextHolder.getReceiveContext().getRetryManager().doRetry(this, retryMessage);
                this.write(message.buildMqttMessage(mqttQoS, messageId));
                break;
        }
    }


    /**
     * write message
     *
     * @param mqttMessage #{@link MqttMessage}
     * @return mono
     */
    public void write(MqttMessage mqttMessage) {
        if (this.connection.channel().isActive() && this.connection.channel().isWritable()) {
            connection.outbound().sendObject(Mono.just(mqttMessage)).then().subscribe();
        }
    }


}
