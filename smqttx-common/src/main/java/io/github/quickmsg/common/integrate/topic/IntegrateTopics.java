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
    void registryTopic(MqttChannel mqttChannel,List<T> t);

    /**
     * registry topic object
     *
     * @param t     body
     */
    void registryTopic(MqttChannel mqttChannel,T t);


    /**
     * remove
     *
     * @param t     body
     * @return result
     */
    void removeTopic(MqttChannel mqttChannel,T t);


    /**
     * remove
     *
     * @param t     body
     * @return result
     */
    void removeTopic(MqttChannel mqttChannel,List<T>  t);


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
