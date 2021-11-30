package io.github.quickmsg.interate;

import io.github.quickmsg.common.integrate.IgniteCacheRegion;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.topic.AbstractTopicAggregate;
import io.github.quickmsg.common.topic.FixedTopicFilter;
import io.github.quickmsg.common.topic.TopicFilter;
import io.github.quickmsg.common.topic.TreeTopicFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteAtomicLong;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
@Slf4j
public class IgniteIntegrateTopics extends AbstractTopicAggregate<SubscribeTopic> implements IntegrateTopics<SubscribeTopic> {

    private final static String SUBSCRIBE_PREFIX = "subscriber_";

    private final IgniteIntegrate integrate;

    private final IgniteAtomicLong subscribeNumber;

    private final IgniteAtomicLong atomicName;

    /**
     * clusterNode -> topic -> channelId
     */
    private final IntegrateCache<String, IntegrateCache<String, String>> shareCache;

    private final String clusterNode;

    public IgniteIntegrateTopics(IgniteIntegrate integrate) {
        super(new FixedTopicFilter<>(), new TreeTopicFilter<>());
        this.integrate = integrate;
        this.shareCache = integrate.getCache(IgniteCacheRegion.TOPIC);
        this.clusterNode = integrate.getCluster().getLocalNode();
        this.subscribeNumber = integrate.getIgnite().atomicLong(
                "subscribers", // Atomic long name.
                0,            // Initial value.
                true         // Create if it does not exist.
        );
        this.atomicName = integrate.getIgnite().atomicLong(
                "topics", // Atomic long name.
                0,            // Initial value.
                true         // Create if it does not exist.
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
                            integrate.getCache(SUBSCRIBE_PREFIX + atomicName.incrementAndGet()));
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
        return integrate.getCluster()
                .getOtherClusterNode()
                .stream()
                .filter(node -> Optional.ofNullable(shareCache.get(node))
                        .map(cache -> cache.exist(topicName))
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
    public boolean isWildcard(String topic) {
        return topic.contains(ONE_SYMBOL)
                || topic.contains(MORE_SYMBOL);
    }

    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }
}
