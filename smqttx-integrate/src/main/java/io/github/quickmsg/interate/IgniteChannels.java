package io.github.quickmsg.interate;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.integrate.IgniteCacheRegion;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.cache.ConnectCache;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.job.CloseJob;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luxurong
 */
public class IgniteChannels implements IntegrateChannels {


    private final ConcurrentHashMap<String, MqttChannel> localChannelCache;

    private final IntegrateCache<Integer, ConnectCache> shareChannelCache;

    private final IgniteIntegrate integrate;

    private final CloseJob closeConnectJob = new CloseJob();

    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }

    public IgniteChannels(IgniteIntegrate integrate, ConcurrentHashMap<String, MqttChannel> channelMap) {
        this.integrate = integrate;
        this.localChannelCache = channelMap;
        this.shareChannelCache = this.integrate.getCache(IgniteCacheRegion.CHANNEL);
    }


    @Override
    public void add(String clientIdentifier, MqttChannel mqttChannel) {
        Collection<Boolean> closeJobs = integrate.getJobExecutor().callBroadcast(this.closeConnectJob,clientIdentifier);
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
    public void remove(MqttChannel mqttChannel) {
        localChannelCache.remove(mqttChannel.getClientId(), mqttChannel);
        shareChannelCache.remove(mqttChannel.getId());
    }
}
