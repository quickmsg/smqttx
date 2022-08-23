package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class PublishRecMessage implements Message {

    private int messageId;

    private long timestamp;

    private String clientId;


    public PublishRecMessage(Object message, String clientId){
        this.clientId  = clientId;
        this.messageId=((MqttMessageIdVariableHeader) ((MqttMessage) message).variableHeader()).messageId();
        this.timestamp = System.currentTimeMillis();
    }

}
