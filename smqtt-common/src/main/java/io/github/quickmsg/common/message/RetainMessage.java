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
public class RetainMessage {

    private int qos;

    private String topic;

    private byte[] body;

    public static RetainMessage of(PublishMessage message) {
        return RetainMessage.builder()
                .topic(message.getTopic())
                .qos(message.getQos())
                .body(message.getBody())
                .build();
    }

    public MqttPublishMessage toPublishMessage(int messageId) {
        return MqttMessageUtils.buildPub(
                false,
                MqttQoS.valueOf(this.qos),
                messageId,
                topic,
                PooledByteBufAllocator.DEFAULT.directBuffer().writeBytes(body));
    }

}
