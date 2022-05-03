package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.event.acceptor.PublicAckEvent;
import io.github.quickmsg.common.message.mqtt.PublishAckMessage;
import io.github.quickmsg.common.protocol.Protocol;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class PublishAckProtocol implements Protocol<PublishAckMessage> {

    @Override
    public Mono<Event> parseProtocol(PublishAckMessage message, MqttChannel mqttChannel, ContextView contextView) {
        return Mono.fromRunnable(() -> contextView.get(ReceiveContext.class)
                        .getRetryManager().cancelRetry(mqttChannel, message.getMessageId()))
                .thenReturn(new PublicAckEvent(System.currentTimeMillis(),
                        mqttChannel.getConnectMessage().getClientId(), message.getMessageId()));
    }

    @Override
    public Class<PublishAckMessage> getClassType() {
        return PublishAckMessage.class;
    }


}
