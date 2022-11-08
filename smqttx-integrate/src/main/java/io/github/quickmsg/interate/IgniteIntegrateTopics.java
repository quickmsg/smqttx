package io.github.quickmsg.interate;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.context.ContextHolder;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.metric.CounterType;
import io.github.quickmsg.common.utils.TopicRegexUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteSet;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CollectionConfiguration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
@Slf4j
public class IgniteIntegrateTopics implements IntegrateTopics<SubscribeTopic> {

    private final IgniteIntegrate integrate;

    private final IgniteSet<String> shareCache;

    @Getter
    private final Map<String, Set<SubscribeTopic>> topicSubscribers;

    protected static final String ONE_SYMBOL = "+";

    protected static final String MORE_SYMBOL = "#";


    public boolean checkFilter(String topicFilter) {
        return topicFilter.contains(ONE_SYMBOL);
    }

    public IgniteIntegrateTopics(IgniteIntegrate integrate) {
        this.integrate = integrate;
        this.shareCache = integrate.getIgnite().set("wildcard",
                    new CollectionConfiguration().setCacheMode(CacheMode.PARTITIONED)
                                .setAtomicityMode(CacheAtomicityMode.ATOMIC)
                                .setCollocated(true));
        this.topicSubscribers = new ConcurrentHashMap<>();
    }

    @Override
    public void registryTopic(MqttChannel mqttChannel, List<SubscribeTopic> subscribeTopics) {
        subscribeTopics.forEach(subscribeTopic -> this.registryTopic(mqttChannel, subscribeTopic));
    }

    @Override
    public void registryTopic(MqttChannel mqttChannel, SubscribeTopic subscribeTopic) {
        Set<SubscribeTopic> subscribeTopicSet = topicSubscribers.computeIfAbsent(subscribeTopic.getTopicFilter(), topic -> new CopyOnWriteArraySet<>());
        String topic = subscribeTopic.getTopicFilter();
        if (subscribeTopicSet.add(subscribeTopic)) {
            ContextHolder.getReceiveContext().getMetricManager().getMetricRegistry().getMetricCounter(CounterType.SUBSCRIBE).increment();
            ContextHolder.getReceiveContext().getMetricManager().getMetricRegistry().getMetricCounter(CounterType.SUBSCRIBE_EVENT).increment();
            integrate.getCluster().listenTopic(topic);
            mqttChannel.getTopics().add(subscribeTopic);
            if (isWildcard(topic)) {
                shareCache.add(topic);
            }
        }
    }


    @Override
    public void removeTopic(MqttChannel mqttChannel, SubscribeTopic subscribeTopic) {
        topicSubscribers.compute(subscribeTopic.getTopicFilter(), (topic, subscribeTopicSet) -> {
            if (subscribeTopicSet == null || subscribeTopicSet.size() < 1) {
                this.clearCache(subscribeTopic.getTopicFilter());
            } else {
                if (subscribeTopicSet.remove(subscribeTopic)) {
                    ContextHolder.getReceiveContext().getMetricManager().getMetricRegistry().getMetricCounter(CounterType.SUBSCRIBE).decrement();
                    ContextHolder.getReceiveContext().getMetricManager().getMetricRegistry().getMetricCounter(CounterType.UN_SUBSCRIBE_EVENT).increment();
                    if ((subscribeTopicSet.size() < 1)) {
                        this.clearCache(subscribeTopic.getTopicFilter());
                    }
                }
            }
            mqttChannel.getTopics().remove(subscribeTopic);
            return subscribeTopicSet;
        });

    }

    private void clearCache(String topic) {
        integrate.getCluster().stopListenTopic(topic);
        if (isWildcard(topic)) {
            shareCache.remove(topic);
        }
    }

    @Override
    public void removeTopic(MqttChannel mqttChannel, List<SubscribeTopic> topics) {
        for (int i = 0; i < topics.size(); i++) {
            this.removeTopic(mqttChannel, topics.get(i));
        }
    }

    @Override
    public Set<SubscribeTopic> getMqttChannelsByTopic(String topic) {
        return topicSubscribers.get(topic);
    }

    @Override
    public Long counts() {
        return null;
    }

    @Override
    public boolean isWildcard(String topic) {
        return topic.contains(ONE_SYMBOL) || topic.contains(MORE_SYMBOL);
    }

    @Override
    public Set<String> getWildcardTopics(String topic) {
        return shareCache.stream().filter(tp->topic.matches(TopicRegexUtils.regexTopic(tp))).collect(Collectors.toSet());
    }

    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }
}
