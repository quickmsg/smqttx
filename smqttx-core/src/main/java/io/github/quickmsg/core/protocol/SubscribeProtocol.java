package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.acl.AclAction;
import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.msg.IntegrateMessages;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.log.LogEvent;
import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.log.LogStatus;
import io.github.quickmsg.common.message.mqtt.SubscribeMessage;
import io.github.quickmsg.common.metric.CounterType;
import io.github.quickmsg.common.metric.MetricManagerHolder;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.JacksonUtil;
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
        ContextHolder.getReceiveContext().getMetricManager()
                .getMetricRegistry()
                .getMetricCounter(CounterType.SUBSCRIBE_EVENT).increment();
        ReceiveContext<?> receiveContext =  contextView.get(ReceiveContext.class);
        LogManager logManager = receiveContext.getLogManager();
        IntegrateTopics<SubscribeTopic> topics = receiveContext.getIntegrate().getTopics();
        AclManager aclManager = receiveContext.getAclManager();
        IntegrateMessages messages = receiveContext.getIntegrate().getMessages();
        List<SubscribeTopic> subscribeTopics = message.getSubscribeTopics()
                    .stream()
                    .filter(subscribeTopic -> aclManager.check(mqttChannel, subscribeTopic.getTopicFilter(), AclAction.SUBSCRIBE))
                    .peek(subscribeTopic -> this.loadRetainMessage(messages, subscribeTopic)).collect(Collectors.toList());
        topics.registryTopic(mqttChannel, subscribeTopics);
        logManager.printInfo(mqttChannel, LogEvent.SUBSCRIBE, LogStatus.SUCCESS, JacksonUtil.bean2Json(message));
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

    private void loadRetainMessage(IntegrateMessages messages, SubscribeTopic topic) {
        messages.getRetainMessage(topic.getTopicFilter())
                    .forEach(retainMessage -> {
                        Optional.ofNullable(topic.getMqttChannel())
                                    .ifPresent(mqttChannel -> {
                                        mqttChannel.sendPublish(topic.minQos(MqttQoS.valueOf(retainMessage.getQos())),
                                                    retainMessage.toPublishMessage());
                                    });
                    });
    }


}
