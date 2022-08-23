package io.github.quickmsg.interate;

import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.cluster.IntegrateCluster;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.message.mqtt.ClusterMessage;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.lang.IgniteBiPredicate;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
public class IgniteIntegrateCluster implements IntegrateCluster {

    private final IgniteIntegrate igniteIntegrate;

    private final IgniteMessaging message;

    private final org.apache.ignite.IgniteCluster igniteCluster;

    public IgniteIntegrateCluster(IgniteIntegrate igniteIntegrate, org.apache.ignite.IgniteCluster igniteCluster) {
        this.igniteIntegrate = igniteIntegrate;
        this.message = igniteIntegrate.getIgnite().message();
        this.igniteCluster = igniteCluster;
        message.localListen(this.getLocalNode(), (IgniteBiPredicate<UUID, Object>) this::doRemote);
    }


    @Override
    public Set<String> getClusterNode() {
        return igniteCluster
                .nodes()
                .stream()
                .map(clusterNode -> clusterNode.consistentId().toString())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getOtherClusterNode() {
        return igniteCluster
                .nodes()
                .stream()
                .filter(clusterNode -> clusterNode != igniteCluster.localNode())
                .map(clusterNode -> clusterNode.consistentId().toString())
                .collect(Collectors.toSet());
    }

    @Override
    public String getLocalNode() {
        return igniteIntegrate.getIgnite().cluster().localNode().consistentId().toString();
    }


    @Override
    public void shutdown() {
        message.stopLocalListen(this.getLocalNode(),(uuid, o) -> true);
    }

    @Override
    public void sendCluster(ClusterMessage clusterMessage) {
        IntegrateTopics<SubscribeTopic> topics = igniteIntegrate.getTopics();
        String topic = clusterMessage.getTopic();
        Set<String> otherNodes = topics.isWildcard(topic) ?
                this.getOtherClusterNode() : topics.getRemoteTopicsContext(topic);
        otherNodes.forEach(node -> message.send(node, clusterMessage));
    }

    private boolean doRemote(UUID uuid, Object o) {
        ClusterMessage clusterMessage = (ClusterMessage) o;
        igniteIntegrate.getProtocolAdaptor().chooseProtocol(clusterMessage.toPublishMessage());
        return true;
    }

    @Override
    public Integrate getIntegrate() {
        return this.igniteIntegrate;
    }


}
