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
public class PingMessage implements Message {


    @JsonIgnore
    private MqttChannel mqttChannel;

    @JsonIgnore
    private ReceiveContext<?> context;

    private PingMessage() {
    }

    public static PingMessage INSTANCE = new PingMessage();

    @Override
    public int getMessageId() {
        return 0;
    }

    public PingMessage(MqttChannel mqttChannel, ReceiveContext<?> receiveContext) {
        this.context = receiveContext;
        this.mqttChannel = mqttChannel;
    }

}
