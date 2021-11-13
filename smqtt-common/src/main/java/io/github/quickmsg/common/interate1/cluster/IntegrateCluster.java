package io.github.quickmsg.common.interate1.cluster;

import io.github.quickmsg.common.cluster.ClusterNode;
import io.github.quickmsg.common.event.message.PublishEvent;
import io.github.quickmsg.common.interate1.IntegrateGetter;
import io.github.quickmsg.common.message.HeapMqttMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author luxurong
 */
public interface IntegrateCluster extends IntegrateGetter {

    /**
     * 获取集群节点信息
     *
     * @return {@link ClusterNode}
     */
    Set<String> getClusterNode();


    /**
     * 获取其他集群节点信息
     *
     * @return {@link ClusterNode}
     */
    Set<String> getOtherClusterNode();


    /**
     * acquire local node id
     *
     * @return String
     */
    String getLocalNode();


    /**
     * 停止
     *
     * @return {@link Mono}
     */
    Mono<Void> shutdown();


}
