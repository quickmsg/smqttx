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

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
@Slf4j
public class IgniteIntegrateTopics extends AbstractTopicAggregate<SubscribeTopic> implements IntegrateTopics<SubscribeTopic> {

    private final IgniteIntegrate integrate;

    private final AtomicLong subscribeNumber = new AtomicLong(0);

    // topic -> node
    private final IntegrateCache<String,Set<String>> shareCache;

    //tree topic count
    private final Map<String,Integer> treeCount ;



    private final String clusterNode;


    public IgniteIntegrateTopics(IgniteIntegrate integrate) {
        super(new FixedTopicFilter<>(), new TreeTopicFilter<>());
        this.integrate = integrate;
        this.shareCache = integrate.getCache(IgniteCacheRegion.TOPIC);
        this.clusterNode = integrate.getCluster().getLocalNode();
        this.treeCount = new HashMap<>();
    }

    @Override
    public void registryTopic(List<SubscribeTopic> subscribeTopics) {
        subscribeTopics.forEach(this::registryTopic);
    }

    @Override
    public void registryTopic(SubscribeTopic subscribeTopic) {
        String topic = subscribeTopic.getTopicFilter();
        TopicFilter<SubscribeTopic> filter = checkFilter(topic);
        if (filter.addObjectTopic(topic, subscribeTopic)) {
            if (filter instanceof TreeTopicFilter) {
                Lock lock =shareCache.lock(topic);
                try {
                    lock.lock();
                    Integer count = this.treeCount.getOrDefault(topic,0);
                    this.treeCount.put(topic,++count);
                    Set<String> shareNodes = shareCache.get(topic);
                    if(shareNodes == null){
                        shareNodes = new HashSet<>();
                        shareCache.put(topic,shareNodes);
                    }
                    shareNodes.add(clusterNode);
                    shareCache.put(topic,shareNodes);
                }finally {
                    lock.unlock();
                }

            }
            subscribeNumber.incrementAndGet();
            subscribeTopic.linkSubscribe();
        }
    }


    @Override
    public void removeTopic(SubscribeTopic subscribeTopic) {
        String topic = subscribeTopic.getTopicFilter();
        TopicFilter<SubscribeTopic> filter = checkFilter(topic);
        if (filter.removeObjectTopic(topic, subscribeTopic)) {
            if (filter instanceof TreeTopicFilter) {
                Lock lock =shareCache.lock(topic);
                try {
                    lock.lock();
                    Integer count = this.treeCount.getOrDefault(topic,0);
                    if(--count<1){
                        Set<String> shareNodes = shareCache.get(topic);
                        if(shareNodes != null && shareNodes.size()>0){
                            shareNodes.remove(clusterNode);
                            shareCache.put(topic,shareNodes);
                        }
                    }
                }finally {
                    lock.unlock();
                }
            }
            subscribeNumber.decrementAndGet();
            subscribeTopic.unLinkSubscribe();
        }

    }

    @Override
    public void removeTopic(List<SubscribeTopic> topics) {
        topics.forEach(this::removeTopic);
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
                        .map(cache -> cache.contains(topicName))
                        .orElse(false))
                .collect(Collectors.toSet());
    }

    @Override
    public Long counts() {
        return subscribeNumber.get();
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
