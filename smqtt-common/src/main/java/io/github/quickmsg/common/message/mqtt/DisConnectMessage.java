package io.github.quickmsg.common.message.mqtt;

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


    private DisConnectMessage() {
    }

    public static DisConnectMessage INSTANCE = new DisConnectMessage();

    @Override
    public int getMessageId() {
        return 0;
    }


    public DisConnectMessage(  String clientId){
        this.clientId = clientId;
    }


}
