package io.github.quickmsg.interate;

import io.github.quickmsg.common.cluster.ClusterNode;
import io.github.quickmsg.common.enums.ClusterStatus;
import io.github.quickmsg.common.interate1.cluster.Cluster;
import io.github.quickmsg.common.message.HeapMqttMessage;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author luxurong
 */
public class IgniteCluster implements Cluster {

    static Ignite ignite;

    static IgniteCache<String,String> igniteCache;


    @Override
    public Flux<HeapMqttMessage> handlerClusterMessage() {
        return null;
    }

    @Override
    public Flux<ClusterStatus> clusterEvent() {
        return null;
    }

    @Override
    public List<ClusterNode> getClusterNode() {
        return null;
    }

    @Override
    public Mono<Void> spreadMessage(HeapMqttMessage heapMqttMessage) {
        return null;
    }

    @Override
    public Mono<Void> shutdown() {
        return null;
    }
}
