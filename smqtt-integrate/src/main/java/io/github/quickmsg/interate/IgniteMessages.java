package io.github.quickmsg.interate;

import io.github.quickmsg.common.integrate.IgniteCacheRegion;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import io.github.quickmsg.common.integrate.msg.IntegrateMessages;
import io.github.quickmsg.common.message.RetainMessage;
import io.github.quickmsg.common.message.SessionMessage;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.topic.AbstractTopicAggregate;
import io.github.quickmsg.common.topic.TopicFilter;
import org.apache.ignite.IgniteAtomicLong;
import org.apache.ignite.IgniteSet;
import org.apache.ignite.configuration.CollectionConfiguration;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author luxurong
 */
public class IgniteMessages extends AbstractTopicAggregate<PublishMessage> implements IntegrateMessages {

    protected final IgniteIntegrate integrate;

    private final IntegrateCache<String, RetainMessage> retainCache;

    private final IgniteAtomicLong sessionCounter;

    private final IgniteAtomicLong retainCounter;

    private final IgniteAtomicLong number;

    protected IgniteMessages(TopicFilter<PublishMessage> fixedTopicFilter, TopicFilter<PublishMessage> treeTopicFilter, IgniteIntegrate integrate) {
        super(fixedTopicFilter, treeTopicFilter);
        this.integrate = integrate;
        this.retainCache = integrate.getCache(IgniteCacheRegion.RETAIN);
        this.sessionCounter = integrate.getIgnite().atomicLong(
                "session-counter", // Atomic long name.
                0,            // Initial value.
                true         // Create if it does not exist.
        );

        this.retainCounter = integrate.getIgnite().atomicLong(
                "retain-counter", // Atomic long name.
                0,            // Initial value.
                true         // Create if it does not exist.
        );
        this.number = integrate.getIgnite().atomicLong(
                "number", // Atomic long name.
                0,            // Initial value.
                true         // Create if it does not exist.
        );

    }


    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }

    @Override
    public void saveRetainMessage(RetainMessage of) {
        retainCache.put(of.getTopic(), of);
        retainCounter.incrementAndGet();
    }

    @Override
    public void deleteRetainMessage(String clientIdentifier) {
        if (retainCache.remove(clientIdentifier)) {
            retainCounter.decrementAndGet();
        }
    }


    @Override
    public Set<RetainMessage> getRetainMessage(String topicName) {
         retainCache.get(topicName);
         return  new HashSet<>();
    }


}
