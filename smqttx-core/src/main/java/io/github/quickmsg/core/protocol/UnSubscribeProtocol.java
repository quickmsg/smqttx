package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.message.mqtt.UnSubscribeMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class UnSubscribeProtocol implements Protocol<UnSubscribeMessage> {


    @Override
    public void parseProtocol(UnSubscribeMessage message, MqttChannel mqttChannel, ContextView contextView) {
        message.getTopics()
                .forEach(topic ->
                        contextView.get(ReceiveContext.class)
                                .getIntegrate()
                                .getTopics()
                                .removeTopic(mqttChannel,new SubscribeTopic(topic, MqttQoS.AT_MOST_ONCE, mqttChannel.getClientId())));
        mqttChannel.write(MqttMessageUtils.buildUnsubAck(message.getMessageId()));
    }

    @Override
    public Class<UnSubscribeMessage> getClassType() {
        return UnSubscribeMessage.class;
    }


}
