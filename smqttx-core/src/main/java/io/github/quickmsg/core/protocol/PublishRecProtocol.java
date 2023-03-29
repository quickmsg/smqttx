package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.log.LogEvent;
import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.log.LogStatus;
import io.github.quickmsg.common.message.mqtt.PublishRecMessage;
import io.github.quickmsg.common.metric.CounterType;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class PublishRecProtocol implements Protocol<PublishRecMessage> {

    @Override
    public void parseProtocol(PublishRecMessage message, MqttChannel mqttChannel, ContextView contextView) {
        ReceiveContext<?> receiveContext =  contextView.get(ReceiveContext.class);
        LogManager logManager = receiveContext.getLogManager();
        logManager.printWarn(mqttChannel, LogEvent.PUBLISH_REC, LogStatus.SUCCESS, JacksonUtil.bean2Json(message));
        contextView.get(ReceiveContext.class).getRetryManager().cancelRetry(mqttChannel, message.getMessageId());
        receiveContext.getMetricManager().getMetricRegistry().getMetricCounter(CounterType.PUBLISH_EVENT).increment();
        mqttChannel.write(MqttMessageUtils.buildPublishRel(message.getMessageId()));
    }

    @Override
    public Class<PublishRecMessage> getClassType() {
        return PublishRecMessage.class;
    }
}
