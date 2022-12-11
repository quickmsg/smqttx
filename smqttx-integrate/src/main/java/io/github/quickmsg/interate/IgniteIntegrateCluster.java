package io.github.quickmsg.interate;

import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.cluster.IntegrateCluster;
import io.github.quickmsg.common.message.mqtt.ClusterMessage;
import io.github.quickmsg.common.utils.ServerUtils;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.lang.IgniteBiPredicate;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
public class IgniteIntegrateCluster implements IntegrateCluster, Serializable {

    private final IgniteIntegrate igniteIntegrate;

    private final IgniteMessaging message;

    private final org.apache.ignite.IgniteCluster igniteCluster;

    private final Map<String, IgniteBiPredicate<UUID, ?>> fixedListener = new ConcurrentHashMap<>();


    private final ClusterHandler clusterHandler;

    public IgniteIntegrateCluster(IgniteIntegrate igniteIntegrate) {
        this.igniteIntegrate = igniteIntegrate;
        this.message = igniteIntegrate.getIgnite().message();
        this.igniteCluster = igniteIntegrate.getIgnite().cluster();
        this.clusterHandler = new ClusterHandler();
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
        return igniteIntegrate.getIgnite().cluster().localNode().addresses().stream().findFirst().orElse(ServerUtils.serverIp);
    }


    @Override
    public void listenTopic(String topic) {
        fixedListener.computeIfAbsent(topic, tp -> {
                    IgniteBiPredicate<UUID, ?> p = clusterHandler::doRemote;
                    message.localListen(tp, p);
                    return p;
                }
        );
    }

    @Override
    public void stopListenTopic(String topic) {
        Optional.ofNullable(fixedListener.remove(topic))
                .ifPresent(o -> message.stopLocalListen(topic, o));
    }

    @Override
    public void sendCluster(String topic, ClusterMessage clusterMessage) {
        message.send(topic, clusterMessage);
    }


    @Override
    public Integrate getIntegrate() {
        return this.igniteIntegrate;
    }

}
