package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.message.mqtt.ClusterMessage;
import io.github.quickmsg.common.protocol.Protocol;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class ClusterProtocol implements Protocol<ClusterMessage> {

    @Override
    public Mono<Event> parseProtocol(ClusterMessage message, MqttChannel mqttChannel, ContextView contextView) {
        return Mono.fromRunnable(()->{
            ReceiveContext<?> receiveContext = contextView.get(ReceiveContext.class);
            receiveContext.getIntegrate().getCluster().sendCluster(message);
        }) ;
    }

    @Override
    public Class<ClusterMessage> getClassType() {
        return ClusterMessage.class;
    }
}
