package io.github.quickmsg.common.interate1;

import io.github.quickmsg.common.interate1.cache.ShareCache;
import io.github.quickmsg.common.interate1.cluster.Cluster;
import io.github.quickmsg.common.interate1.msg.Message;
import io.github.quickmsg.common.interate1.topic.Topics;

/**
 * @author luxurong
 */
public interface Integrate {

    /**
     * @return {@link Cluster }
     */
    Cluster getCluster();

    /**
     * @return {@link ShareCache support memory or Persistence }
     */
    <K, V> ShareCache<K, V> getCache(String cacheName);

    <K, V> ShareCache<K, V> getCache(String cacheName, Integer Size);


    <K, V> ShareCache<K, V> getCache(String cacheName, Integer Size, Boolean persistence);


    /**
     * @return {@link Topics manager topic }
     */
    Topics getTopics();

    /**
     * @return {@link Message manager message }
     */
    Message getMessage();
}
