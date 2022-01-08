package io.github.quickmsg.core.mqtt;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.Message;
import io.github.quickmsg.common.message.mqtt.*;
import io.github.quickmsg.common.rule.DslExecutor;
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
        mqttChannel
                .getConnection()
                .inbound()
                .receiveObject()
                .cast(MqttMessage.class)
                .doOnError(throwable -> log.error("on connect error", throwable))
                .subscribe(mqttMessage -> this.accept(parseMessage(mqttChannel, mqttMessage)));

    }

    private Message parseMessage(MqttChannel mqttChannel, MqttMessage mqttMessage) {
        MqttFixedHeader fixedHeader = mqttMessage.fixedHeader();
        if (mqttMessage.decoderResult().isSuccess()) {
            switch (fixedHeader.messageType()) {
                case PUBACK:
                    return new PublishAckMessage(mqttMessage, mqttChannel, this);
                case PUBREC:
                    return new PublishRecMessage(mqttMessage, mqttChannel, this);
                case PUBREL:
                    return new PublishRelMessage(mqttMessage, mqttChannel, this);
                case CONNECT:
                    return new ConnectMessage(mqttMessage, mqttChannel, this);
                case PINGREQ:
                    return new PingMessage(mqttChannel, this);
                case PUBCOMP:
                    return new PublishCompMessage(mqttMessage, mqttChannel, this);
                case PUBLISH:
                    return new PublishMessage(mqttMessage, mqttChannel, this);
                case SUBSCRIBE:
                    return new SubscribeMessage(mqttMessage, mqttChannel, this);
                case DISCONNECT:
                    return new DisConnectMessage(mqttChannel, this);
                case UNSUBSCRIBE:
                    return new UnSubscribeMessage(mqttMessage, mqttChannel, this);
                default:
                    return Message.EMPTY_MESSAGE;
            }
        } else {
            return Message.EMPTY_MESSAGE;
        }
    }

    @Override
    public void accept(Message message) {
        this.getProtocolAdaptor().chooseProtocol(message);
    }


}
