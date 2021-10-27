package io.github.quickmsg.interate;

import io.github.quickmsg.common.interate1.Integrate;
import io.github.quickmsg.common.interate1.cache.ShareCache;
import io.github.quickmsg.common.interate1.cluster.Cluster;
import io.github.quickmsg.common.interate1.job.JobExecutor;
import io.github.quickmsg.common.interate1.msg.Message;
import io.github.quickmsg.common.interate1.topic.Topics;
import io.github.quickmsg.common.topic.FixedTopicFilter;
import io.github.quickmsg.common.topic.TreeTopicFilter;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;

/**
 * @author luxurong
 */
public class IgniteIntegrate implements Integrate {

    private final IgniteConfiguration configuration;

    private final Ignite ignite;

    public IgniteIntegrate(IgniteConfiguration configuration) {
        this.configuration = configuration;
        this.ignite = Ignition.start(configuration);
    }


    @Override
    public Cluster getCluster() {
        return new IgniteCluster();
    }

    @Override
    public <K, V> ShareCache<K, V> getCache(String cacheName) {
        CacheConfiguration<K, V> configuration =
                new CacheConfiguration<K, V>().setName(cacheName);
        return new IgniteShareCache<>(ignite.getOrCreateCache(configuration));
    }

    @Override
    public <K, V> ShareCache<K, V> getCache(String cacheName, String setDataRegionName) {
        CacheConfiguration<K, V> configuration =
                new CacheConfiguration<K, V>().setName(cacheName).setDataRegionName(setDataRegionName);
        return new IgniteShareCache<>(ignite.getOrCreateCache(configuration));
    }

    @Override
    public Topics getTopics() {
        return new IgniteTopics();
    }

    @Override
    public Message getMessage() {
        return new IgniteMessage(new FixedTopicFilter<>(), new TreeTopicFilter<>());
    }

    @Override
    public JobExecutor getJobExecutor() {
        return new IgniteExecutor(ignite.compute(ignite.cluster()));
    }

}
