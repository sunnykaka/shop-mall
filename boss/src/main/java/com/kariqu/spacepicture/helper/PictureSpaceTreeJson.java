package com.kariqu.spacepicture.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Wendy
 * Date: 12-7-13
 * Time: 下午6:10
 */
public class PictureSpaceTreeJson {
    private int id;

    private String text;

    boolean leaf;

    private List<PictureSpaceTreeJson> children = new LinkedList<PictureSpaceTreeJson>();

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public PictureSpaceTreeJson addNode(PictureSpaceTreeJson node) {
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

    public List<PictureSpaceTreeJson> getChildren() {
        return children;
    }

    public void setChildren(List<PictureSpaceTreeJson> children) {
        this.children = children;
    }

}
