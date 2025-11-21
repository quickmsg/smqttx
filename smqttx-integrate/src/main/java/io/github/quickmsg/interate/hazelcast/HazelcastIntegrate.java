package io.github.quickmsg.interate.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.map.IMap;
import io.github.quickmsg.common.integrate.CacheRegion;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.SubscribeTopic;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.cluster.IntegrateCluster;
import io.github.quickmsg.common.integrate.job.JobExecutor;
import io.github.quickmsg.common.integrate.msg.IntegrateMessages;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import io.github.quickmsg.common.topic.FixedTopicFilter;
import io.github.quickmsg.common.topic.TreeTopicFilter;


import java.util.concurrent.ConcurrentHashMap;

/**
 * Hazelcast implementation of Integrate interface
 *
 * @author hxx
 */
public class HazelcastIntegrate implements Integrate {

    private final HazelcastInstance hazelcastInstance;

    private final ProtocolAdaptor protocolAdaptor;

    private final HazelcastChannels hazelcastChannels;

    private final HazelcastIntegrateCluster cluster;

    private final HazelcastIntegrateTopics integrateTopics;

    private final HazelcastMessages hazelcastMessages;

    private final HazelcastExecutor hazelcastExecutor;

    public HazelcastIntegrate(Config configuration, ProtocolAdaptor protocolAdaptor) {
        this.hazelcastInstance = Hazelcast.newHazelcastInstance(configuration);
        this.protocolAdaptor = protocolAdaptor;
        this.hazelcastChannels = new HazelcastChannels(this, new ConcurrentHashMap<>());
        this.cluster = new HazelcastIntegrateCluster(this);
        this.integrateTopics = new HazelcastIntegrateTopics(this);
        this.hazelcastMessages = new HazelcastMessages(new FixedTopicFilter<>(), new TreeTopicFilter<>(), this);
        this.hazelcastExecutor = new HazelcastExecutor(hazelcastInstance.getExecutorService("default"));
    }

    @Override
    public IntegrateChannels getChannels() {
        return this.hazelcastChannels;
    }

    @Override
    public IntegrateCluster getCluster() {
        return this.cluster;
    }

    @Override
    public <K, V> IntegrateCache<K, V> getCache(String cacheName) {
        IMap<K, V> map = hazelcastInstance.getMap(cacheName);
        return new HazelcastIntegrateCache<>(map);
    }

    @Override
    public <K, V> IntegrateCache<K, V> getLocalCache(String cacheName) {
        return getLocalCache(cacheName, false);
    }

    @Override
    public <K, V> IntegrateCache<K, V> getLocalCache(String cacheName, boolean local) {
        // In Hazelcast, local caching can be achieved by configuring map with no backups
        IMap<K, V> map = hazelcastInstance.getMap(cacheName);
        return new HazelcastIntegrateCache<>(map);
    }

    public <K, V> IntegrateCache<K, V> getCache(CacheRegion cacheRegion) {
        String cacheName = cacheRegion.getCacheName();
        
        // Configure map based on region settings
        MapConfig mapConfig = new MapConfig(cacheName);
        mapConfig.setBackupCount(1); // Equivalent to Ignite's setBackups(1)
        mapConfig.setAsyncBackupCount(0);
        mapConfig.setReadBackupData(true); // Equivalent to READ_ONLY_SAFE partition loss policy
        
        // Add map config to hazelcast instance config if not exists
        Config config = hazelcastInstance.getConfig();
        if (config.getMapConfig(cacheName) == null) {
            config.addMapConfig(mapConfig);
        }
        
        IMap<K, V> map = hazelcastInstance.getMap(cacheName);
        return new HazelcastIntegrateCache<>(map);
    }

    @Override
    public IntegrateTopics<SubscribeTopic> getTopics() {
        return this.integrateTopics;
    }

    @Override
    public IntegrateMessages getMessages() {
        return this.hazelcastMessages;
    }

    @Override
    public JobExecutor getJobExecutor() {
        return this.hazelcastExecutor;
    }

    @Override
    public ProtocolAdaptor getProtocolAdaptor() {
        return this.protocolAdaptor;
    }

    @Override
    public HazelcastInstance getDistributedSystem() {
        return this.hazelcastInstance;
    }

    @Override
    public IAtomicLong getGlobalCounter(String name) {
        // Return a Hazelcast IAtomicLong as an Object
        return hazelcastInstance.getCPSubsystem().getAtomicLong(name);
    }

    /**
     * Get the underlying HazelcastInstance
     * 
     * @return HazelcastInstance
     */
    public HazelcastInstance getHazelcastInstance() {
        return this.hazelcastInstance;
    }

    /**
     * Shutdown the Hazelcast instance
     */
    public void shutdown() {
        if (hazelcastInstance != null) {
            hazelcastInstance.shutdown();
        }
    }
}