package com.kariqu.categorycenter.domain.model.tree;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Tiger
 * @Since: 11-6-25 下午3:11
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class DefaultNode<Model extends TreeModelType> implements Node<Model> {

    private Model model;

    private List<Node<Model>> children = new LinkedList<Node<Model>>();

    private Node<Model> parent;

    public DefaultNode(Model model) {
        this.model = model;
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public void setParent(Node<Model> parent) {
        this.parent = parent;
    }

    @Override
    public List<Node<Model>> getChildren() {
        return children;
    }

    @Override
    public void addChildNode(Node<Model> modelNode) {
        this.children.add(modelNode);
        modelNode.setParent(this);

    }

    @Override
    public Node<Model> getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return null == children || children.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultNode that = (DefaultNode) o;

        if (model != null ? !model.equals(that.model) : that.model != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return model != null ? model.hashCode() : 0;
    }
}
