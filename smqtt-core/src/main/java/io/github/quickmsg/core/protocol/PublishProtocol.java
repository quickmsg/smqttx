package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.enums.ChannelStatus;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.acceptor.PublishEvent;
import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.integrate.topic.TopicRegistry;
import io.github.quickmsg.common.message.RetainMessage;
import io.github.quickmsg.common.message.SessionMessage;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.spi.registry.MessageRegistry;
import io.github.quickmsg.common.utils.MessageUtils;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
@Slf4j
public class PublishProtocol implements Protocol<MqttPublishMessage> {

    private final static List<MqttMessageType> MESSAGE_TYPE_LIST = new ArrayList<>();

    static {
        MESSAGE_TYPE_LIST.add(MqttMessageType.PUBLISH);
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return MESSAGE_TYPE_LIST;
    }


    @Override
    public Mono<Event> parseProtocol(SmqttMessage<MqttPublishMessage> smqttMessage, MqttChannel mqttChannel, ContextView contextView) {
        try {
            MqttPublishMessage message = smqttMessage.getMessage();
            ReceiveContext<?> receiveContext = contextView.get(ReceiveContext.class);
            TopicRegistry topicRegistry = receiveContext.getTopicRegistry();
            MqttPublishVariableHeader variableHeader = message.variableHeader();
            MessageRegistry messageRegistry = receiveContext.getMessageRegistry();
            Set<SubscribeTopic> mqttChannels = topicRegistry.getSubscribesByTopic(variableHeader.topicName(),
                    message.fixedHeader().qosLevel());
            // http mock
            if (mqttChannel.getIsMock()) {
                return send(mqttChannels, message, messageRegistry, filterRetainMessage(message, messageRegistry))
                        .thenReturn(new PublishEvent());
            }
            switch (message.fixedHeader().qosLevel()) {
                case AT_MOST_ONCE:
                    return send(mqttChannels, message, messageRegistry, filterRetainMessage(message, messageRegistry))
                            .thenReturn(new PublishEvent());
                case AT_LEAST_ONCE:
                    return send(mqttChannels, message, messageRegistry,
                            mqttChannel.write(MqttMessageUtils.buildPublishAck(variableHeader.packetId()), false)
                                    .then(filterRetainMessage(message, messageRegistry)))
                            .thenReturn(new PublishEvent());
                case EXACTLY_ONCE:
                    if (!mqttChannel.existQos2Msg(variableHeader.packetId())) {
                        return mqttChannel
                                .cacheQos2Msg(variableHeader.packetId(),
                                        MessageUtils.wrapPublishMessage(message, message.fixedHeader().qosLevel(), 0))
                                .then(mqttChannel.write(MqttMessageUtils.buildPublishRec(variableHeader.packetId()), true))
                                .thenReturn(new PublishEvent());
                    }
                default:
                    return Mono.empty();
            }
        } catch (Exception e) {
            log.error("error ", e);
        }
        return Mono.empty();
    }


    /**
     * 通用发送消息
     *
     * @param subscribeTopics {@link SubscribeTopic}
     * @param message         {@link MqttPublishMessage}
     * @param messageRegistry {@link MessageRegistry}
     * @param other           {@link Mono}
     * @return Mono
     */
    private Mono<Void> send(Set<SubscribeTopic> subscribeTopics, MqttPublishMessage message, MessageRegistry messageRegistry, Mono<Void> other) {
        return Mono.when(
                subscribeTopics.stream()
                        .filter(subscribeTopic -> filterOfflineSession(subscribeTopic.getMqttChannel(), messageRegistry, message))
                        .map(subscribeTopic ->
                                subscribeTopic.getMqttChannel().write(MessageUtils.wrapPublishMessage(message,
                                                subscribeTopic.getQoS(),
                                                subscribeTopic.getMqttChannel().generateMessageId()),
                                        subscribeTopic.getQoS().value() > 0)
                        )
                        .collect(Collectors.toList())).then(other);

    }


    /**
     * 过滤离线会话消息
     *
     * @param mqttChannel     {@link MqttChannel}
     * @param messageRegistry {@link MessageRegistry}
     * @param mqttMessage     {@link MqttPublishMessage}
     * @return boolean
     */
    private boolean filterOfflineSession(MqttChannel mqttChannel, MessageRegistry messageRegistry, MqttPublishMessage mqttMessage) {
        if (mqttChannel.getStatus() == ChannelStatus.ONLINE) {
            return true;
        } else {
            messageRegistry
                    .saveSessionMessage(SessionMessage.of(mqttChannel.getClientIdentifier(), mqttMessage));
            return false;
        }
    }


    /**
     * 过滤保留消息
     *
     * @param message         {@link MqttPublishMessage}
     * @param messageRegistry {@link MessageRegistry}
     * @return Mono
     */
    private Mono<Void> filterRetainMessage(MqttPublishMessage message, MessageRegistry messageRegistry) {
        return Mono.fromRunnable(() -> {
            if (message.fixedHeader().isRetain()) {
                messageRegistry.saveRetainMessage(RetainMessage.of(message));
            }
        });
    }


}
