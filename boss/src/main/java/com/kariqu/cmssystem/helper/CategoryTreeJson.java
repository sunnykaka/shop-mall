package com.kariqu.cmssystem.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-9-17
 * Time: 下午2:43
 */
public class CategoryTreeJson {
    private int id;

    private String text;

    boolean leaf;

    private List<CategoryTreeJson> children = new LinkedList<CategoryTreeJson>();

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public CategoryTreeJson addNode(CategoryTreeJson node) {
        children.add(node);
        return node;
    }

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

    public List<CategoryTreeJson> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryTreeJson> children) {
        this.children = children;
    }
}
