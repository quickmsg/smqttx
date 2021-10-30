package io.github.quickmsg.interate;

import io.github.quickmsg.common.channel.MqttChannel;
import io.github.quickmsg.common.interate1.Integrate;
import io.github.quickmsg.common.interate1.cache.IntegrateCache;
import io.github.quickmsg.common.interate1.channel.IntegrateChannels;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luxurong
 */
public class IgniteChannels implements IntegrateChannels {


    private final ConcurrentHashMap<String, MqttChannel> mqttChannelCache;

    private final IntegrateCache<String, String> shareCache;

    private final IgniteIntegrate integrate;

    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }

    public IgniteChannels(IgniteIntegrate integrate, ConcurrentHashMap<String, MqttChannel> mqttChannelCache, IntegrateCache<String, String> integrateCache) {
        this.integrate = integrate;
        this.mqttChannelCache = mqttChannelCache;
        this.shareCache = integrateCache;
    }



}
