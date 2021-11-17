package io.github.quickmsg.sql;

import io.github.quickmsg.TestAnnocation;
import io.github.quickmsg.common.interate1.proxy.IntegrateProxy;
import io.github.quickmsg.common.utils.JacksonUtil;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlFunction;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.SqlConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.*;

/**
 * @author luxurong
 * @date 2021/10/18 17:50
 * @description
 */
public class Test1 {
    public static void main(String[] args) throws InterruptedException {
        // Preparing IgniteConfiguration using Java APIs
        IgniteConfiguration cfg = new IgniteConfiguration();

        // The node will be started as a client node.
        cfg.setClientMode(false);
        cfg.setLocalHost("127.0.0.1");
        // Classes of custom Java logic will be transferred over the wire from this app.
        cfg.setPeerClassLoadingEnabled(true);

        // Setting up an IP Finder to ensure the client can locate the servers.
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));


        SqlConfiguration sqlConfiguration = new SqlConfiguration();
        sqlConfiguration.setSqlSchemas("test");
        cfg.setSqlConfiguration(sqlConfiguration);
        // Starting the node
        Ignite ignite = Ignition.start(cfg);

        IgniteCache<Integer,Person>  cache=
                ignite.getOrCreateCache(
                        new CacheConfiguration<Integer,Person>()
                                .setName("myCache")
                                .setSqlFunctionClasses(MyFunctions.class)
                                .setIndexedTypes(Integer.class,Person.class));
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("haha",2);

        map.put("json",1);
        map.put("map",map1);


        List<String> sts = new ArrayList<>();
        sts.add(JacksonUtil.map2Json(map));
        cache.put(1,new Person("zhnagsan",1,28,sts));
//        cache.put(2,new Person("lisi",2,38));


        long time1 = System.currentTimeMillis();



        SqlFieldsQuery sqlFieldsQuery = new SqlFieldsQuery("select params from Person where age > 19").setLocal(true);
        // Iterate over the result set.
        try (QueryCursor<List<?>> cursor=cache.query(sqlFieldsQuery)) {
            for (List<?> row : cursor)
                System.out.println("person=" + row);
        }



        long time2 = System.currentTimeMillis();


        System.out.println("sql query cost time " + ((time2 - time1) / 1000) + "s");

        System.out.println(">> Compute task is executed, check for output on the server nodes.");

        // Disconnect from the cluster.
        Thread.sleep(1000000L);

        ignite.close();
    }

    public static class MyFunctions {

        @QuerySqlFunction
        public static String sqr(String value,String exp) {
            JexlEngine J_EXL_ENGINE = new JexlBuilder().create();
            JexlExpression e = J_EXL_ENGINE.createExpression(exp);
            Map<String,Object> maps=JacksonUtil.json2Map(value,String.class,Object.class);
            MapContext context = new MapContext();
            context.set("$",maps);
            return String.valueOf(e.evaluate(context)) ;
        }
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
