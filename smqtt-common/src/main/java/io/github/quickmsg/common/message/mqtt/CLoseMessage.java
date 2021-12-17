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
public class CLoseMessage implements Message {

    private int messageId;

    private long timestamp;

    private String clientId;

    private String reason;


    @JsonIgnore
    private MqttChannel mqttChannel;

    @JsonIgnore
    private ReceiveContext<?> context;



    public CLoseMessage(Object message, MqttChannel mqttChannel, ReceiveContext<?> receiveContext){
        this.context  = receiveContext;
        this.mqttChannel = mqttChannel;
    }

}
