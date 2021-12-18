package io.github.quickmsg;

import io.github.quickmsg.common.integrate.proxy.IntegrateProxy;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Collections;
import java.util.Map;

/**
 * @author luxurong
 * @date 2021/10/18 17:50
 * @description
 */
public class Test1 {
    public static void main(String[] args) throws InterruptedException {
        // Preparing IgniteConfiguration using Java APIs
        // Preparing IgniteConfiguration using Java APIs
        DataRegionConfiguration dataRegionConfiguration=new DataRegionConfiguration()
                .setName("store")
                .setPersistenceEnabled(true);
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration();
        dataStorageConfiguration.setDataRegionConfigurations(dataRegionConfiguration);

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDataStorageConfiguration(dataStorageConfiguration);

        // The node will be started as a client node.
        cfg.setClientMode(false);
        cfg.setLocalHost("127.0.0.1");
        // Classes of custom Java logic will be transferred over the wire from this app.
        cfg.setPeerClassLoadingEnabled(true);

        // Setting up an IP Finder to ensure the client can locate the servers.
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

        // Starting the node
        Ignite ignite = Ignition.start(cfg);

//        // Create an IgniteCacheRegion and put some values in it.
//        IgniteCacheRegion<Integer, String> cache = ignite.getOrCreateCache("myCache");
//        cache.put(1, "Hello");
//        cache.put(2, "World!");

        CacheConfiguration configuration = new CacheConfiguration();
        configuration.setName("test");
        configuration.setDataRegionName("store");
        IgniteCache<Integer, Map<String,Object>> cache = ignite.getOrCreateCache(configuration);

        long time1 = System.currentTimeMillis();

        for (int i = 0; i < 500000; i++) {
            Map<String,Object> mapCache=cache.get(3);
            if(mapCache!=null){
                System.out.println(">> " + mapCache.get("1"));

            }
        }

        long time2 = System.currentTimeMillis();


        System.out.println("cost time " + ((time2 - time1) / 1000) + "s");

        System.out.println(">> Compute task is executed, check for output on the server nodes.");

        // Disconnect from the cluster.
        Thread.sleep(1000000L);

        ignite.close();
    }

    /**
     * A compute tasks that prints out a node ID and some details about its OS and JRE.
     * Plus, the code shows how to access data stored in a cache from the compute task.
     */
//    private static class RemoteTask implements IgniteRunnable {
//        @IgniteInstanceResource
//        Ignite ignite;
//
//        @Override
//        public void run() {
//            System.out.println(">> Executing the compute task");
//
//
//        }
//    }
}
