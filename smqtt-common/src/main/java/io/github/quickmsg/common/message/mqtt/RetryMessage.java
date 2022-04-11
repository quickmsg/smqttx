package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luxurong
 */
@Data
@NoArgsConstructor
public class RetryMessage implements Message {

    private int count;

    private int messageId;

    private long timestamp;


    private boolean isRetain;

    private String topic;

    private MqttQoS mqttQoS;

    private byte[] body;


    private MqttChannel mqttChannel;

    @JsonIgnore
    private ReceiveContext<?> context;

    public RetryMessage(int messageId, long timestamp, boolean isRetain, String topic, MqttQoS mqttQoS, byte[] body, MqttChannel mqttChannel, ReceiveContext<?> context) {
        this.messageId = messageId;
        this.timestamp = timestamp;
        this.isRetain = isRetain;
        this.topic = topic;
        this.mqttQoS = mqttQoS;
        this.body = body;
        this.mqttChannel = mqttChannel;
        this.context = context;
    }

    public void clear() {
        this.mqttChannel = null;
        this.context = null;
        this.body = null;
    }

    public void retry() {
        ContextHolder.getReceiveContext().getProtocolAdaptor().chooseProtocol(this);
        count++;
    }
}
