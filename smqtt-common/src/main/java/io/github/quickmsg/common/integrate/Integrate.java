package io.github.quickmsg.common.integrate;

import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import io.github.quickmsg.common.integrate.channel.IntegrateChannels;
import io.github.quickmsg.common.integrate.cluster.IntegrateCluster;
import io.github.quickmsg.common.integrate.job.JobExecutor;
import io.github.quickmsg.common.integrate.msg.IntegrateMessages;
import io.github.quickmsg.common.integrate.topic.IntegrateTopics;
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
     * @param igniteCacheRegion  {@link IntegrateCache support memory or Persistence }
     * @return {@link IntegrateCache support memory or Persistence }
     */
    <K, V> IntegrateCache<K, V> getCache(IgniteCacheRegion igniteCacheRegion);



    /**
     * @return {@link IntegrateTopics manager topic }
     */
    IntegrateTopics<SubscribeTopic> getTopics();

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
