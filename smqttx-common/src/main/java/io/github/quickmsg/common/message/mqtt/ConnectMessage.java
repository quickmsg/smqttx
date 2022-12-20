package io.github.quickmsg.common.message.mqtt;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.integrate.cache.ConnectCache;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.utils.ServerUtils;
import io.netty.handler.codec.mqtt.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author luxurong
 */

@Data
@Slf4j
@NoArgsConstructor
public class ConnectMessage implements Message {

    private String clientAddress;

    private String clientId;

    private String nodeIp;
    @JsonIgnore
    private MqttChannel mqttChannel;

    private MqttVersion version;

    private int keepalive;

    private boolean cleanSession;

    private String time;

    private MqttChannel.Auth auth;

    private MqttChannel.Will will;

    private String event = "connect";


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
        this.clientId = message.payload().clientIdentifier();
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
        this.time = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);
        this.mqttChannel = mqttChannel;
        this.mqttChannel.setClientId(mqttConnectPayload.clientIdentifier());
        this.clientAddress = mqttChannel.getAddress();
    }


    public ConnectCache getCache(String localNode){
        ConnectCache cache = new ConnectCache();
        cache.setAuth(this.auth);
        cache.setKeepalive(this.keepalive);
        cache.setVersion(this.version);
        cache.setWill(this.will);
        cache.setCleanSession(this.cleanSession);
        cache.setClientId(this.mqttChannel.getClientId());
        cache.setConnectTime(this.time);
        cache.setNodeIp(localNode);
        cache.setClientAddress(this.clientAddress);
        return cache;
    }

}