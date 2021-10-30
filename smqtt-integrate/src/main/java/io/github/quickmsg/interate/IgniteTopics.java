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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;

/**
 * @author luxurong
 */
@Slf4j
public class IgniteTopics extends AbstractTopicAggregate<SubscribeTopic> implements Topics<SubscribeTopic> {

    private final LongAdder subscribeNumber = new LongAdder();

    private final IgniteIntegrate integrate;

    private final IntegrateCache<String, Map<String, Integer>> shareCache;

    private final String clusterNode;

    public IgniteTopics(IgniteIntegrate integrate) {
        super(new FixedTopicFilter<>(), new TreeTopicFilter<>());
        this.integrate = integrate;
        this.shareCache = integrate.getCache(IgniteKeys.TOPIC_CACHE_AREA, IgniteKeys.TOPIC_CACHE_AREA);
        this.clusterNode = integrate.getCluster().getLocalNode();
    }

    @Override
    public void registryTopic(String topic, SubscribeTopic subscribeTopic) {
        TopicFilter<SubscribeTopic> filter = checkFilter(subscribeTopic.getTopicFilter());
        if (filter.addObjectTopic(subscribeTopic.getTopicFilter(), subscribeTopic)) {
            subscribeNumber.increment();
            subscribeTopic.linkSubscribe();
            Lock lock = shareCache.lock(topic);
            try {
                lock.lock();
                Map<String, Integer> subscribeCounts =
                        shareCache.getAndPutIfAbsent(topic, new HashMap<>());
                Integer number;
                if ((number = subscribeCounts.get(clusterNode)) != null) {
                    number += 1;
                } else {
                    number = 1;
                }
                subscribeCounts.put(clusterNode, number);
                shareCache.put(topic, subscribeCounts);
            } finally {
                lock.lock();
            }
        }
    }

    @Override
    public boolean removeTopic(String topic, SubscribeTopic subscribeTopic) {
        TopicFilter<SubscribeTopic> filter = checkFilter(subscribeTopic.getTopicFilter());
        boolean success = filter.removeObjectTopic(subscribeTopic.getTopicFilter(), subscribeTopic);
        if (success) {
            subscribeNumber.decrement();
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
        return new HashSet<>(shareCache.get(topicName).keySet());
    }

    @Override
    public Integer counts() {
        return subscribeNumber.intValue();
    }

    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }
}
