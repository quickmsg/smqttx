package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class PublishAckMessage implements Message {

    private int messageId;

    private long timestamp;

    @JsonIgnore
    private MqttChannel mqttChannel;


    public PublishAckMessage(Object message, MqttChannel mqttChannel){
        this.mqttChannel = mqttChannel;
        this.messageId=((MqttMessageIdVariableHeader) ((MqttMessage) message).variableHeader()).messageId();
        this.timestamp = System.currentTimeMillis();
    }

}