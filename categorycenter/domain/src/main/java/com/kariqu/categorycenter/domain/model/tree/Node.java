package com.kariqu.categorycenter.domain.model.tree;

import java.io.Serializable;
import java.util.List;

/**
 * 类目对象
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午2:54
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public interface Node<Model extends TreeModelType> extends Serializable {

    Model getModel();

    void setParent(Node<Model> parent);

    List<Node<Model>> getChildren();

    void addChildNode(Node<Model> node);

    Node<Model> getParent();

    boolean isLeaf();

}
