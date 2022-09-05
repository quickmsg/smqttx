package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.mqtt.CloseMessage;
import io.github.quickmsg.common.protocol.Protocol;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class CloseProtocol implements Protocol<CloseMessage> {

    @Override
    public void parseProtocol(CloseMessage message, MqttChannel mqttChannel, ContextView contextView) {

    }

    @Override
    public Class<CloseMessage> getClassType() {
        return CloseMessage.class;
    }
}
