package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.message.mqtt.PublishAckMessage;
import io.github.quickmsg.common.protocol.Protocol;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class PublishAckProtocol implements Protocol<PublishAckMessage> {

    @Override
    public void parseProtocol(PublishAckMessage message, MqttChannel mqttChannel, ContextView contextView) {
        contextView.get(ReceiveContext.class)
                .getRetryManager().cancelRetry(mqttChannel, message.getMessageId());
    }

    @Override
    public Class<PublishAckMessage> getClassType() {
        return PublishAckMessage.class;
    }


}
