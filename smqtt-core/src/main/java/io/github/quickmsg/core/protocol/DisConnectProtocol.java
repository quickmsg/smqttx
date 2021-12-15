package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.message.mqtt.DisConnectMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class DisConnectProtocol implements Protocol<DisConnectMessage> {


    @Override
    public Mono<Event> parseProtocol(DisConnectMessage message, MqttChannel mqttChannel, ContextView contextView) {
        return Mono.fromSupplier(() -> {
            mqttChannel.setWill(null);
            Connection connection;
            if (!(connection = mqttChannel.getConnection()).isDisposed()) {
                connection.dispose();
            }
            return build(EventMsg.DIS_CONNECT_MESSAGE, mqttChannel.getClientIdentifier(), 0);
        });
    }

    @Override
    public Class<DisConnectMessage> getClassType() {
        return DisConnectMessage.class;
    }


}
