package io.github.quickmsg.interate;

import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.interate1.IgniteKeys;
import io.github.quickmsg.common.interate1.Integrate;
import io.github.quickmsg.common.interate1.cache.IntegrateCache;
import io.github.quickmsg.common.interate1.topic.Topics;
import io.github.quickmsg.common.topic.AbstractTopicAggregate;
import io.github.quickmsg.common.topic.FixedTopicFilter;
import io.github.quickmsg.common.topic.TopicFilter;
import io.github.quickmsg.common.topic.TreeTopicFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteAtomicLong;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
@Slf4j
public class IgniteTopics extends AbstractTopicAggregate<SubscribeTopic> implements Topics<SubscribeTopic> {

    private final static String SUBSCRIBE_PREFIX = "subscriber_";

    private final IgniteIntegrate integrate;

    private final IgniteAtomicLong subscribeNumber;

    private final AtomicInteger count = new AtomicInteger(0);

    /**
     * clusterNode -> topic -> channelId
     */
    private final IntegrateCache<String, IntegrateCache<String, String>> shareCache;

    private final String clusterNode;

    public IgniteTopics(IgniteIntegrate integrate) {
        super(new FixedTopicFilter<>(), new TreeTopicFilter<>());
        this.integrate = integrate;
        this.shareCache = integrate.getCache(IgniteKeys.TOPIC_CACHE_AREA,
                IgniteKeys.TOPIC_PERSISTENCE_AREA);
        this.clusterNode = integrate.getCluster().getLocalNode();
        this.subscribeNumber = integrate.getIgnite().atomicLong(
                "subscribers", // Atomic long name.
                0,            // Initial value.
                false         // Create if it does not exist.
        );
    }

    @Override
    public void registryTopic(String topic, SubscribeTopic subscribeTopic) {
        TopicFilter<SubscribeTopic> filter = checkFilter(subscribeTopic.getTopicFilter());
        if (filter.addObjectTopic(subscribeTopic.getTopicFilter(), subscribeTopic)) {
            subscribeNumber.incrementAndGet();
            subscribeTopic.linkSubscribe();
            IntegrateCache<String, String> subscribeCache =
                    shareCache.getAndPutIfAbsent(clusterNode,
                            integrate.getCache(SUBSCRIBE_PREFIX + count.incrementAndGet()));
            subscribeCache.put(topic, subscribeTopic.getMqttChannel().getClientIdentifier());
        }
    }

    @Override
    public boolean removeTopic(String topic, SubscribeTopic subscribeTopic) {
        TopicFilter<SubscribeTopic> filter = checkFilter(subscribeTopic.getTopicFilter());
        boolean success = filter.removeObjectTopic(subscribeTopic.getTopicFilter(), subscribeTopic);
        if (success) {
            subscribeNumber.decrementAndGet();
            subscribeTopic.linkSubscribe();
            Optional.ofNullable(shareCache.get(clusterNode))
                    .ifPresent(cache -> cache.remove(topic));
        }
        return success;

    }

    @Override
    public Set<SubscribeTopic> getObjectsByTopic(String topicName) {
        Set<SubscribeTopic> subscribeTopics = this.getFixedTopicFilter().getAllObjectsTopic();
        subscribeTopics.addAll(this.getTreeTopicFilter().getAllObjectsTopic());
        return subscribeTopics;
    }

    @Override
    public Set<String> getRemoteTopicsContext(String topicName) {
        return integrate.getIgnite()
                .cluster()
                .nodes()
                .stream()
                .map(cn -> cn.consistentId().toString())
                .filter(node -> Optional.ofNullable(shareCache.get(node))
                        .map(cache -> cache.exist(node))
                        .orElse(false))
                .collect(Collectors.toSet());
    }

    @Override
    public Long counts() {
        return subscribeNumber.get();
    }

    @Override
    public void clearTopics(String node) {
        IntegrateCache<String, String> cache = this.shareCache.get(node);
        if (cache != null) {
            cache.clear();
            cache.close();
        }
    }

    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }
}
