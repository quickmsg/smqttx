package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.message.mqtt.PublishRelMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.Set;

/**
 * @author luxurong
 */
public class PublishRelProtocol implements Protocol<PublishRelMessage> {
    @Override
    public Mono<Event> parseProtocol(PublishRelMessage message, MqttChannel mqttChannel, ContextView contextView) {
        int id = message.getMessageId();
        /*
         * 判断是不是缓存qos2消息
         *       是： 走消息分发 & 回复 comp消息
         *       否： 直接回复 comp消息
         */
        // todo 处理持久化 && 集群

        return mqttChannel.removeQos2Msg(id)
                .map(msg -> {
                    ReceiveContext<?> receiveContext = contextView.get(ReceiveContext.class);
                    IntegrateTopics<SubscribeTopic> topics = receiveContext.getIntegrate().getTopics();
                    Set<SubscribeTopic> subscribeTopics = topics.getObjectsByTopic(msg.getTopic());

                    return Mono.fromRunnable(() ->
                                    subscribeTopics.forEach(subscribeTopic -> {
                                                MqttQoS qoS = subscribeTopic.minQos(subscribeTopic.getQoS());
                                                subscribeTopic.getMqttChannel()
                                                        .write(msg.buildMqttMessage(qoS,
                                                                subscribeTopic.getMqttChannel().generateMessageId()), qoS.value() > 0).subscribe();
                                            }
                                    ))
                            .then(mqttChannel.cancelRetry(MqttMessageType.PUBREC, id))
                            .then(mqttChannel.write(MqttMessageUtils.buildPublishComp(id), false))
                            .thenReturn(build(EventMsg.PUB_REL_MESSAGE,
                                    mqttChannel.getClientIdentifier(),
                                    id));
                }).orElseGet(() -> mqttChannel.write(MqttMessageUtils.buildPublishComp(id), false)
                        .thenReturn(build(EventMsg.PUB_REL_MESSAGE,
                                mqttChannel.getClientIdentifier(),
                                id)));
    }

    @Override
    public Class<PublishRelMessage> getClassType() {
        return PublishRelMessage.class;
    }
}
