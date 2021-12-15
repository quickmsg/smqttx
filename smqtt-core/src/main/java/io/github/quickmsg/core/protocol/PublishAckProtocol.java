package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.acceptor.PublicAckEvent;
import io.github.quickmsg.common.message.mqtt.PublishAckMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import io.netty.handler.codec.mqtt.MqttMessageType;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class PublishAckProtocol implements Protocol<PublishAckMessage> {

    @Override
    public Mono<Event> parseProtocol(PublishAckMessage message, MqttChannel mqttChannel, ContextView contextView) {
        return mqttChannel.cancelRetry(MqttMessageType.PUBLISH, message.getMessageId())
                .thenReturn(new PublicAckEvent(EventMsg.PUBLISH_ACK_MESSAGE,
                        System.currentTimeMillis()
                        , mqttChannel.getClientIdentifier(), message.getMessageId()));
    }

    @Override
    public Class<PublishAckMessage> getClassType() {
        return PublishAckMessage.class;
    }


}
