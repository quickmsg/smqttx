package io.github.quickmsg.common.message.mqtt;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luxurong
 */

@Data
@Slf4j
public class ConnectMessage implements Message {

    private boolean logger;

    private String clientId;

    private MqttVersion version;

    private String username;

    private byte[] password;

    private int keepalive;

    private boolean cleanSession;

    private MqttChannel.Will will;

    @Override
    public int getMessageId() {
        return 0;
    }

    @Override
    public Message fromMqttMessage(Object message) {
        if (message instanceof MqttConnectMessage) {
            ConnectMessage connectMessage = new ConnectMessage();
            MqttFixedHeader fixedHeader = ((MqttConnectMessage) message).fixedHeader();
            MqttConnectVariableHeader variableHeader = ((MqttConnectMessage) message).variableHeader();
            MqttConnectPayload mqttConnectPayload = ((MqttConnectMessage) message).payload();
            connectMessage.setClientId(mqttConnectPayload.clientIdentifier());
            connectMessage.setVersion(MqttVersion.fromProtocolNameAndLevel(variableHeader.name(),(byte)variableHeader.version()));
            connectMessage.setUsername();
            fixedHeader.isRetain();

        } else {
            return Message.EMPTY_MESSAGE;
        }
    }
}
