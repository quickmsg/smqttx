package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.acl.AclAction;
import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.msg.IntegrateMessages;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.message.mqtt.SubscribeMessage;
import io.github.quickmsg.common.metric.CounterType;
import io.github.quickmsg.common.metric.MetricManagerHolder;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import reactor.util.context.ContextView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
public class SubscribeProtocol implements Protocol<SubscribeMessage> {


    @Override
    public void parseProtocol(SubscribeMessage message, MqttChannel mqttChannel, ContextView contextView) {
        MetricManagerHolder.metricManager.getMetricRegistry().getMetricCounter(CounterType.SUBSCRIBE_EVENT).increment();
        ReceiveContext<?> receiveContext = contextView.get(ReceiveContext.class);
        IntegrateTopics<SubscribeTopic> topics = receiveContext.getIntegrate().getTopics();
        IntegrateChannels channels = receiveContext.getIntegrate().getChannels();
        AclManager aclManager = receiveContext.getAclManager();
        IntegrateMessages messages = receiveContext.getIntegrate().getMessages();
        List<SubscribeTopic> subscribeTopics=message.getSubscribeTopics()
                .stream()
                .filter(subscribeTopic -> aclManager.check(mqttChannel, subscribeTopic.getTopicFilter(), AclAction.SUBSCRIBE))
                .peek(subscribeTopic -> this.loadRetainMessage(channels,messages, subscribeTopic)).collect(Collectors.toList());
        topics.registryTopic(subscribeTopics);
        mqttChannel.write(
                MqttMessageUtils.buildSubAck(
                        message.getMessageId(),
                        message.getSubscribeTopics()
                                .stream()
                                .map(subscribeTopic -> subscribeTopic.getQoS().value())
                                .collect(Collectors.toList())));
    }

    @Override
    public Class<SubscribeMessage> getClassType() {
        return SubscribeMessage.class;
    }

    private void loadRetainMessage(IntegrateChannels channels, IntegrateMessages messages, SubscribeTopic topic) {
        messages.getRetainMessage(topic.getTopicFilter())
                .forEach(retainMessage -> {
                    Optional.ofNullable(channels.get(topic.getClientId()))
                                            .ifPresent(mqttChannel -> {
                                                mqttChannel.sendPublish(topic.minQos(MqttQoS.valueOf(retainMessage.getQos())),
                                                            retainMessage.toPublishMessage());
                                            });
                });
    }


}
