package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.acceptor.SubscribeAckEvent;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luxurong
 */
public class SubscribeAckProtocol implements Protocol<MqttSubAckMessage> {

    private final static List<MqttMessageType> MESSAGE_TYPE_LIST = new ArrayList<>();


    static {
        MESSAGE_TYPE_LIST.add(MqttMessageType.SUBACK);
    }

    @Override
    public Mono<Event> parseProtocol(SmqttMessage<MqttSubAckMessage> smqttMessage, MqttChannel mqttChannel, ContextView contextView) {
        MqttSubAckMessage message = smqttMessage.getMessage();
        return mqttChannel.cancelRetry(MqttMessageType.SUBSCRIBE, message.variableHeader().messageId())
                .thenReturn(new SubscribeAckEvent(
                        EventMsg.SUBSCRIBE_ACK_MESSAGE, mqttChannel.getClientIdentifier(), message.variableHeader().messageId(), message.payload().grantedQoSLevels(), System.currentTimeMillis()));
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return MESSAGE_TYPE_LIST;
    }


}
