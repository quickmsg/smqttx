package io.github.quickmsg.interate;

import cn.hutool.core.collection.CollectionUtil;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.context.ReceiveContext;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.log.LogEvent;
import io.github.quickmsg.common.log.LogManager;
import io.github.quickmsg.common.log.LogStatus;
import io.github.quickmsg.common.message.mqtt.ClusterMessage;
import io.github.quickmsg.common.utils.JacksonUtil;
import io.netty.handler.codec.mqtt.MqttQoS;

import javax.naming.Context;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * @author luxurong
 */
public class ClusterHandler implements Serializable {


    public boolean doRemote(UUID uuid, Object o) {
        ClusterMessage clusterMessage = (ClusterMessage) o;
        Set<SubscribeTopic> channels = ContextHolder.getReceiveContext().getIntegrate().getTopics()
                    .getMqttChannelsByTopic(clusterMessage.getTopic());
        LogManager logManager = ContextHolder.getReceiveContext().getLogManager();

        if(CollectionUtil.isNotEmpty(channels)){
            String msg = JacksonUtil.bean2Json(clusterMessage);
            for(SubscribeTopic subscribeTopic:channels){
                logManager.printInfo(subscribeTopic.getMqttChannel(), LogEvent.WRITE, LogStatus.SUCCESS,msg);
                subscribeTopic.getMqttChannel().sendPublish(MqttQoS.valueOf(clusterMessage.getQos()),clusterMessage.toPublishMessage());
            }
        }
        return true;
    }

}
