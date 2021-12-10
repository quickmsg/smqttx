package io.github.quickmsg.core.mqtt;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.message.mqtt.PublishAckMessage;
import io.github.quickmsg.common.transport.Transport;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luxurong
 */
@Getter
@Slf4j
public class MqttReceiveContext extends AbstractReceiveContext<MqttConfiguration> {

    public MqttReceiveContext(MqttConfiguration configuration, Transport<MqttConfiguration> transport) {
        super(configuration, transport);
    }

    public void apply(MqttChannel mqttChannel) {
        mqttChannel.registryDelayTcpClose()
                .getConnection()
                .inbound()
                .receiveObject()
                .cast(MqttMessage.class)
                .doOnError(throwable -> log.error("on connect error", throwable))
                .subscribe(mqttMessage -> this.accept(mqttChannel, parseMessage(mqttMessage)));

    }

    @Override
    public void accept(MqttChannel channel, Message message) {
        this.getProtocolAdaptor().chooseProtocol(channel, message, this);
    }

    private Message parseMessage(MqttMessage mqttMessage) {
        MqttFixedHeader fixedHeader =mqttMessage.fixedHeader();
        switch (fixedHeader.messageType()){
            case PUBACK:
                return new PublishAckMessage();
            case PUBREC:
            case PUBREL:
            case SUBACK:
            case CONNACK:
            case CONNECT:
            case PINGREQ:
            case PUBCOMP:
            case PUBLISH:
            case PINGRESP:
            case UNSUBACK:
            case SUBSCRIBE:
            case DISCONNECT:
            case UNSUBSCRIBE:
            default:
                return Message.EMPTY_MESSAGE;
        }

    }
}
