package io.github.quickmsg.core.spi;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.integrate.topic.TopicRegistry;
import io.github.quickmsg.common.topic.AbstractTopicAggregate;
import io.github.quickmsg.common.topic.FixedTopicFilter;
import io.github.quickmsg.common.topic.TopicFilter;
import io.github.quickmsg.common.topic.TreeTopicFilter;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
@Slf4j
public class DefaultTopicRegistry extends AbstractTopicAggregate<SubscribeTopic> implements TopicRegistry {

    private final LongAdder subscribeNumber = new LongAdder();

    public DefaultTopicRegistry() {
        super(new FixedTopicFilter<>(), new TreeTopicFilter<>());
    }


    @Override
    public void registrySubscribeTopic(String topicFilter, MqttChannel mqttChannel, MqttQoS qos) {
        this.registrySubscribeTopic(new SubscribeTopic(topicFilter, qos, mqttChannel));
    }

    @Override
    public void registrySubscribeTopic(SubscribeTopic subscribeTopic) {
        TopicFilter<SubscribeTopic> filter = checkFilter(subscribeTopic.getTopicFilter());
        if (filter.addObjectTopic(subscribeTopic.getTopicFilter(), subscribeTopic)) {
            subscribeNumber.increment();
            subscribeTopic.linkSubscribe();

        }
    }


    @Override
    public void clear(MqttChannel mqttChannel) {
        Set<SubscribeTopic> topics = mqttChannel.getTopics();
        if (log.isDebugEnabled()) {
            log.info("mqttChannel channel {} clear topics {}", mqttChannel, topics);
        }
        topics.forEach(this::removeSubscribeTopic);
    }


    @Override
    public void removeSubscribeTopic(SubscribeTopic subscribeTopic) {
        TopicFilter<SubscribeTopic> filter = checkFilter(subscribeTopic.getTopicFilter());
        if (filter.removeObjectTopic(subscribeTopic.getTopicFilter(), subscribeTopic)) {
            subscribeNumber.decrement();
            subscribeTopic.unLinkSubscribe();
        }
    }


    @Override
    public Set<SubscribeTopic> getSubscribesByTopic(String topicName, MqttQoS qos) {
        Set<SubscribeTopic> subscribeTopics = this.getFixedTopicFilter().getObjectByTopic(topicName);
        subscribeTopics.addAll(this.getTreeTopicFilter().getObjectByTopic(topicName));
        return subscribeTopics;
    }

    @Override
    public void registrySubscribesTopic(Set<SubscribeTopic> mqttTopicSubscriptions) {
        mqttTopicSubscriptions.forEach(this::registrySubscribeTopic);
    }


    @Override
    public Map<String, Set<MqttChannel>> getAllTopics() {
        Set<SubscribeTopic> subscribeTopics = this.getFixedTopicFilter().getAllObjectsTopic();
        subscribeTopics.addAll(this.getTreeTopicFilter().getAllObjectsTopic());
        return subscribeTopics
                .stream()
                .collect(Collectors.groupingBy(
                        SubscribeTopic::getTopicFilter,
                        Collectors.mapping(SubscribeTopic::getMqttChannel, Collectors.toSet())));
    }

    @Override
    public Integer counts() {
        return subscribeNumber.intValue();
    }


}
