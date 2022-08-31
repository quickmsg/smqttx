package io.github.quickmsg.common.topic;

import java.util.HashSet;
import java.util.Set;

/**
 * @author luxurong
 */
public class TreeTopicFilter<T> implements TopicFilter<T> {

    private final TreeNode<T> rootTreeNode = new TreeNode<>("root");


    @Override
    public boolean addObjectTopic(String topicFilter, T t) {
        return rootTreeNode.addObjectTopic(topicFilter, t);
    }

    @Override
    public boolean removeObjectTopic(String topic, T t) {
        return rootTreeNode.removeObjectTopic(topic, t);
    }

    @Override
    public Set<T> getObjectByTopic(String topic) {
        return new HashSet<>(rootTreeNode.getObjectsByTopic(topic));
    }

    @Override
    public Set<T> getAllObjectsTopic() {
        return rootTreeNode.getObjects();
    }
}
