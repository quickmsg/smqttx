package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.acceptor.UnSubscribeEvent;
import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.message.SmqttMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import io.github.quickmsg.common.utils.MqttMessageUtils;
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
    public Mono<Event> parseProtocol(SmqttMessage<MqttUnsubscribeMessage> smqttMessage, MqttChannel mqttChannel, ContextView contextView) {
        MqttUnsubscribeMessage message = smqttMessage.getMessage();
        return Mono.fromRunnable(() -> message.payload()
                        .topics()
                        .forEach(topic ->
                                contextView.get(ReceiveContext.class)
                                        .getIntegrate()
                                        .getTopics()
                                        .removeTopic(topic, new SubscribeTopic(topic, MqttQoS.AT_MOST_ONCE, mqttChannel))))
                .then(mqttChannel.write(MqttMessageUtils.buildUnsubAck(message.variableHeader().messageId()), false))
                .thenReturn(new UnSubscribeEvent(EventMsg.SUBSCRIBE_MESSAGE,
                        mqttChannel.getClientIdentifier(),
                        message.variableHeader().messageId(),
                        message.payload().topics(),
                        System.currentTimeMillis()));
    }

    @Override
    public List<MqttMessageType> getMqttMessageTypes() {
        return MESSAGE_TYPE_LIST;
    }


}
