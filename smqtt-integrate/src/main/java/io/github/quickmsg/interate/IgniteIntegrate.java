package io.github.quickmsg.interate;

import io.github.quickmsg.common.config.Configuration;
import io.github.quickmsg.common.interate1.Integrate;
import io.github.quickmsg.common.interate1.cache.ShareCache;
import io.github.quickmsg.common.interate1.cluster.Cluster;
import io.github.quickmsg.common.interate1.msg.Message;
import io.github.quickmsg.common.interate1.topic.Topics;
import io.github.quickmsg.common.topic.FixedTopicFilter;
import io.github.quickmsg.common.topic.TreeTopicFilter;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Collections;

/**
 * @author luxurong
 */
public class IgniteIntegrate implements Integrate {

    private final Configuration configuration;

    private final Ignite ignite;

    public IgniteIntegrate(Configuration configuration) {
        this.configuration = configuration;
        this.ignite = initIgnite(configuration);
    }


    @Override
    public Cluster getCluster() {
        return new IgniteCluster() ;
    }

    @Override
    public <K, V> ShareCache<K, V> getCache(String cacheName) {
        return new IgniteShareCache<>(ignite.getOrCreateCache(cacheName));
    }

    @Override
    public <K, V> ShareCache<K, V> getCache(String cacheName, Integer Size) {
        return null;
    }

    @Override
    public <K, V> ShareCache<K, V> getCache(String cacheName, Integer Size, Boolean persistence) {
        return ;
    }

    @Override
    public Topics getTopics() {
        return new IgniteTopics();
    }

    @Override
    public Message getMessage() {
        return new IgniteMessage(new FixedTopicFilter<>(),new TreeTopicFilter<>());
    }

    private Ignite initIgnite(Configuration configuration) {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(false);
        cfg.setLocalHost("127.0.0.1");
        cfg.setPeerClassLoadingEnabled(true);
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));
        return Ignition.start(cfg);
    }
}
