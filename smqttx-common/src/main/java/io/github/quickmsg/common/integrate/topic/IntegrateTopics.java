package io.github.quickmsg.common.integrate.topic;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.IntegrateGetter;

import java.util.List;
import java.util.Set;

/**
 * @author luxurong
 */
public interface IntegrateTopics<T> extends IntegrateGetter {


    /**
     * registry topic object
     *
     * @param t     body
     */
    void registryTopic(List<T> t);

    /**
     * registry topic object
     *
     * @param t     body
     */
    void registryTopic(T t);


    /**
     * remove
     *
     * @param t     body
     * @return result
     */
    void removeTopic( T t);


    /**
     * remove
     *
     * @param t     body
     * @return result
     */
    void removeTopic( List<T>  t);


    /**
     * get all topic object
     *
     * @param topicName topic name
     * @return {@link SubscribeTopic}
     */
    Set<T> getObjectsByTopic(String topicName);


    /**
     * this get fixed topic remote message
     *
     * @param topicName topic name
     * @return cluster node
     */
    Set<String> getRemoteTopicsContext(String topicName);

    /**
     * get all count
     *
     * @return counts
     */
    Long counts();



    /**
     * clear node all count
     *
     =     */
    boolean isWildcard(String topic);

}
