package io.github.quickmsg.common.spi.registry;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.enums.ChannelEvent;

/**
 * @author luxurong
 */
public interface EventRegistry {


    /**
     * message
     *
     * @param event       {@link ChannelEvent}
     * @param mqttChannel {@link MqttChannel}
     * @param body        {@link Object}
     * @param receiveContext {@link ReceiveContext}
     */
    void registry(ChannelEvent event, MqttChannel mqttChannel, Object body, ReceiveContext<?> receiveContext);

}
