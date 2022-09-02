package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luxurong
 */
@Data
@NoArgsConstructor
public class RetryMessage {

    private int count;

    private int messageId;

    private long timestamp;

    private boolean isRetain;

    private String topic;

    private MqttQoS mqttQoS;

    private byte[] body;

    private MqttChannel mqttChannel;


    public RetryMessage(int messageId, long timestamp, boolean isRetain, String topic, MqttQoS mqttQoS, byte[] body, MqttChannel mqttChannel) {
        this.messageId = messageId;
        this.mqttChannel = mqttChannel;
        this.timestamp = timestamp;
        this.isRetain = isRetain;
        this.topic = topic;
        this.mqttQoS = mqttQoS;
        this.body = body;
    }

    public void clear() {
        this.body = null;
    }

    public void retry() {
        count++;
        mqttChannel.sendRetry(this);
    }

    public MqttMessage buildMqttMessage() {
        return MqttMessageUtils.buildPub(true, mqttQoS, this.isRetain, messageId, this.getTopic(), PooledByteBufAllocator.DEFAULT.buffer().writeBytes(body));

    }


}
