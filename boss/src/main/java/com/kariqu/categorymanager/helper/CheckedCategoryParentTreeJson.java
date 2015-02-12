package com.kariqu.categorymanager.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * 前台类目关联上后台类目的父类目
 * @author: Eli
 * @since 1.0.0
 * Date:12-9-13
 * Time:下午2:52
 */

public class CheckedCategoryParentTreeJson {

    private int id;

    private String text;

    private boolean leaf;

    private boolean disabled;

    private List<CategoryCheckTreeJson> children = new LinkedList<CategoryCheckTreeJson>();

    public CategoryCheckTreeJson addNode(CategoryCheckTreeJson node) {
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

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public List<CategoryCheckTreeJson> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryCheckTreeJson> children) {
        this.children = children;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
