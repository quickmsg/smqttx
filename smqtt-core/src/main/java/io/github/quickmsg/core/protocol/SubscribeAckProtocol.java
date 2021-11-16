package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.event.NoneEvent;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luxurong
 */
public class SubscribeAckProtocol implements Protocol<MqttSubAckMessage, NoneEvent> {

    private final static List<MqttMessageType> MESSAGE_TYPE_LIST = new ArrayList<>();


    static {
        MESSAGE_TYPE_LIST.add(MqttMessageType.SUBACK);
    }

    @Override
    public Mono<NoneEvent> parseProtocol(SmqttMessage<MqttSubAckMessage> smqttMessage, MqttChannel mqttChannel, ContextView contextView) {
        MqttSubAckMessage message = smqttMessage.getMessage();
        return mqttChannel.cancelRetry(MqttMessageType.SUBSCRIBE,message.variableHeader().messageId())
                .thenReturn(NoneEvent.INSTANCE);
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return MESSAGE_TYPE_LIST;
    }


}
