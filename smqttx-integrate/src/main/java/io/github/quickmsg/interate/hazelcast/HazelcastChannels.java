package io.github.quickmsg.interate.hazelcast;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.cache.ConnectCache;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.job.CloseJob;
import io.github.quickmsg.common.sql.ConnectionQueryModel;
import io.github.quickmsg.common.sql.PageResult;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxx
 */
@Slf4j
public class HazelcastChannels implements IntegrateChannels {

    private final ConcurrentHashMap<String, MqttChannel> localChannelCache;

    private final IntegrateCache<Integer, ConnectCache> shareChannelCache;

    private final HazelcastIntegrate integrate;

    private final CloseJob closeConnectJob = new CloseJob();

    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }

    public HazelcastChannels(HazelcastIntegrate integrate, ConcurrentHashMap<String, MqttChannel> channelMap) {
        this.integrate = integrate;
        this.localChannelCache = channelMap;
        // 使用字符串常量替代IgniteCacheRegion枚举
        this.shareChannelCache = this.integrate.getCache("channel_cache");
    }

    @Override
    public void add(String clientIdentifier, MqttChannel mqttChannel) {
        Collection<Boolean> closeJobs = integrate.getJobExecutor().callBroadcast(this.closeConnectJob, clientIdentifier);
        localChannelCache.put(clientIdentifier, mqttChannel);
        this.shareChannelCache.put(mqttChannel.getId(), mqttChannel.getConnectCache());
    }

    @Override
    public boolean exists(String clientIdentifier) {
        return localChannelCache.containsKey(clientIdentifier);
    }

    @Override
    public MqttChannel get(String clientIdentifier) {
        return localChannelCache.get(clientIdentifier);
    }

    @Override
    public Integer counts() {
        return localChannelCache.size();
    }

    @Override
    public Collection<MqttChannel> getChannels() {
        return localChannelCache.values();
    }

    @Override
    public MqttChannel remove(MqttChannel mqttChannel) {
        localChannelCache.remove(mqttChannel.getClientId(), mqttChannel);
        shareChannelCache.remove(mqttChannel.getId());
        return mqttChannel;
    }

    @Override
    public PageResult<ConnectCache> queryConnectionSql(ConnectionQueryModel model) {
        // 实现Hazelcast的查询逻辑，替代原来的SQL查询
        return new PageResult<>();
    }
}