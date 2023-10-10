package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.log.LogEvent;
import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.log.LogStatus;
import io.github.quickmsg.common.message.mqtt.PublishCompMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.JacksonUtil;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class PublishCompProtocol implements Protocol<PublishCompMessage> {

    @Override
    public void parseProtocol(PublishCompMessage message, MqttChannel mqttChannel, ContextView contextView) {
        ReceiveContext<?> receiveContext =  contextView.get(ReceiveContext.class);
        LogManager logManager = receiveContext.getLogManager();
        logManager.printWarn(mqttChannel, LogEvent.PUBLISH_COMP, LogStatus.SUCCESS,JacksonUtil.bean2Json(message));
    }

    @Override
    public Class<PublishCompMessage> getClassType() {
        return PublishCompMessage.class;
    }
}
