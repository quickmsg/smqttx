package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.acceptor.SubscribeEvent;
import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.interate1.msg.IntegrateMessages;
import io.github.quickmsg.common.interate1.topic.IntergrateTopics;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
public class SubscribeProtocol implements Protocol<MqttSubscribeMessage> {


    private final static List<MqttMessageType> MESSAGE_TYPE_LIST = new ArrayList<>();


    static {
        MESSAGE_TYPE_LIST.add(MqttMessageType.SUBSCRIBE);
    }

    @Override
    public Mono<Event> parseProtocol(SmqttMessage<MqttSubscribeMessage> smqttMessage, MqttChannel mqttChannel, ContextView contextView) {
        MqttSubscribeMessage message = smqttMessage.getMessage();
        return Mono.fromRunnable(() -> {
            ReceiveContext<?> receiveContext = contextView.get(ReceiveContext.class);
            IntergrateTopics<SubscribeTopic> topics = receiveContext.getIntegrate().getTopics();
            IntegrateMessages messages = receiveContext.getIntegrate().getMessages();
            message.payload().topicSubscriptions()
                    .forEach(mqttTopicSubscription -> {
                        this.loadRetainMessage(messages, mqttChannel, mqttTopicSubscription.topicName());
                        topics.registryTopic(mqttTopicSubscription.topicName(),
                                new SubscribeTopic(mqttTopicSubscription.topicName(),
                                        mqttTopicSubscription.qualityOfService(), mqttChannel));
                    });
        }).then(mqttChannel.write(
                MqttMessageUtils.buildSubAck(
                        message.variableHeader().messageId(),
                        message.payload()
                                .topicSubscriptions()
                                .stream()
                                .map(mqttTopicSubscription ->
                                        mqttTopicSubscription.qualityOfService()
                                                .value())
                                .collect(Collectors.toList())), false)).thenReturn(buildEvent(message, mqttChannel));
    }

    private SubscribeEvent buildEvent(MqttSubscribeMessage message, MqttChannel mqttChannel) {
        return new SubscribeEvent(EventMsg.SUBSCRIBE_MESSAGE,
                mqttChannel.getClientIdentifier(),
                message.payload().topicSubscriptions(),
                System.currentTimeMillis());
    }

    private void loadRetainMessage(IntegrateMessages messages, MqttChannel mqttChannel, String topicName) {
        messages.getRetainMessage(topicName)
                .forEach(retainMessage ->
                        mqttChannel.write(retainMessage.toPublishMessage(mqttChannel), retainMessage.getQos() > 0)
                                .subscribe());
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return MESSAGE_TYPE_LIST;
    }


}
