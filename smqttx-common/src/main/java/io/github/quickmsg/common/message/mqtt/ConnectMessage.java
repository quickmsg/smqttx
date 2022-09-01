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

    private MqttChannel mqttChannel;

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

    @Override
    public MqttChannel getMqttChannel() {
        return mqttChannel;
    }


    public ConnectMessage(MqttConnectMessage message,MqttChannel mqttChannel) {
        MqttConnectVariableHeader variableHeader = message.variableHeader();
        MqttConnectPayload mqttConnectPayload = message.payload();
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
        this.mqttChannel = mqttChannel;
    }
}