package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.log.LogEvent;
import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.log.LogStatus;
import io.github.quickmsg.common.message.mqtt.PublishAckMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.JacksonUtil;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class PublishAckProtocol implements Protocol<PublishAckMessage> {

    @Override
    public void parseProtocol(PublishAckMessage message, MqttChannel mqttChannel, ContextView contextView) {
        ReceiveContext<?> receiveContext =  contextView.get(ReceiveContext.class);
        LogManager logManager = receiveContext.getLogManager();
        logManager.printInfo(mqttChannel, LogEvent.PUBLISH_ACK, LogStatus.SUCCESS, JacksonUtil.bean2Json(message));
        contextView.get(ReceiveContext.class)
                .getRetryManager().cancelRetry(mqttChannel, message.getMessageId());
    }

    @Override
    public Class<PublishAckMessage> getClassType() {
        return PublishAckMessage.class;
    }


}
