package com.kariqu.categorymanager.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * 属性树节点
 * User: Asion
 * Date: 11-11-14
 * Time: 上午11:04
 */
public class PropertyTreeJson {

    private int id;

    private String text;

    boolean leaf;

    private List<PropertyTreeJson> children = new LinkedList<PropertyTreeJson>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public List<PropertyTreeJson> getChildren() {
        return children;
    }

    public void setChildren(List<PropertyTreeJson> children) {
        this.children = children;
    }
}
