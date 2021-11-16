package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.acceptor.CommonEvent;
import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.interate1.topic.IntergrateTopics;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
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
public class CommonProtocol implements Protocol<MqttMessage, CommonEvent> {


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
    public Mono<CommonEvent> parseProtocol(SmqttMessage<MqttMessage> smqttMessage, MqttChannel mqttChannel, ContextView contextView) {
        MqttMessage message = smqttMessage.getMessage();
        switch (message.fixedHeader().messageType()) {
            case PINGREQ:
                return mqttChannel.write(MqttMessageUtils.buildPongMessage(), false)
                        .thenReturn(new CommonEvent());
            case DISCONNECT:
                return Mono.fromRunnable(() -> {
                    mqttChannel.setWill(null);
                    Connection connection;
                    if (!(connection = mqttChannel.getConnection()).isDisposed()) {
                        connection.dispose();
                    }
                });
            case PUBREC:
                MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
                int messageId = messageIdVariableHeader.messageId();
                return mqttChannel.cancelRetry(MqttMessageType.PUBLISH, messageId)
                        .then(mqttChannel.write(MqttMessageUtils.buildPublishRel(messageId), true))
                        .thenReturn(new CommonEvent());
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
                                    .thenReturn(new CommonEvent());
                        }).orElseGet(() -> mqttChannel.write(MqttMessageUtils.buildPublishComp(id), false).thenReturn(new CommonEvent()));

            case PUBCOMP:
                MqttMessageIdVariableHeader messageIdVariableHeader1 = (MqttMessageIdVariableHeader) message.variableHeader();
                int compId = messageIdVariableHeader1.messageId();
                return mqttChannel.cancelRetry(MqttMessageType.PUBREL, compId)
                        .thenReturn(new CommonEvent());
            case PINGRESP:
            default:
                return Mono.empty();

        }
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return MESSAGE_TYPE_LIST;
    }
}
