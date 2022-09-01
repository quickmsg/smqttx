package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import lombok.Data;

/**
 * @author luxurong
 */
@Data
public class DisConnectMessage implements Message {


    private String clientId;


    private long timestamp;


    @JsonIgnore
    private MqttChannel mqttChannel;


    private DisConnectMessage() {
    }

    public static DisConnectMessage INSTANCE = new DisConnectMessage();

    @Override
    public int getMessageId() {
        return 0;
    }


    public DisConnectMessage(  MqttChannel mqttChannel){
        this.clientId = mqttChannel.getClientId();
        this.mqttChannel=mqttChannel;
    }


}
