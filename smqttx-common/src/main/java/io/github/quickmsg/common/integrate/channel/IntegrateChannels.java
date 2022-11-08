package io.github.quickmsg.common.integrate.channel;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.integrate.IntegrateGetter;
import io.github.quickmsg.common.integrate.cache.ConnectCache;
import io.github.quickmsg.common.message.mqtt.ConnectMessage;
import io.github.quickmsg.common.sql.ConnectionQueryModel;
import io.github.quickmsg.common.sql.PageRequest;
import io.github.quickmsg.common.sql.PageResult;

import java.util.Collection;

/**
 * @author luxurong
 */
public interface IntegrateChannels extends IntegrateGetter {



    /**
     * 注册通道
     *
     * @param clientIdentifier 客户端id
     * @param mqttChannel      {@link MqttChannel old}
     */
    void add(String clientIdentifier, MqttChannel mqttChannel);

    /**
     * 判读通道是否存在
     *
     * @param clientIdentifier 客户端id
     * @return 布尔
     */
    boolean exists(String clientIdentifier);


    /**
     * 获取通道
     *
     * @param clientIdentifier 客户端id
     * @return MqttChannel
     */
    MqttChannel get(String clientIdentifier);


    /**
     * 获取通道计数
     *
     * @return 通道数
     */
    Integer counts();


    /**
     * 获取说有channel信息
     *
     * @return {@link Collection}
     */
    Collection<MqttChannel> getChannels();


    /**
     * 移除说有channel信息
     * @param mqttChannel {@link MqttChannel old}
     */
    void remove(MqttChannel mqttChannel);


    PageResult<ConnectCache> queryConnectionSql(ConnectionQueryModel model);
}
