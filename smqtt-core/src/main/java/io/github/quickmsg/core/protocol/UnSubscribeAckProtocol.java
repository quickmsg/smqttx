package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.NoneEvent;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luxurong
 */
public class UnSubscribeAckProtocol implements Protocol<MqttUnsubAckMessage> {

    private final static List<MqttMessageType> MESSAGE_TYPE_LIST = new ArrayList<>();

    static {
        MESSAGE_TYPE_LIST.add(MqttMessageType.UNSUBACK);
    }

    @Override
    public Mono<Event> parseProtocol(SmqttMessage<MqttUnsubAckMessage> smqttMessage, MqttChannel mqttChannel, ContextView contextView) {
        MqttUnsubAckMessage message = smqttMessage.getMessage();
        return mqttChannel.cancelRetry(MqttMessageType.UNSUBSCRIBE, message.variableHeader().messageId())
                .thenReturn(NoneEvent.INSTANCE);
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return MESSAGE_TYPE_LIST;
    }


}
