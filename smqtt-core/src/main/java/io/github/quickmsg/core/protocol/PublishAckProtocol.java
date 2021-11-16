package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.acceptor.CommonEvent;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luxurong
 */
public class PublishAckProtocol implements Protocol<MqttPubAckMessage> {

    private final static List<MqttMessageType> MESSAGE_TYPE_LIST = new ArrayList<>();


    static {
        MESSAGE_TYPE_LIST.add(MqttMessageType.PUBACK);
    }


    @Override
    public Mono<Event> parseProtocol(SmqttMessage<MqttPubAckMessage> smqttMessage, MqttChannel mqttChannel, ContextView contextView) {
        MqttPubAckMessage message = smqttMessage.getMessage();
        MqttMessageIdVariableHeader idVariableHeader = message.variableHeader();
        int messageId = idVariableHeader.messageId();
        return mqttChannel.cancelRetry(MqttMessageType.PUBLISH, messageId)
                .thenReturn(new CommonEvent(mqttChannel.getClientIdentifier(),
                        EventMsg.PUBLISH_ACK_MESSAGE, messageId, System.currentTimeMillis()));
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return MESSAGE_TYPE_LIST;
    }


}
