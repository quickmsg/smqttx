package io.github.quickmsg.interate;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.enums.ChannelStatus;
import io.github.quickmsg.common.interate1.Integrate;
import io.github.quickmsg.common.interate1.cache.IntegrateCache;
import io.github.quickmsg.common.interate1.channel.IntegrateChannels;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luxurong
 */
public class IgniteChannels implements IntegrateChannels {


    private final ConcurrentHashMap<String, MqttChannel> channelMap;

    private final IntegrateCache<String, String> shareCache;

    private final IgniteIntegrate integrate;

    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }

    public IgniteChannels(IgniteIntegrate integrate, ConcurrentHashMap<String, MqttChannel> channelMap, IntegrateCache<String, String> integrateCache) {
        this.integrate = integrate;
        this.channelMap = channelMap;
        this.shareCache = integrateCache;
    }


    @Override
    public void close(MqttChannel mqttChannel) {
        Optional.ofNullable(mqttChannel.getClientIdentifier())
                .ifPresent(channelMap::remove);
    }

    @Override
    public void registry(String clientIdentifier, MqttChannel mqttChannel) {
        channelMap.put(clientIdentifier, mqttChannel);
    }

    @Override
    public boolean exists(String clientIdentifier) {
        return channelMap.containsKey(clientIdentifier) && channelMap.get(clientIdentifier).getStatus() == ChannelStatus.ONLINE;
    }

    @Override
    public MqttChannel get(String clientIdentifier) {
        return channelMap.get(clientIdentifier);
    }

    @Override
    public Integer counts() {
        return channelMap.size();
    }

    @Override
    public Collection<MqttChannel> getChannels() {
        return channelMap.values();
    }
}
