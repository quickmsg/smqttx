package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.log.LogEvent;
import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.log.LogStatus;
import io.github.quickmsg.common.message.mqtt.UnSubscribeMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import io.netty.handler.codec.mqtt.MqttQoS;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class UnSubscribeProtocol implements Protocol<UnSubscribeMessage> {


    @Override
    public void parseProtocol(UnSubscribeMessage message, MqttChannel mqttChannel, ContextView contextView) {
        ReceiveContext<?> receiveContext =  contextView.get(ReceiveContext.class);
        LogManager logManager = receiveContext.getLogManager();
        message.getTopics()
                .forEach(topic ->
                        contextView.get(ReceiveContext.class)
                                .getIntegrate()
                                .getTopics()
                                .removeTopic(mqttChannel,new SubscribeTopic(topic, MqttQoS.AT_MOST_ONCE, mqttChannel)));

        logManager.printInfo(mqttChannel, LogEvent.SUBSCRIBE, LogStatus.SUCCESS, JacksonUtil.bean2Json(message));

        mqttChannel.write(MqttMessageUtils.buildUnsubAck(message.getMessageId()));
    }

    @Override
    public Class<UnSubscribeMessage> getClassType() {
        return UnSubscribeMessage.class;
    }


}
