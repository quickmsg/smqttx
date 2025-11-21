package io.github.quickmsg.interate.hazelcast;

import com.hazelcast.cp.IAtomicLong;
import io.github.quickmsg.common.integrate.Integrate;
import io.github.quickmsg.common.integrate.cache.IntegrateCache;
import io.github.quickmsg.common.integrate.msg.IntegrateMessages;
import io.github.quickmsg.common.message.RetainMessage;
import io.github.quickmsg.common.message.mqtt.PublishMessage;
import io.github.quickmsg.common.topic.AbstractTopicAggregate;

import java.util.HashSet;
import java.util.Set;

/**
 * Hazelcast implementation of IntegrateMessages
 *
 * @author hxx
 */
public class HazelcastMessages extends AbstractTopicAggregate<PublishMessage> implements IntegrateMessages {

    protected final HazelcastIntegrate integrate;
    private final IntegrateCache<String, RetainMessage> retainCache;
    private final IAtomicLong sessionCounter;
    private final IAtomicLong retainCounter;
    private final IAtomicLong number;

    protected HazelcastMessages(
            io.github.quickmsg.common.topic.FixedTopicFilter<PublishMessage> fixedTopicFilter,
            io.github.quickmsg.common.topic.TreeTopicFilter<PublishMessage> treeTopicFilter,
            HazelcastIntegrate integrate) {
        super(fixedTopicFilter, treeTopicFilter);
        this.integrate = integrate;
        // 使用字符串常量替代IgniteCacheRegion枚举
        this.retainCache = integrate.getCache("retain_message");
        
        this.sessionCounter = integrate.getHazelcastInstance()
                .getCPSubsystem()
                .getAtomicLong("session-counter");
        
        this.retainCounter = integrate.getHazelcastInstance()
                .getCPSubsystem()
                .getAtomicLong("retain-counter");
        
        this.number = integrate.getHazelcastInstance()
                .getCPSubsystem()
                .getAtomicLong("number");
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
        Set<RetainMessage> retainMessages = new HashSet<>();
        RetainMessage message = retainCache.get(topicName);
        if (message != null) {
            retainMessages.add(message);
        }
        return retainMessages;
    }
}