package io.github.quickmsg.interate;

import io.github.quickmsg.common.interate1.Integrate;
import io.github.quickmsg.common.interate1.msg.IntegrateMessages;
import io.github.quickmsg.common.message.HeapMqttMessage;
import io.github.quickmsg.common.topic.AbstractTopicAggregate;
import io.github.quickmsg.common.topic.TopicFilter;

/**
 * @author luxurong
 */
public class IgniteMessages extends AbstractTopicAggregate<HeapMqttMessage> implements IntegrateMessages {

    protected final IgniteIntegrate  integrate;

    protected IgniteMessages(TopicFilter<HeapMqttMessage> fixedTopicFilter, TopicFilter<HeapMqttMessage> treeTopicFilter, IgniteIntegrate integrate) {
        super(fixedTopicFilter, treeTopicFilter);
        this.integrate = integrate;
    }


    @Override
    public Integrate getIntegrate() {
        return this.integrate;
    }
}
