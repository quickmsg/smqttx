package io.github.quickmsg.common.spi.registry;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.enums.ChannelEvent;
import io.github.quickmsg.common.message.Message;

/**
 * @author luxurong
 */
@Deprecated
public interface EventRegistry {


    /**
     * message
     *
     * @param event          {@link ChannelEvent}
     * @param mqttChannel    {@link MqttChannel}
     * @param message        {@link Message}
     * @param receiveContext {@link ReceiveContext}
     */
    void registry(ChannelEvent event, MqttChannel mqttChannel, Message message, ReceiveContext<?> receiveContext);

}
