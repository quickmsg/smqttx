package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.acceptor.UnSubscribeEvent;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.message.mqtt.UnSubscribeMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class UnSubscribeProtocol implements Protocol<UnSubscribeMessage> {


    @Override
    public Mono<Event> parseProtocol(UnSubscribeMessage message, MqttChannel mqttChannel, ContextView contextView) {
        return Mono.fromRunnable(() -> message.getTopics()
                        .forEach(topic ->
                                contextView.get(ReceiveContext.class)
                                        .getIntegrate()
                                        .getTopics()
                                        .removeTopic(topic, new SubscribeTopic(topic, MqttQoS.AT_MOST_ONCE, mqttChannel))))
                .then(mqttChannel.write(MqttMessageUtils.buildUnsubAck(message.getMessageId()), false))
                .thenReturn(new UnSubscribeEvent(EventMsg.UN_SUBSCRIBE_MESSAGE,
                        mqttChannel.getClientIdentifier(),
                        message.getMessageId(),
                        message.getTopics(),
                        System.currentTimeMillis()));
    }

    @Override
    public Class<UnSubscribeMessage> getClassType() {
        return UnSubscribeMessage.class;
    }


}
