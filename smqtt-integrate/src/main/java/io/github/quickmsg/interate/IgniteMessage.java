package io.github.quickmsg.interate;

import io.github.quickmsg.common.interate1.msg.Message;
import io.github.quickmsg.common.message.HeapMqttMessage;
import io.github.quickmsg.common.topic.AbstractTopicAggregate;
import io.github.quickmsg.common.topic.TopicFilter;

/**
 * @author luxurong
 */
public class IgniteMessage extends AbstractTopicAggregate<HeapMqttMessage> implements Message {


    protected IgniteMessage(TopicFilter<HeapMqttMessage> fixedTopicFilter, TopicFilter<HeapMqttMessage> treeTopicFilter) {
        super(fixedTopicFilter, treeTopicFilter);
    }





}
