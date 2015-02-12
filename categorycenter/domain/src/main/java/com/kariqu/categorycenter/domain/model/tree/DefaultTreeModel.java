package com.kariqu.categorycenter.domain.model.tree;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午3:17
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class DefaultTreeModel<Model extends TreeModelType<? extends Serializable>> implements TreeModel<Model> {

    private Map<Serializable, Node<Model>> nodeMap = new ConcurrentHashMap<Serializable, Node<Model>>();

    @Override
    public void addNode(Node<Model> node) {
        Node<Model> parent = node.getParent();
        if (nodeMap.containsKey(node.getModel().getId())) {
            Node<Model> hasPutedNode = nodeMap.get(node.getModel().getId());
            /**
             * 如果加入的当前节点已经存在并且已经加入的节点父节点为空，则设置父节点
             */
            if (null == hasPutedNode.getParent() || !nodeMap.containsKey(hasPutedNode.getParent().getModel().getId())) {
                hasPutedNode.setParent(parent);
                if (null != parent) {
                    parent.addChildNode(node);
                    nodeMap.put(parent.getModel().getId(), parent);
                }
            } else {
                nodeMap.get(hasPutedNode.getParent().getModel().getId()).addChildNode(node);
            }

            return;
        }
        nodeMap.put(node.getModel().getId(), node);
        if (parent == null)
            return;
        if (nodeMap.containsKey(parent.getModel().getId())) {
            parent = nodeMap.get(parent.getModel().getId());
            parent.addChildNode(node);
        } else {
            nodeMap.put(parent.getModel().getId(), parent);
            parent.addChildNode(node);
        }

    }

    @Override
    public List<Node<Model>> getChildrenByParentId(Serializable parentId) {
        final Node<Model> parentNode = this.getNodeById(parentId);
        if (null == parentNode)
            return Collections.emptyList();
        return parentNode.getChildren();
    }


    @Override
    public Node<Model> getNodeById(Serializable modelId) {
        return this.nodeMap.get(modelId);
    }

    private Node<Model> getNode(Node<Model> node, Serializable modelId) {
        Serializable id = node.getModel().getId();
        if (id.equals(modelId)) {
            return node;
        } else {
            Node<Model> result = null;
            for (Node<Model> childNode : node.getChildren()) {
                result = getNode(childNode, modelId);
                if (result != null) {
                    break;
                }
            }
            return result;
        }
    }
}
