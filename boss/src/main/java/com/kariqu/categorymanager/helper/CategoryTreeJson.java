package com.kariqu.categorymanager.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * 构建类目树时的Json数据对象
 * User: Asion
 * Date: 11-7-19
 * Time: 下午2:53
 */
public class CategoryTreeJson {

    private int id;

    private String text;

    boolean leaf;

    boolean hot = false;

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

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }
}
