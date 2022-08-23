package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import lombok.Data;

import java.util.List;

/**
 * @author luxurong
 */

@Data
public class UnSubscribeMessage implements Message {

    private int messageId;
    private long timestamp;

    private List<String> topics;

    private String clientId;

    public UnSubscribeMessage(Object message,String clientId) {
        this.clientId=clientId;
        MqttUnsubscribeMessage unsubscribeMessage = (MqttUnsubscribeMessage) message;
        this.messageId = unsubscribeMessage.variableHeader().messageId();
        this.topics = unsubscribeMessage.payload().topics();
        this.timestamp = System.currentTimeMillis();
    }
}
