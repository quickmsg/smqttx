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
public class CloseMessage implements Message {

    private int messageId;
    private long timestamp;

    private String reason;

    @JsonIgnore
    private MqttChannel mqttChannel;


    public CloseMessage(){
        this.timestamp = System.currentTimeMillis();
    }

}
