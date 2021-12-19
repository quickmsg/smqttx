package io.github.quickmsg.common.message;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Builder;
import lombok.Data;

/**
 * @author luxurong
 */

@Data
@Builder
public class SessionMessage {

    private int qos;

    private String topic;

    private byte[] body;

    private String clientIdentifier;

    private boolean retain;

    public static SessionMessage of(String clientIdentifier, PublishMessage message) {
        return SessionMessage.builder()
                .clientIdentifier(clientIdentifier)
                .topic(message.getTopic())
                .qos(message.getQos())
                .retain(message.isRetain())
                .body(message.getBody())
                .build();
    }

    public MqttPublishMessage toPublishMessage(MqttChannel mqttChannel, int messageId) {
        return MqttMessageUtils.buildPub(
                false,
                MqttQoS.valueOf(this.qos),
                messageId,
                topic,
                PooledByteBufAllocator.DEFAULT.directBuffer().writeBytes(body));
    }

}
