package io.github.quickmsg.common.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.cache.ConnectCache;
import io.github.quickmsg.common.message.mqtt.ConnectMessage;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.message.mqtt.RetryMessage;
import io.github.quickmsg.common.retry.RetryManager;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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

    private Map<Integer,PublishMessage> qosCache = new HashMap<>();


    private Integer id;

    private String clientId;

    @JsonIgnore
    private static final int MAX_MESSAGE_ID = 65535;


    @JsonIgnore
    private Connection connection;

    private long activeTime;

    private String authTime;

    private ConnectCache connectCache;

    private String address;

    @JsonIgnore
    private Set<SubscribeTopic> topics;

    @JsonIgnore
    private transient AtomicInteger atomicInteger;

    public void saveQos2Cache(Integer messageId,PublishMessage publishMessage){
        qosCache.put(messageId,publishMessage);    }


    public PublishMessage sendQos2Cache(Integer messageId){
        return qosCache.remove(messageId);
    }

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
        int index = atomicInteger.incrementAndGet();
        if (index > MAX_MESSAGE_ID) {
            synchronized (this) {
                index = atomicInteger.incrementAndGet();
                if (index > MAX_MESSAGE_ID) {
                    index = 1;
                    atomicInteger.set(index);
                }
            }
        }
        return index;
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
                RetryManager retryManager = ContextHolder.getReceiveContext().getRetryManager();
                RetryMessage retryMessage = new RetryMessage(messageId, System.currentTimeMillis(), message.isRetain(),
                        message.getTopic(), MqttQoS.valueOf(message.getQos()), JacksonUtil.dynamicJson(message.getBody()).getBytes(StandardCharsets.UTF_8),this);
                retryManager.doRetry(this, retryMessage);
                this.write(message.buildMqttMessage(mqttQoS, messageId));
                break;
        }
    }



    public void sendRetry(RetryMessage retryMessage) {
        this.write(retryMessage.buildMqttMessage());
    }


    /**
     * write message
     *
     * @param mqttMessage #{@link MqttMessage}
     */
    public void write(MqttMessage mqttMessage) {
        if (this.connection.channel().isActive() && this.connection.channel().isWritable()) {
            connection.outbound().sendObject(Mono.just(mqttMessage)).then().subscribe();
        }
    }


}
