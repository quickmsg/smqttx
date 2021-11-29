package io.github.quickmsg.common.topic;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * @author luxurong
 */
@Getter
@Setter
public class TreeNode<T> {

    private final String topic;

    private Set<T> objects = new CopyOnWriteArraySet<>();

    private Map<String,TreeNode<T>> childNodes = new ConcurrentHashMap<>();

    public TreeNode(String topic) {
        this.topic = topic;
    }

    private final String ONE_SYMBOL = "+";

    private final String MORE_SYMBOL = "#";

    public boolean addObjectTopic(String topic,T t) {
        String[] topics = topic.split("/");
        return addIndex(topic,t, topics, 0);
    }


    private boolean addTreeObject(T t) {
        return objects.add(t);
    }


    private boolean addIndex(String topic,T t, String[] topics, Integer index) {
        String lastTopic = topics[index];
        TreeNode<T> treeNode = childNodes.computeIfAbsent(topic, tp -> new TreeNode<T>(lastTopic));
        if (index == topics.length - 1) {
            return treeNode.addTreeObject(t);
        } else {
            return treeNode.addIndex(topic,t, topics, index + 1);
        }
    }


    public List<T> getObjectsByTopic(String topicFilter) {
        String[] topics = topicFilter.split("/");
        return searchTree(topics);
    }


    private List<T> searchTree(String[] topics) {
        LinkedList<T> objectList = new LinkedList<>();
        loadTreeObjects(this, objectList, topics, 0);
        return objectList;
    }

    private void loadTreeObjects(TreeNode<T> treeNode, LinkedList<T> objectLists, String[] topics, Integer index) {
        String lastTopic = topics[index];
        TreeNode<T> moreTreeNode = treeNode.getChildNodes().get(MORE_SYMBOL);
        if (moreTreeNode != null) {
            objectLists.addAll(moreTreeNode.getObjects());
        }
        if (index == topics.length - 1) {
            TreeNode<T> localTreeNode = treeNode.getChildNodes().get(lastTopic);
            if (localTreeNode != null) {
                Set<T> lists = localTreeNode.getObjects();
                if (lists != null && lists.size() > 0) {
                    objectLists.addAll(lists);
                }
            }
            localTreeNode = treeNode.getChildNodes().get(ONE_SYMBOL);
            if (localTreeNode != null) {
                Set<T> lists = localTreeNode.getObjects();
                if (lists != null && lists.size() > 0) {
                    objectLists.addAll(lists);
                }
            }

        } else {
            TreeNode<T> oneTreeNode = treeNode.getChildNodes().get(ONE_SYMBOL);
            if (oneTreeNode != null) {
                loadTreeObjects(oneTreeNode, objectLists, topics, index + 1);
            }
            TreeNode<T> node = treeNode.getChildNodes().get(lastTopic);
            if (node != null) {
                loadTreeObjects(node, objectLists, topics, index + 1);
            }
        }

    }

    public boolean removeObjectTopic(String topicFilter,T t) {
        TreeNode<T> node = this;
        String[] topics = topicFilter.split("/");
        for (String topic : topics) {
            if (node != null) {
                node = node.getChildNodes().get(topic);
            }
        }
        if (node != null) {
            Set<T> subscribeTopics = node.getObjects();
            if (subscribeTopics != null) {
                return subscribeTopics.remove(t);
            }
        }
        return false;
    }

    public Set<T> getAllObjectsTopic() {
        return getTreeObjectsTopic(this);
    }

    private Set<T> getTreeObjectsTopic(TreeNode<T> node) {
        Set<T> allSubscribeTopics = new HashSet<>();
        allSubscribeTopics.addAll(node.getObjects());
        allSubscribeTopics.addAll(node.getChildNodes()
                .values()
                .stream()
                .flatMap(treeNode -> treeNode.getTreeObjectsTopic(treeNode).stream())
                .collect(Collectors.toSet()));
        return allSubscribeTopics;
    }

}
