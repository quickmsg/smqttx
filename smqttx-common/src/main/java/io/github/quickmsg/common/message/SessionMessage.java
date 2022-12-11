package io.github.quickmsg.common.message;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;

import java.nio.charset.StandardCharsets;

/**
 * @author luxurong
 */

@Data
@Builder
public class SessionMessage {

    private int qos;

    private String topic;

    private byte[] body;

    private String clientId;

    private boolean retain;

    public static SessionMessage of(String clientIdentifier, PublishMessage message) {
        return SessionMessage.builder()
                .clientId(clientIdentifier)
                .topic(message.getTopic())
                .qos(message.getQos())
                .retain(message.isRetain())
                .body(JacksonUtil.dynamicJson(message.getBody()).getBytes(StandardCharsets.UTF_8))
                .build();
    }


    public  PublishMessage toPublishMessage() {
        PublishMessage publishMessage =new PublishMessage();
        publishMessage.setBody(this.body);
        publishMessage.setTopic(this.topic);
        publishMessage.setRetain(this.retain);
        publishMessage.setClientId(this.clientId);
        publishMessage.setQos(this.qos);
        return publishMessage;
    }

}
