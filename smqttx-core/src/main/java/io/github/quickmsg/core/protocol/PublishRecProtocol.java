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
import reactor.util.context.ContextView;

/**
 * @author luxurong
 */
public class PublishRecProtocol implements Protocol<PublishRecMessage> {

    // todo 暂不支持qos2
    @Override
    public void parseProtocol(PublishRecMessage message, MqttChannel mqttChannel, ContextView contextView) {
        ReceiveContext<?> receiveContext =  contextView.get(ReceiveContext.class);
        LogManager logManager = receiveContext.getLogManager();
        logManager.printWarn(mqttChannel, LogEvent.PUBLISH_ACK, LogStatus.SUCCESS,"unSupport qos2 "+JacksonUtil.bean2Json(message));
        receiveContext.getMetricManager().getMetricRegistry().getMetricCounter(CounterType.PUBLISH_EVENT).increment();

//        int messageId = message.getMessageId();
//        return mqttChannel.cancelRetry(MqttMessageType.PUBLISH, messageId)
//                .then(mqttChannel.write(MqttMessageUtils.buildPublishRel(messageId), true))
//                .thenReturn(build(EventMsg.PUB_REC_MESSAGE,
//                        mqttChannel.getConnectMessage().getClientId(),
//                        messageId));
    }

    @Override
    public Class<PublishRecMessage> getClassType() {
        return PublishRecMessage.class;
    }
}
