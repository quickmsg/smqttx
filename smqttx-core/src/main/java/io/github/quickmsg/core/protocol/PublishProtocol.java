package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.acl.AclAction;
import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.cluster.IntegrateCluster;
import io.github.quickmsg.common.integrate.msg.IntegrateMessages;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.message.RetainMessage;
import io.github.quickmsg.common.message.mqtt.ClusterMessage;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.Optional;
import java.util.Set;

/**
 * @author luxurong
 */
@Slf4j
public class PublishProtocol implements Protocol<PublishMessage> {


    @Override
    public void parseProtocol(PublishMessage message, MqttChannel mqttChannel, ContextView contextView) {
        ReceiveContext<?> receiveContext = contextView.get(ReceiveContext.class);

        IntegrateTopics<SubscribeTopic> topics = receiveContext.getIntegrate().getTopics();
        IntegrateCluster integrateCluster = receiveContext.getIntegrate().getCluster();
        IntegrateMessages messages = receiveContext.getIntegrate().getMessages();
        AclManager aclManager = receiveContext.getAclManager();
        Set<SubscribeTopic> mqttChannels = topics.getMqttChannelsByTopic(message.getTopic());
        if (!aclManager.check(mqttChannel, message.getTopic(), AclAction.PUBLISH)) {
            log.warn("mqtt【{}】publish topic 【{}】 acl not authorized ", mqttChannel.getConnectCache(), message.getTopic());
            return;
        }
        if (message.isRetain()) {
            messages.saveRetainMessage(RetainMessage.of(message));
        }
        ClusterMessage clusterMessage = new ClusterMessage(message);
        integrateCluster.sendCluster(clusterMessage.getTopic(), clusterMessage);
        Set<String> wildcardTopics = topics.getWildcardTopics(clusterMessage.getTopic());
        if (wildcardTopics != null && wildcardTopics.size() > 0) {
            wildcardTopics.forEach(tp -> {
                integrateCluster.sendCluster(tp, clusterMessage);
            });
        }

    }

    @Override
    public Class<PublishMessage> getClassType() {
        return PublishMessage.class;
    }


    /**
     * 通用发送消息
     *
     * @param subscribeTopics {@link SubscribeTopic}
     * @param message         {@link PublishMessage}
     * @param other           {@link Mono}
     */
    private void send(Set<SubscribeTopic> subscribeTopics, PublishMessage message, Mono<Void> other) {
        subscribeTopics
                    .forEach(subscribeTopic -> {
                                    subscribeTopic.getMqttChannel()
                                                .sendPublish(subscribeTopic.minQos(MqttQoS.valueOf(message.getQos())), message);
                                }
                    );
        other.subscribe();
    }


    /**
     * 过滤保留消息
     *
     * @param message  {@link PublishMessage}
     * @param messages {@link IntegrateMessages}
     * @return Mono
     */
    private Mono<Void> filterRetainMessage(PublishMessage message, IntegrateMessages messages) {
        return Mono.fromRunnable(() -> {
            if (message.isRetain()) {
                messages.saveRetainMessage(RetainMessage.of(message));
            }
        });
    }


}
