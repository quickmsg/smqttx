package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.integrate.topic.TopicRegistry;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author luxurong
 */
public class UnSubscribeProtocol implements Protocol<MqttUnsubscribeMessage> {

    private final static List<MqttMessageType> MESSAGE_TYPE_LIST = new ArrayList<>();

    static {
        MESSAGE_TYPE_LIST.add(MqttMessageType.UNSUBSCRIBE);
    }


    @Override
    public Mono<Void> parseProtocol(SmqttMessage<MqttUnsubscribeMessage> smqttMessage , MqttChannel mqttChannel, ContextView contextView) {
        MqttUnsubscribeMessage message = smqttMessage.getMessage();
        return Mono.fromRunnable(() -> {
            ReceiveContext<?> receiveContext = contextView.get(ReceiveContext.class);
            TopicRegistry topicRegistry = receiveContext.getTopicRegistry();
            message.payload()
                    .topics()
                    .stream()
                    // 随机设置一个MqttQoS 用于删除topic订阅
                    .map(topic -> new SubscribeTopic(topic, MqttQoS.AT_MOST_ONCE, mqttChannel))
                    .forEach(topicRegistry::removeSubscribeTopic);
        }).then(mqttChannel.write(MqttMessageUtils.buildUnsubAck(message.variableHeader().messageId()), false));
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return MESSAGE_TYPE_LIST;
    }


}
