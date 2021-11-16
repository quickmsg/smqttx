package io.github.quickmsg.common.interate1;

import io.github.quickmsg.common.integrate.topic.SubscribeTopic;
import io.github.quickmsg.common.interate1.cache.IntegrateCache;
import io.github.quickmsg.common.interate1.channel.IntegrateChannels;
import io.github.quickmsg.common.interate1.cluster.IntegrateCluster;
import io.github.quickmsg.common.interate1.job.JobExecutor;
import io.github.quickmsg.common.interate1.msg.IntegrateMessages;
import io.github.quickmsg.common.interate1.topic.IntergrateTopics;
import io.github.quickmsg.common.event.Pipeline;
import io.github.quickmsg.common.protocol.ProtocolAdaptor;
import org.apache.ignite.Ignite;

/**
 * @author luxurong
 */
public interface Integrate {


    /**
     * @return {@link IntegrateChannels }
     */
    IntegrateChannels getChannels();

    /**
     * @return {@link IntegrateCluster }
     */
    IntegrateCluster getCluster();


    /**
     * @param cacheName cache name
     * @return {@link IntegrateCache support memory or Persistence }
     */
    <K, V> IntegrateCache<K, V> getCache(String cacheName);

    /**
     * @param cacheName         cache name
     * @param setDataRegionName data region name
     * @return {@link IntegrateCache support memory or Persistence }
     */
    <K, V> IntegrateCache<K, V> getCache(String cacheName, String setDataRegionName);


    /**
     * @return {@link IntergrateTopics manager topic }
     */
    IntergrateTopics<SubscribeTopic> getTopics();

    /**
     * @return {@link IntegrateMessages manager message }
     */
    IntegrateMessages getMessages();

    /**
     * @return {@link JobExecutor job executor }
     */
    JobExecutor getJobExecutor();


    /**
     * get protocol adaptor
     *
     * @return {@link ProtocolAdaptor protocol adaptor }
     */
    ProtocolAdaptor getProtocolAdaptor();


    /**
     * get  Ignite
     *
     * @return {@link Ignite Ignite  }
     */
    Ignite getIgnite();


    /**
     * get  Pipeline
     *
     * @return {@link Pipeline get event pipeline  }
     */
    Pipeline   getPipeline();



}
