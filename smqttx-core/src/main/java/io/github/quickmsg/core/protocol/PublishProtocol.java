package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.acl.AclAction;
import io.github.quickmsg.common.acl.AclManager;
import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
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
        try {
            IntegrateTopics<SubscribeTopic> topics = receiveContext.getIntegrate().getTopics();
            IntegrateChannels channels = receiveContext.getIntegrate().getChannels();
            IntegrateMessages messages = receiveContext.getIntegrate().getMessages();
            AclManager aclManager = receiveContext.getAclManager();
            Set<SubscribeTopic> mqttChannels = topics.getObjectsByTopic(message.getTopic());

            if (!aclManager.check(mqttChannel, message.getTopic(), AclAction.PUBLISH)) {
                log.warn("mqtt【{}】publish topic 【{}】 acl not authorized ", mqttChannel.getConnectMessage(), message.getTopic());
                return;
            }
            if (mqttChannel == null) {
                // cluster message
                send(channels, mqttChannels, message, filterRetainMessage(message, messages));
                return;
            }
            switch (MqttQoS.valueOf(message.getQos())) {
                case AT_MOST_ONCE:
                    send(channels, mqttChannels, message, filterRetainMessage(message, messages));
                    break;
                case EXACTLY_ONCE:
                case AT_LEAST_ONCE:
                default:
                    send(channels, mqttChannels, message, Mono.fromRunnable(() -> mqttChannel.write(MqttMessageUtils.buildPublishAck(message.getMessageId()))));
                    break;
            }
        } catch (Exception e) {
            log.error("error ", e);
        } finally {
            if (mqttChannel != null && receiveContext.isCluster()) {
                receiveContext.getIntegrate().getCluster().sendCluster(message);
            }
        }
    }

    @Override
    public Class<PublishMessage> getClassType() {
        return PublishMessage.class;
    }


    /**
     * 通用发送消息
     *
     * @param channels
     * @param subscribeTopics {@link SubscribeTopic}
     * @param message         {@link PublishMessage}
     * @param other           {@link Mono}
     */
    private void send(IntegrateChannels channels, Set<SubscribeTopic> subscribeTopics, PublishMessage message, Mono<Void> other) {
        subscribeTopics
                    .forEach(subscribeTopic -> {
                                    Optional.ofNullable(channels.get(subscribeTopic.getClientId()))
                                                .ifPresent(mqttChannel -> {
                                                    mqttChannel.sendPublish(subscribeTopic.minQos(MqttQoS.valueOf(message.getQos())), message);

                                                });
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
