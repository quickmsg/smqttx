package io.github.quickmsg.common.topic;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
public class FixedTopicFilter<T> implements TopicFilter<T> {

    private final Map<String, CopyOnWriteArraySet<T>> topicChannels = new ConcurrentHashMap<>();


    @Override
    public Set<T> getObjectByTopic(String topic) {
        return topicChannels.computeIfAbsent(topic, t -> new CopyOnWriteArraySet<>());
    }

    @Override
    public boolean addObjectTopic(String topicFilter, T t) {
        CopyOnWriteArraySet<T> channels =
                topicChannels.computeIfAbsent(
                        topicFilter,
                        topic -> new CopyOnWriteArraySet<>());
       return channels.add(t);
    }


    @Override
    public boolean removeObjectTopic(String topic, T t) {
        CopyOnWriteArraySet<T> channels = topicChannels.get(topic);
        if (channels != null && channels.size() > 0) {
            return channels.remove(t);
        } else return false;
    }

    @Override
    public Set<T> getAllObjectsTopic() {
        return topicChannels
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
