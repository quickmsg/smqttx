package io.github.quickmsg.common.interate1.topic;

import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.interate1.IntegrateGetter;

import java.util.Set;

/**
 * @author luxurong
 */
public interface Topics<T> extends IntegrateGetter {


    /**
     * registry topic object
     *
     * @param t     body
     * @param topic topic
     */
    void registryTopic(String topic, T t);


    /**
     * remove
     *
     * @param t     body
     * @param topic topic
     * @return result
     */
    boolean removeTopic(String topic, T t);


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

}
