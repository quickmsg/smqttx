package io.github.quickmsg.common.message.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.Message;
import io.netty.handler.codec.mqtt.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luxurong
 */

@Data
@Slf4j
@NoArgsConstructor
public class ConnectMessage implements Message {


    private String nodeIp;

    private String clientId;

    private MqttVersion version;

    private int keepalive;

    private boolean cleanSession;

    private long timestamp;

    private MqttChannel.Auth auth;

    private MqttChannel.Will will;


    @Override
    public int getMessageId() {
        return 0;
    }


    public ConnectMessage(Object message) {
        MqttConnectVariableHeader variableHeader = ((MqttConnectMessage) message).variableHeader();
        MqttConnectPayload mqttConnectPayload = ((MqttConnectMessage) message).payload();
        if (variableHeader.isWillFlag()) {
            this.will = MqttChannel.Will.builder()
                    .willMessage(mqttConnectPayload.willMessageInBytes())
                    .isRetain(variableHeader.isWillRetain())
                    .willTopic(mqttConnectPayload.willTopic())
                    .mqttQoS(MqttQoS.valueOf(variableHeader.willQos()))
                    .build();
        }
        if (variableHeader.hasUserName() && variableHeader.hasPassword()) {
            this.auth = new MqttChannel.Auth();
            this.auth.setUsername(mqttConnectPayload.userName());
            this.auth.setPassword(mqttConnectPayload.passwordInBytes());
        }
        this.version = MqttVersion.fromProtocolNameAndLevel(variableHeader.name(), (byte) variableHeader.version());
        this.cleanSession = variableHeader.isCleanSession();
        this.keepalive = variableHeader.keepAliveTimeSeconds();
        this.timestamp = System.currentTimeMillis();
        this.clientId = mqttConnectPayload.clientIdentifier();
    }
}