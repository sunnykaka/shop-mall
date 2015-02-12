package com.kariqu.productmanager.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-9-17
 * Time: 下午2:43
 */
public class CustomerTreeJson {
    private int id;

    private String text;

    boolean leaf;

    private List<CustomerTreeJson> children = new LinkedList<CustomerTreeJson>();

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public CustomerTreeJson addNode(CustomerTreeJson node) {
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

    public List<CustomerTreeJson> getChildren() {
        return children;
    }

    public void setChildren(List<CustomerTreeJson> children) {
        this.children = children;
    }
}
