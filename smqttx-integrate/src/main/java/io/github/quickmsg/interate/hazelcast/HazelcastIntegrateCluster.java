package io.github.quickmsg.interate.hazelcast;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.MessageListener;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.cluster.IntegrateCluster;
import io.github.quickmsg.common.message.mqtt.ClusterMessage;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Hazelcast implementation of IntegrateCluster
 *
 * @author hxx
 */
public class HazelcastIntegrateCluster implements IntegrateCluster, Serializable {

    private final HazelcastIntegrate hazelcastIntegrate;
    private final HazelcastInstance hazelcastInstance;
    private final Map<String, UUID> topicListeners = new ConcurrentHashMap<>();
    private final HazelcastClusterHandler clusterHandler;

    public HazelcastIntegrateCluster(HazelcastIntegrate hazelcastIntegrate) {
        this.hazelcastIntegrate = hazelcastIntegrate;
        this.hazelcastInstance = hazelcastIntegrate.getHazelcastInstance();
        this.clusterHandler = new HazelcastClusterHandler();
    }

    @Override
    public Set<String> getClusterNode() {
        return hazelcastInstance.getCluster()
                .getMembers()
                .stream()
                .map(member -> member.getAddress().toString())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getOtherClusterNode() {
        Member localMember = hazelcastInstance.getCluster().getLocalMember();
        return hazelcastInstance.getCluster()
                .getMembers()
                .stream()
                .filter(member -> !member.equals(localMember))
                .map(member -> member.getAddress().toString())
                .collect(Collectors.toSet());
    }

    @Override
    public String getLocalNode() {
        return hazelcastInstance.getCluster()
                .getLocalMember()
                .getAddress()
                .getHost();
    }

    @Override
    public void listenTopic(String topic) {
        topicListeners.computeIfAbsent(topic, tp -> {
            ITopic<ClusterMessage> iTopic = hazelcastInstance.getTopic(tp);
            MessageListener<ClusterMessage> listener = message -> 
                clusterHandler.doRemote(message.getMessageObject());
            return iTopic.addMessageListener(listener);
        });
    }

    @Override
    public void stopListenTopic(String topic) {
        UUID listenerId = topicListeners.remove(topic);
        if (listenerId != null) {
            ITopic<ClusterMessage> iTopic = hazelcastInstance.getTopic(topic);
            iTopic.removeMessageListener(listenerId);
        }
    }

    @Override
    public void sendCluster(String topic, ClusterMessage clusterMessage) {
        ITopic<ClusterMessage> iTopic = hazelcastInstance.getTopic(topic);
        iTopic.publish(clusterMessage);
    }

    @Override
    public Integrate getIntegrate() {
        return this.hazelcastIntegrate;
    }
}
