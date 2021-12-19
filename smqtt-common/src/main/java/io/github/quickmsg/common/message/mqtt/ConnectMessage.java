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

    private boolean logger;

    private String clientId;

    private MqttVersion version;

    private String username;

    private byte[] password;

    private int keepalive;

    private boolean cleanSession;

    private MqttChannel.Will will;

    @JsonIgnore
    private MqttChannel mqttChannel;

    @JsonIgnore
    private ReceiveContext<?> context;

    @Override
    public int getMessageId() {
        return 0;
    }


    public ConnectMessage(Object message, MqttChannel mqttChannel, ReceiveContext<?> receiveContext){
        this.context  = receiveContext;
        this.mqttChannel = mqttChannel;
        MqttConnectVariableHeader variableHeader = ((MqttConnectMessage) message).variableHeader();
        MqttConnectPayload mqttConnectPayload = ((MqttConnectMessage) message).payload();
        this.clientId=mqttConnectPayload.clientIdentifier();
        this.version=MqttVersion.fromProtocolNameAndLevel(variableHeader.name(), (byte) variableHeader.version());
        this.username=mqttConnectPayload.userName();
        this.password=mqttConnectPayload.passwordInBytes();
        this.cleanSession=variableHeader.isCleanSession();
        if (variableHeader.isWillFlag()) {
            this.will= MqttChannel.Will.builder()
                            .willMessage(mqttConnectPayload.willMessageInBytes())
                            .isRetain(variableHeader.isWillRetain())
                            .willTopic(mqttConnectPayload.willTopic())
                            .mqttQoS(MqttQoS.valueOf(variableHeader.willQos()))
                            .build();
        }
        this.keepalive =variableHeader.keepAliveTimeSeconds();
        this.clientId = mqttConnectPayload.clientIdentifier();
        this.mqttChannel =mqttChannel;
    }

}
