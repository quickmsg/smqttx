package io.github.quickmsg.interate;

import io.github.quickmsg.common.integrate.IgniteCacheRegion;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.cache.ConnectCache;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.cluster.IntegrateCluster;
import io.github.quickmsg.common.integrate.job.JobExecutor;
import io.github.quickmsg.common.integrate.msg.IntegrateMessages;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.topic.FixedTopicFilter;
import io.github.quickmsg.common.topic.TreeTopicFilter;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteAtomicLong;
import org.apache.ignite.IgniteSet;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.*;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luxurong
 */
public class IgniteIntegrate implements Integrate {

    private final Ignite ignite;

    private final ProtocolAdaptor protocolAdaptor;

    private final IgniteChannels igniteChannels;

    private final IgniteIntegrateCluster cluster;

    private final IgniteIntegrateTopics integrateTopics;

    private final IgniteMessages igniteMessages;

    private final IgniteExecutor igniteExecutor;

    public IgniteIntegrate(IgniteConfiguration configuration, ProtocolAdaptor protocolAdaptor) {
        this.ignite = Ignition.start(configuration);
        if (!this.ignite.cluster().state().active()) {
            this.ignite.cluster().state(ClusterState.ACTIVE);
        }
        this.protocolAdaptor = protocolAdaptor;
        this.igniteChannels = new IgniteChannels(this, new ConcurrentHashMap<>());
        this.cluster = new IgniteIntegrateCluster(this);
        this.integrateTopics = new IgniteIntegrateTopics(this);
        this.igniteMessages = new IgniteMessages(new FixedTopicFilter<>(), new TreeTopicFilter<>(), this);
        this.igniteExecutor = new IgniteExecutor(ignite.compute(ignite.cluster()));
    }


    @Override
    public IntegrateChannels getChannels() {
        return this.igniteChannels;
    }

    @Override
    public IntegrateCluster getCluster() {
        return this.cluster;
    }

    @Override
    public <K, V> IntegrateCache<K, V> getCache(String cacheName) {
        CacheConfiguration<K, V> configuration =
                    new CacheConfiguration<K, V>()
                                .setName(cacheName);
        return new IgniteIntegrateCache<>(ignite.getOrCreateCache(configuration));
    }

    @Override
    public <K, V> IntegrateCache<K, V> getLocalCache(String cacheName) {
        return getLocalCache(cacheName, false);
    }

    @Override
    public <K, V> IntegrateCache<K, V> getLocalCache(String cacheName, boolean local) {
        CacheMode cacheMode = local ? CacheMode.REPLICATED: CacheMode.PARTITIONED;
        CacheConfiguration<K, V> configuration =
                    new CacheConfiguration<K, V>()
                                .setName(cacheName).setCacheMode(cacheMode);
        return new IgniteIntegrateCache<>(ignite.getOrCreateCache(configuration));
    }

    @Override
    public <K, V> IntegrateCache<K, V> getCache(IgniteCacheRegion igniteCacheRegion) {
        CacheMode cacheMode = igniteCacheRegion.local() ? CacheMode.REPLICATED : CacheMode.PARTITIONED;
        CacheConfiguration<K, V> configuration =
                new CacheConfiguration<K, V>()
                        .setName(igniteCacheRegion.getCacheName())
                        .setCacheMode(cacheMode)
                        .setDataRegionName(igniteCacheRegion.getRegionName())
                        .setCacheMode(igniteCacheRegion.getCacheMode())
                        .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                        .setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC)
                        .setBackups(1)
                        .setEagerTtl(true)
                        .setPartitionLossPolicy(PartitionLossPolicy.READ_ONLY_SAFE)
                        .setIndexedTypes(igniteCacheRegion.getIndexedTypes())
                        .setRebalanceMode(CacheRebalanceMode.ASYNC);
        Optional.ofNullable(igniteCacheRegion.getExpiryPolicyFactory())
                .ifPresent(configuration::setExpiryPolicyFactory);
        return new IgniteIntegrateCache<>(ignite.getOrCreateCache(configuration));
    }



    @Override
    public IntegrateTopics<SubscribeTopic> getTopics() {
        return this.integrateTopics;
    }

    @Override
    public IntegrateMessages getMessages() {
        return this.igniteMessages;
    }

    @Override
    public JobExecutor getJobExecutor() {
        return this.igniteExecutor;
    }

    @Override
    public ProtocolAdaptor getProtocolAdaptor() {
        return this.protocolAdaptor;
    }

    @Override
    public Ignite getIgnite() {
        return this.ignite;
    }

    @Override
    public IgniteAtomicLong getGlobalCounter(String name) {
        return ignite.atomicLong(name, 0, true);
    }


}
