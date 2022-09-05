package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.message.mqtt.PingMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
@Slf4j
public class PingProtocol implements Protocol<PingMessage> {

    @Override
    public void parseProtocol(PingMessage message, MqttChannel mqttChannel, ContextView contextView) {
        mqttChannel.write(MqttMessageUtils.buildPongMessage());
    }

    @Override
    public Class<PingMessage> getClassType() {
        return PingMessage.class;
    }
}
