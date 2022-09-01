package io.github.quickmsg.interate;

import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.cluster.IntegrateCluster;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.message.mqtt.ClusterMessage;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.utils.JacksonUtil;
import org.apache.ignite.IgniteMessaging;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
public class IgniteIntegrateCluster implements IntegrateCluster {

    private final IgniteIntegrate igniteIntegrate;

    private final IgniteMessaging message;

    private final org.apache.ignite.IgniteCluster igniteCluster;

    private Map<String, UUID> fixedListener = new ConcurrentHashMap<>();

    public IgniteIntegrateCluster(IgniteIntegrate igniteIntegrate) {
        this.igniteIntegrate = igniteIntegrate;
        this.message = igniteIntegrate.getIgnite().message();
        this.igniteCluster = igniteIntegrate.getIgnite().cluster();
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
        return igniteIntegrate.getIgnite().cluster().localNode().id().toString();
    }



    @Override
    public void listenTopic(String topic) {
        fixedListener.computeIfAbsent(topic,tp->message.remoteListen(tp,this::doRemote));
    }

    @Override
    public void stopListenTopic(String topic) {
        Optional.ofNullable(fixedListener.remove(topic))
                    .ifPresent(message::stopRemoteListenAsync);
    }

    @Override
    public void sendCluster(PublishMessage publishMessage) {
        message.send(publishMessage.getTopic(),JacksonUtil.bean2Json(publishMessage));
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
