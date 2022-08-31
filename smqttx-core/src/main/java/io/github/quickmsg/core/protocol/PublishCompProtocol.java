package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.event.Event;
import io.github.quickmsg.common.message.mqtt.PublishCompMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.EventMsg;
import io.netty.handler.codec.mqtt.MqttMessageType;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class PublishCompProtocol implements Protocol<PublishCompMessage> {

    @Override
    // todo 暂不支持qos2
    public void parseProtocol(PublishCompMessage message, MqttChannel mqttChannel, ContextView contextView) {
//        int compId = message.getMessageId();
//        return mqttChannel.cancelRetry(MqttMessageType.PUBREL, compId)
//                .thenReturn(build(EventMsg.PUB_COMP_MESSAGE,
//                        mqttChannel.getConnectMessage().getClientId(),
//                        compId));
    }

    @Override
    public Class<PublishCompMessage> getClassType() {
        return PublishCompMessage.class;
    }
}
