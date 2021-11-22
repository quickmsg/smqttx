package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.enums.ChannelStatus;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.NoneEvent;
import io.github.quickmsg.common.event.acceptor.PublishEvent;
import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.interate1.msg.IntegrateMessages;
import io.github.quickmsg.common.interate1.topic.IntergrateTopics;
import io.github.quickmsg.common.message.RetainMessage;
import io.github.quickmsg.common.message.SessionMessage;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
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
            IntergrateTopics<SubscribeTopic> topics = receiveContext.getIntegrate().getTopics();
            MqttPublishVariableHeader variableHeader = message.variableHeader();
            IntegrateMessages messages = receiveContext.getIntegrate().getMessages();
            Set<SubscribeTopic> mqttChannels = topics.getObjectsByTopic(variableHeader.topicName());
            // http mock
            if (mqttChannel.getIsMock()) {
                return send(mqttChannels, message, messages, filterRetainMessage(message, messages))
                        .thenReturn(buildEvent(smqttMessage,mqttChannel.getClientIdentifier()));
            }
            switch (message.fixedHeader().qosLevel()) {
                case AT_MOST_ONCE:
                    return send(mqttChannels, message, messages, filterRetainMessage(message, messages))
                            .thenReturn(new PublishEvent());
                case AT_LEAST_ONCE:
                    return send(mqttChannels, message, messages,
                            mqttChannel.write(MqttMessageUtils.buildPublishAck(variableHeader.packetId()), false)
                                    .then(filterRetainMessage(message, messages)))
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

    private Event buildEvent(SmqttMessage<MqttPublishMessage> smqttMessage, String clientIdentifier) {
        if(smqttMessage.getIsCluster()){
            return NoneEvent.INSTANCE;
        }



    }


    /**
     * 通用发送消息
     *
     * @param subscribeTopics {@link SubscribeTopic}
     * @param message         {@link MqttPublishMessage}
     * @param messages        {@link IntegrateMessages}
     * @param other           {@link Mono}
     * @return Mono
     */
    private Mono<Void> send(Set<SubscribeTopic> subscribeTopics, MqttPublishMessage message, IntegrateMessages messages, Mono<Void> other) {
        return Mono.fromRunnable(() ->
                subscribeTopics.stream()
                        .filter(subscribeTopic -> filterOfflineSession(subscribeTopic.getMqttChannel(), messages, message))
                        .forEach(subscribeTopic ->
                                subscribeTopic.getMqttChannel().write(MessageUtils.wrapPublishMessage(message,
                                                        subscribeTopic.minQos(message.fixedHeader().qosLevel()),
                                                        subscribeTopic.getMqttChannel().generateMessageId()),
                                                subscribeTopic.minQos(message.fixedHeader().qosLevel()).value() > 0)
                                        .subscribe()
                        )).then(other);

    }


    /**
     * 过滤离线会话消息
     *
     * @param mqttChannel {@link MqttChannel}
     * @param messages    {@link IntegrateMessages}
     * @param mqttMessage {@link MqttPublishMessage}
     * @return boolean
     */
    private boolean filterOfflineSession(MqttChannel mqttChannel, IntegrateMessages messages, MqttPublishMessage mqttMessage) {
        if (mqttChannel.getStatus() == ChannelStatus.ONLINE) {
            return true;
        } else {
            messages
                    .saveSessionMessage(SessionMessage.of(mqttChannel.getClientIdentifier(), mqttMessage));
            return false;
        }
    }


    /**
     * 过滤保留消息
     *
     * @param message  {@link MqttPublishMessage}
     * @param messages {@link IntegrateMessages}
     * @return Mono
     */
    private Mono<Void> filterRetainMessage(MqttPublishMessage message, IntegrateMessages messages) {
        return Mono.fromRunnable(() -> {
            if (message.fixedHeader().isRetain()) {
                messages.saveRetainMessage(RetainMessage.of(message));
            }
        });
    }


}
