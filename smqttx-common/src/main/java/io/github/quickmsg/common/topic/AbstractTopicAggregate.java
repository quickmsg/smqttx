package io.github.quickmsg.common.topic;

import lombok.Getter;

/**
 * @author luxurong
 */
@Getter
@Deprecated
public abstract class AbstractTopicAggregate<T> {


    protected static final String ONE_SYMBOL = "+";

    protected static final String MORE_SYMBOL = "#";


    private final TopicFilter<T> fixedTopicFilter;

    private final TopicFilter<T> treeTopicFilter;


    protected AbstractTopicAggregate(TopicFilter<T> fixedTopicFilter, TopicFilter<T> treeTopicFilter) {
        this.fixedTopicFilter = fixedTopicFilter;
        this.treeTopicFilter = treeTopicFilter;
    }

    public TopicFilter<T> checkFilter(String topicFilter) {
        return topicFilter.contains(ONE_SYMBOL)
                || topicFilter.contains(MORE_SYMBOL) ? treeTopicFilter : fixedTopicFilter;
    }
}
