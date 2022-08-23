package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.message.mqtt.RetryMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.buffer.PooledByteBufAllocator;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class RetryProtocol implements Protocol<RetryMessage> {

    @Override
    public void parseProtocol(RetryMessage retryMessage, MqttChannel mqttChannel, ContextView contextView) {
        mqttChannel.write(MqttMessageUtils.buildPub(
                true,
                retryMessage.getMqttQoS(),
                retryMessage.getMessageId(),
                retryMessage.getTopic(),
                PooledByteBufAllocator.DEFAULT.directBuffer().writeBytes(retryMessage.getBody())));
    }

    @Override
    public Class<RetryMessage> getClassType() {
        return RetryMessage.class;
    }
}
