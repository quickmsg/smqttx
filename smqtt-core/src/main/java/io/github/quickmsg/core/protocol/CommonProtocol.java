package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.NoneEvent;
import io.github.quickmsg.common.event.acceptor.CommonEvent;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.topic.IntergrateTopics;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import io.github.quickmsg.common.utils.MessageUtils;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
@Slf4j
public class CommonProtocol implements Protocol<MqttMessage> {


    private final static List<MqttMessageType> MESSAGE_TYPE_LIST = new ArrayList<>();

    static {
        MESSAGE_TYPE_LIST.add(MqttMessageType.PINGRESP);
        MESSAGE_TYPE_LIST.add(MqttMessageType.PINGREQ);
        MESSAGE_TYPE_LIST.add(MqttMessageType.DISCONNECT);
        MESSAGE_TYPE_LIST.add(MqttMessageType.PUBCOMP);
        MESSAGE_TYPE_LIST.add(MqttMessageType.PUBREC);
        MESSAGE_TYPE_LIST.add(MqttMessageType.PUBREL);
    }


    @Override
    public Mono<Event> parseProtocol(SmqttMessage<MqttMessage> smqttMessage, MqttChannel mqttChannel, ContextView contextView) {
        MqttMessage message = smqttMessage.getMessage();
        switch (message.fixedHeader().messageType()) {
            case PINGREQ:
                return mqttChannel.write(MqttMessageUtils.buildPongMessage(), false)
                        .then(Mono.fromSupplier(() -> build(EventMsg.PING_MESSAGE, mqttChannel.getClientIdentifier(), 0)));
            case DISCONNECT:
                return Mono.fromSupplier(() -> {
                    mqttChannel.setWill(null);
                    Connection connection;
                    if (!(connection = mqttChannel.getConnection()).isDisposed()) {
                        connection.dispose();
                    }
                    return build(EventMsg.DIS_CONNECT_MESSAGE, mqttChannel.getClientIdentifier(), 0);
                });
            case PUBREC:
                MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
                int messageId = messageIdVariableHeader.messageId();
                return mqttChannel.cancelRetry(MqttMessageType.PUBLISH, messageId)
                        .then(mqttChannel.write(MqttMessageUtils.buildPublishRel(messageId), true))
                        .thenReturn(build(EventMsg.PUB_REC_MESSAGE,
                                mqttChannel.getClientIdentifier(),
                                messageId));
            case PUBREL:
                MqttMessageIdVariableHeader relMessageIdVariableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
                int id = relMessageIdVariableHeader.messageId();
                /*
                 * 判断是不是缓存qos2消息
                 *       是： 走消息分发 & 回复 comp消息
                 *       否： 直接回复 comp消息
                 */
                return mqttChannel.removeQos2Msg(id)
                        .map(msg -> {
                            ReceiveContext<?> receiveContext = contextView.get(ReceiveContext.class);
                            IntergrateTopics<SubscribeTopic> topics = receiveContext.getIntegrate().getTopics();
                            Set<SubscribeTopic> subscribeTopics = topics.getObjectsByTopic(msg.variableHeader().topicName());
                            return Mono.when(
                                            subscribeTopics.stream()
                                                    .map(subscribeTopic -> subscribeTopic.getMqttChannel()
                                                            .write(MessageUtils.wrapPublishMessage(msg, subscribeTopic.minQos(msg.fixedHeader().qosLevel()),
                                                                    subscribeTopic.getMqttChannel().generateMessageId()), subscribeTopic.getQoS().value() > 0)
                                                    ).collect(Collectors.toList()))
                                    .then(mqttChannel.cancelRetry(MqttMessageType.PUBREC, id))
                                    .then(mqttChannel.write(MqttMessageUtils.buildPublishComp(id), false))
                                    .thenReturn(build(EventMsg.PUB_REL_MESSAGE,
                                            mqttChannel.getClientIdentifier(),
                                            id));
                        }).orElseGet(() -> mqttChannel.write(MqttMessageUtils.buildPublishComp(id), false)
                                .thenReturn(build(EventMsg.PUB_REL_MESSAGE,
                                        mqttChannel.getClientIdentifier(),
                                        id)));

            case PUBCOMP:
                MqttMessageIdVariableHeader messageIdVariableHeader1 = (MqttMessageIdVariableHeader) message.variableHeader();
                int compId = messageIdVariableHeader1.messageId();
                return mqttChannel.cancelRetry(MqttMessageType.PUBREL, compId)
                        .thenReturn(build(EventMsg.PUB_COMP_MESSAGE,
                                mqttChannel.getClientIdentifier(),
                                compId));
            case PINGRESP:
            default:
                return Mono.just(NoneEvent.INSTANCE);

        }
    }

    private Event build(String type, String clientId, int id) {
        return new CommonEvent(type, clientId, id, System.currentTimeMillis());
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return MESSAGE_TYPE_LIST;
    }
}
