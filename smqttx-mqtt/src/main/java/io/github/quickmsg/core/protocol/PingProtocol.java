package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.log.LogEvent;
import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.log.LogStatus;
import io.github.quickmsg.common.message.mqtt.PingMessage;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.JacksonUtil;
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
        ReceiveContext<?> receiveContext =  contextView.get(ReceiveContext.class);
        LogManager logManager = receiveContext.getLogManager();
        logManager.printInfo(mqttChannel, LogEvent.PING, LogStatus.SUCCESS, JacksonUtil.bean2Json(message));
        mqttChannel.write(MqttMessageUtils.buildPongMessage());
    }

    @Override
    public Class<PingMessage> getClassType() {
        return PingMessage.class;
    }
}
