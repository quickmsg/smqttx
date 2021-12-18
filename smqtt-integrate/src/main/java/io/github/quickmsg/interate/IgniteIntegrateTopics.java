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
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicLong;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteSet;
import org.apache.ignite.configuration.CollectionConfiguration;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
@Slf4j
public class IgniteIntegrateTopics extends AbstractTopicAggregate<SubscribeTopic> implements IntegrateTopics<SubscribeTopic> {

    private final IgniteIntegrate integrate;

    private final AtomicLong subscribeNumber = new AtomicLong(0);

    /**
     * clientId -> topic
     */
    private final IntegrateCache<String, IgniteSet<String>> shareCache;

    private final String clusterNode;


    private final IgniteCompute igniteCompute;

    private final String PREFIX_SET = "client_sets:";



    public IgniteIntegrateTopics(IgniteIntegrate integrate) {
        super(new FixedTopicFilter<>(), new TreeTopicFilter<>());
        this.integrate = integrate;
        this.shareCache = integrate.getCache(IgniteCacheRegion.TOPIC);
        this.clusterNode = integrate.getCluster().getLocalNode();
        this.igniteCompute=integrate.getIgnite().compute();
    }

    @Override
    public void registryTopic(String topic, SubscribeTopic subscribeTopic) {
        TopicFilter<SubscribeTopic> filter = checkFilter(subscribeTopic.getTopicFilter());
        if (filter.addObjectTopic(subscribeTopic.getTopicFilter(), subscribeTopic)) {
            IgniteSet<String> topics=  shareCache.getAndPutIfAbsent(clusterNode,
                    integrate
                            .getIgnite()
                            .set(PREFIX_SET+clusterNode, new CollectionConfiguration().setBackups(1)));
            if(topics == null){
                topics =  shareCache.get(clusterNode);
            }
            topics.add(topic);
            subscribeNumber.incrementAndGet();
            subscribeTopic.linkSubscribe();
        }
    }

    @Override
    public boolean removeTopic(String topic, SubscribeTopic subscribeTopic) {
        TopicFilter<SubscribeTopic> filter = checkFilter(subscribeTopic.getTopicFilter());
        boolean success = filter.removeObjectTopic(subscribeTopic.getTopicFilter(), subscribeTopic);
        if (success) {
            filter.removeObjectTopic(topic,subscribeTopic);
            IgniteSet<String> topics = shareCache.get(clusterNode);
            if(topics!=null){
                topics.remove(topic);
            }
            subscribeNumber.decrementAndGet();
            subscribeTopic.unLinkSubscribe();
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
                        .map(cache -> cache.contains(topicName))
                        .orElse(false))
                .collect(Collectors.toSet());
    }

    @Override
    public Long counts() {
        return subscribeNumber.get();
    }

    @Override
    public void clearTopics(String node) {
        IgniteSet<String> cache = this.shareCache.get(node);
        if (cache != null) {
            cache.clear();
            cache.close();
            this.shareCache.remove(node);
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
