package io.github.quickmsg.interate;

import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.interate1.IgniteKeys;
import io.github.quickmsg.common.interate1.Integrate;
import io.github.quickmsg.common.interate1.cache.IntegrateCache;
import io.github.quickmsg.common.interate1.channel.IntegrateChannels;
import io.github.quickmsg.common.interate1.cluster.IntegrateCluster;
import io.github.quickmsg.common.interate1.job.JobExecutor;
import io.github.quickmsg.common.interate1.msg.IntegrateMessages;
import io.github.quickmsg.common.interate1.topic.Topics;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.topic.FixedTopicFilter;
import io.github.quickmsg.common.topic.TreeTopicFilter;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luxurong
 */
public class IgniteIntegrate implements Integrate {

    private final Ignite ignite;

    private final ProtocolAdaptor protocolAdaptor;

    public IgniteIntegrate(IgniteConfiguration configuration, ProtocolAdaptor protocolAdaptor) {
        this.ignite = Ignition.start(configuration);
        this.protocolAdaptor = protocolAdaptor;
    }


    @Override
    public IntegrateChannels getChannels() {
        return new IgniteChannels(this, new ConcurrentHashMap<>(),
                getCache(IgniteKeys.CHANNEL_CACHE_NAME, IgniteKeys.CHANNEL_PERSISTENCE_AREA));
    }

    @Override
    public IntegrateCluster getCluster() {
        return new IgniteIntegrateCluster(this, ignite.cluster());
    }

    @Override
    public <K, V> IntegrateCache<K, V> getCache(String cacheName) {
        CacheConfiguration<K, V> configuration =
                new CacheConfiguration<K, V>().setName(cacheName);
        return new IgniteIntegrateCache<>(ignite.getOrCreateCache(configuration));
    }

    @Override
    public <K, V> IntegrateCache<K, V> getCache(String cacheName, String setDataRegionName) {
        CacheConfiguration<K, V> configuration =
                new CacheConfiguration<K, V>().setName(cacheName).setDataRegionName(setDataRegionName)
                        .setAtomicityMode(CacheConfiguration.DFLT_CACHE_ATOMICITY_MODE)
                        .setCacheMode(CacheMode.PARTITIONED)
                        .setBackups(1)
                        .setRebalanceMode(CacheRebalanceMode.ASYNC);
        return new IgniteIntegrateCache<>(ignite.getOrCreateCache(configuration));
    }

    @Override
    public Topics<SubscribeTopic> getTopics() {
        return new IgniteTopics(this);
    }

    @Override
    public IntegrateMessages getMessage() {
        return new IgniteMessages(new FixedTopicFilter<>(), new TreeTopicFilter<>(), this);
    }

    @Override
    public JobExecutor getJobExecutor() {
        return new IgniteExecutor(ignite.compute(ignite.cluster()));
    }

    @Override
    public ProtocolAdaptor getProtocolAdaptor() {
        return this.protocolAdaptor;
    }

    @Override
    public Ignite getIgnite() {
        return this.ignite;
    }


}
