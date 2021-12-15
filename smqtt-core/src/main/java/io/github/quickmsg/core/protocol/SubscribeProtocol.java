package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.acceptor.SubscribeEvent;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.msg.IntegrateMessages;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.message.mqtt.SubscribeMessage;
import io.github.quickmsg.common.metric.CounterType;
import io.github.quickmsg.common.metric.MetricManagerHolder;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.stream.Collectors;

/**
 * @author luxurong
 */
public class SubscribeProtocol implements Protocol<SubscribeMessage> {


    @Override
    public Mono<Event> parseProtocol(SubscribeMessage message, MqttChannel mqttChannel, ContextView contextView) {
        MetricManagerHolder.metricManager.getMetricRegistry().getMetricCounter(CounterType.SUBSCRIBE_EVENT).increment();
        return Mono.fromRunnable(() -> {
                    ReceiveContext<?> receiveContext = contextView.get(ReceiveContext.class);
                    IntegrateTopics<SubscribeTopic> topics = receiveContext.getIntegrate().getTopics();
                    IntegrateMessages messages = receiveContext.getIntegrate().getMessages();
                    message.getSubscribeTopics()
                            .forEach(subscribeTopic -> {
                                this.loadRetainMessage(messages, mqttChannel, subscribeTopic.getTopicFilter());
                                topics.registryTopic(subscribeTopic.getTopicFilter(), subscribeTopic.setMqttChannel(mqttChannel));
                            });
                }).then(mqttChannel.write(
                        MqttMessageUtils.buildSubAck(
                                message.getMessageId(),
                                message.getSubscribeTopics()
                                        .stream()
                                        .map(subscribeTopic -> subscribeTopic.getQoS().value())
                                        .collect(Collectors.toList())), false))
                .thenReturn(buildEvent(message, mqttChannel));
    }

    @Override
    public Class<SubscribeMessage> getClassType() {
        return SubscribeMessage.class;
    }

    private SubscribeEvent buildEvent(SubscribeMessage message, MqttChannel mqttChannel) {
        return new SubscribeEvent(EventMsg.SUBSCRIBE_MESSAGE,
                mqttChannel.getClientIdentifier(),
                message.getSubscribeTopics(),
                System.currentTimeMillis());
    }

    private void loadRetainMessage(IntegrateMessages messages, MqttChannel mqttChannel, String topic) {
        messages.getRetainMessage(topic)
                .forEach(retainMessage ->
                        mqttChannel.write(retainMessage.toPublishMessage(mqttChannel), retainMessage.getQos() > 0)
                                .subscribe());
    }


}
