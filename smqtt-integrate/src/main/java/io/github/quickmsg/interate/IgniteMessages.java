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

    private final IntegrateCache<String, IgniteSet<SessionMessage>> sessionCache;

    private final IntegrateCache<String, RetainMessage> retainCache;

    private final IntegrateCache<String, RetainMessage> publishAckCache;

    private final IgniteAtomicLong sessionCounter;

    private final IgniteAtomicLong retainCounter;

    private final IgniteAtomicLong number;

    protected IgniteMessages(TopicFilter<PublishMessage> fixedTopicFilter, TopicFilter<PublishMessage> treeTopicFilter, IgniteIntegrate integrate) {
        super(fixedTopicFilter, treeTopicFilter);
        this.integrate = integrate;
        this.sessionCache = integrate.getCache(IgniteCacheRegion.SESSION);
        this.retainCache = integrate.getCache(IgniteCacheRegion.RETAIN);
        this.publishAckCache= integrate.getCache(IgniteCacheRegion.ACK);
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
    public Set<SessionMessage> getSessionMessage(String clientIdentifier) {
        return sessionCache.get(clientIdentifier);
    }

    @Override
    public void deleteSessionMessage(String clientIdentifier) {
        Optional.ofNullable(sessionCache.get(clientIdentifier))
                .ifPresent(sessionMessages -> {
                    if (sessionCache.remove(clientIdentifier)) {
                        sessionMessages.close();
                        for (; ; ) {
                            int size = sessionMessages.size();
                            long counter = sessionCounter.get();
                            if (sessionCounter.compareAndSet(counter, counter - size)) {
                                break;
                            }
                        }
                    }
                });
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
    public boolean saveSessionMessage(SessionMessage sessionMessage) {
        String clientId = sessionMessage.getClientId();
        String SESSION_PREFIX = "session:";
        IgniteSet<SessionMessage> sessionMessages = sessionCache.getAndPutIfAbsent(clientId, integrate
                .getIgnite()
                .set(SESSION_PREFIX +clientId, new CollectionConfiguration().setCollocated(true).setBackups(1)));
        if(sessionMessages == null){
            sessionMessages = sessionCache.get(clientId);
        }
        boolean success = sessionMessages.add(sessionMessage);
        if (success) {
            sessionCounter.incrementAndGet();
        }
        return success;
    }

    @Override
    public Set<RetainMessage> getRetainMessage(String topicName) {
         retainCache.get(topicName);
         return  new HashSet<>();
    }


}
