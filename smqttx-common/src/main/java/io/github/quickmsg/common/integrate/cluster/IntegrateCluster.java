package io.github.quickmsg.common.integrate.cluster;

import io.github.quickmsg.common.integrate.IntegrateGetter;
import io.github.quickmsg.common.message.mqtt.ClusterMessage;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author luxurong
 */
public interface IntegrateCluster extends IntegrateGetter {

    /**
     * 获取集群节点信息
     *
     * @return node collection
     */
    Set<String> getClusterNode();


    /**
     * 获取其他集群节点信息
     *
     * @return other node collection
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
    void shutdown();


    /**
     * 停止
     *
     * @param clusterMessage {@link ClusterMessage}
     * @return {@link Mono}
     */
    void sendCluster(ClusterMessage clusterMessage);


}
