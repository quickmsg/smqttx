package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.message.mqtt.CLoseMessage;
import io.github.quickmsg.common.protocol.Protocol;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class CloseProtocol implements Protocol<CLoseMessage> {


    @Override
    public Mono<Event> parseProtocol(CLoseMessage message, MqttChannel mqttChannel, ContextView contextView) {
        return null;
    }
}
