package io.github.quickmsg;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheEntryProcessor;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.MutableEntry;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luxurong
 * @date 2021/10/18 17:50
 * @description
 */
public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        // Preparing IgniteConfiguration using Java APIs
        DataRegionConfiguration dataRegionConfiguration=new DataRegionConfiguration()
                .setName("store")
                .setPersistenceEnabled(true);
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfiguration);

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDataStorageConfiguration(dataStorageConfiguration);
        cfg.setLocalHost("127.0.0.1");
        // The node will be started as a client node.
        cfg.setClientMode(false);


        // Classes of custom Java logic will be transferred over the wire from this app.
        cfg.setPeerClassLoadingEnabled(true);

        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Arrays.asList("127.0.0.1"));
        spi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(spi);

        // Starting the node
        Ignite ignite = Ignition.start(cfg);
        ignite.cluster().state(ClusterState.ACTIVE);

        // Create an IgniteCacheRegion and put some values in it.
        CacheConfiguration configuration = new CacheConfiguration();
        configuration.setName("test");
        configuration.setDataRegionName("store");
        IgniteCache<Integer, Map<String,Object>> cache = ignite.getOrCreateCache(configuration);
//        Map<String,Object> map = new HashMap<>();
//        map.put("1","90090099");
//        cache.put(3,map);
//        Thread.sleep(2000);
        Map<String,Object> map = cache.get(3);
        System.out.println(map);


        System.out.println(">> Created the cache and add the values.");

        // Executing custom Java compute task on server nodes.
//        ignite.compute(ignite.cluster().forServers()).broadcast(new RemoteTask());

        System.out.println(">> Compute task is executed, check for output on the server nodes.");

        // Disconnect from the cluster.
        Thread.sleep(1000000000L);
//        ignite.close();
    }

    /**
     * A compute tasks that prints out a node ID and some details about its OS and JRE.
     * Plus, the code shows how to access data stored in a cache from the compute task.
     */
    private static class RemoteTask implements IgniteRunnable {
        @IgniteInstanceResource
        Ignite ignite;

        @Override public void run() {
            System.out.println(">> Executing the compute task");

            System.out.println(
                    "   Node ID: " + ignite.cluster().localNode().id() + "\n" +
                            "   OS: " + System.getProperty("os.name") +
                            "   JRE: " + System.getProperty("java.runtime.name"));

            IgniteCache<Integer, String> cache = ignite.cache("myCache");

            System.out.println(">> " + cache.get(1) + " " + cache.get(2));
        }
    }
}
