package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class PublishMessage implements Message {

    private int messageId;

    private boolean cluster;

    private String topic;

    private int qos;

    private boolean retain;

    private byte[] body;


    public MqttPublishMessage buildMqttMessage(MqttQoS qoS, int messageId) {
        return MqttMessageUtils.buildPub(false, qoS, this.retain, messageId, this.getTopic(), PooledByteBufAllocator.DEFAULT.buffer().writeBytes(body));
    }

}
