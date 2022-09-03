package io.github.quickmsg.interate;

import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.message.mqtt.ClusterMessage;
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
        for(SubscribeTopic subscribeTopic:channels){
            subscribeTopic.getMqttChannel().sendPublish(MqttQoS.valueOf(clusterMessage.getQos()),clusterMessage.toPublishMessage());
        }
        return true;
    }

}
