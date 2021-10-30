package io.github.quickmsg.interate;

import io.github.quickmsg.common.enums.ClusterStatus;
import io.github.quickmsg.common.interate1.Integrate;
import io.github.quickmsg.common.interate1.cluster.IntegrateCluster;
import io.github.quickmsg.common.message.HeapMqttMessage;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.lang.IgniteBiPredicate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
public class IgniteIntegrateCluster implements IntegrateCluster {

    private final IgniteIntegrate igniteIntegrate;

    private final IgniteMessaging message;

    private final Sinks.Many<HeapMqttMessage> heapMqttMessageMany = Sinks.many().multicast().onBackpressureBuffer();

    private final org.apache.ignite.IgniteCluster igniteCluster;

    public IgniteIntegrateCluster(IgniteIntegrate igniteIntegrate, org.apache.ignite.IgniteCluster igniteCluster) {
        this.igniteIntegrate = igniteIntegrate;
        this.message = igniteIntegrate.getIgnite().message();
        this.igniteCluster = igniteCluster;
        message.localListen(igniteCluster.localNode().id().toString(), (IgniteBiPredicate<UUID, Object>) this::apply);
    }

    @Override
    public Flux<HeapMqttMessage> handlerClusterMessage() {
        return heapMqttMessageMany.asFlux();
    }

    @Override
    public Flux<ClusterStatus> clusterEvent() {
        return null;
    }

    @Override
    public Set<String> getCluster() {
        return igniteCluster
                .nodes()
                .stream()
                .map(clusterNode -> clusterNode.id().toString())
                .collect(Collectors.toSet());
    }

    @Override
    public String getLocalNode() {
        return null;
    }

    @Override
    public Mono<Void> spreadMessage(HeapMqttMessage heapMqttMessage) {
        return Mono.fromRunnable(() -> message.send(igniteCluster.localNode().id().toString(), heapMqttMessage));
    }

    @Override
    public Mono<Void> shutdown() {
        return Mono.fromRunnable(() ->
                message.remoteListen(igniteCluster.localNode().id().toString(), (IgniteBiPredicate<UUID, Object>) (uuid, o) -> true));
    }

    private boolean apply(UUID uuid, Object o) {
        return heapMqttMessageMany.tryEmitNext((HeapMqttMessage) o).isSuccess();
    }

    @Override
    public Integrate getIntegrate() {
        return this.igniteIntegrate;
    }
}
