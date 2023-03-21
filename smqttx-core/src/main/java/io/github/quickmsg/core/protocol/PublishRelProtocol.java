package io.github.quickmsg.core.protocol;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.cluster.IntegrateCluster;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.log.LogEvent;
import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.log.LogStatus;
import io.github.quickmsg.common.message.mqtt.ClusterMessage;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.message.mqtt.PublishRelMessage;
import io.github.quickmsg.common.metric.CounterType;
import io.github.quickmsg.common.protocol.Protocol;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.github.quickmsg.common.utils.MqttMessageUtils;
import reactor.util.context.ContextView;

import java.util.Set;

/**
 * @author luxurong
 */
// todo 暂不支持qos2
public class PublishRelProtocol implements Protocol<PublishRelMessage> {
    @Override
    public void parseProtocol(PublishRelMessage message, MqttChannel mqttChannel, ContextView contextView) {
        ReceiveContext<?> receiveContext =  contextView.get(ReceiveContext.class);
        LogManager logManager = receiveContext.getLogManager();
        logManager.printInfo(mqttChannel, LogEvent.PUBLISH_REL, LogStatus.SUCCESS, JacksonUtil.bean2Json(message));
        PublishMessage publishMessage=mqttChannel.sendQos2Cache(message.getMessageId());
        mqttChannel.write(MqttMessageUtils.buildPublishComp(message.getMessageId()));
        if(publishMessage!=null){
            receiveContext.getMetricManager().getMetricRegistry().getMetricCounter(CounterType.PUBLISH_EVENT).increment();
            logManager.printInfo(mqttChannel, LogEvent.PUBLISH, LogStatus.SUCCESS, JacksonUtil.bean2Json(message));
            ClusterMessage clusterMessage = new ClusterMessage(publishMessage);
            IntegrateCluster integrateCluster =receiveContext.getIntegrate().getCluster();
            integrateCluster.sendCluster(clusterMessage.getTopic(), clusterMessage);
            IntegrateTopics<SubscribeTopic> topics=receiveContext.getIntegrate().getTopics();
            if(topics.isWildcard(clusterMessage.getTopic())){
                Set<String> wildcardTopics = topics.getWildcardTopics(clusterMessage.getTopic());
                if (wildcardTopics != null && wildcardTopics.size() > 0) {
                    wildcardTopics.forEach(tp -> {
                        clusterMessage.setTopic(tp);
                        integrateCluster.sendCluster(tp, clusterMessage);
                    });
                }
            }

        }
    }

    @Override
    public Class<PublishRelMessage> getClassType() {
        return PublishRelMessage.class;
    }
}
