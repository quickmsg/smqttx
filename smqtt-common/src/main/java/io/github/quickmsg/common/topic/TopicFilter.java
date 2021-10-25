package io.github.quickmsg.common.topic;

import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.Set;

/**
 * @author luxurong
 */
public interface TopicFilter<T> {

    /**
     * 获取订阅topic
     *
     * @param topic   topic
     * @return {@link SubscribeTopic}
     */
    Set<T> getObjectByTopic(String topic);


    /**
     * 保存订阅topic
     *
     * @param topicFilter topicFilter
     * @param t           filter result
     */
    boolean addObjectTopic(String topicFilter, T t);


    /**
     * 保存订阅topic
     *
     * @param topic  topic
     * @param t filter result
     * @return bool
     */
    boolean removeObjectTopic(String topic, T t);


    /**
     * 获取订所有订阅topic
     *
     * @return {@link SubscribeTopic}
     */
    Set<T> getAllObjectsTopic();





}
