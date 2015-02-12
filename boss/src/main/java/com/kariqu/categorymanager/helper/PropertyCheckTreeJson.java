package com.kariqu.categorymanager.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * 属性和值选择树节点
 * 当在一个类目上添加子类目的时候，系统会调出父类目的所有属性和值组成一个选择树，这个树只有两级，类似：
 * <p/>
 * 品牌
 * ---三星
 * ---苹果
 * 尺寸
 * ---22寸
 * ---23寸
 * User: Asion
 * Date: 11-11-6
 * Time: 下午11:02
 */
public class PropertyCheckTreeJson {

    private long id;

    private String text;

    boolean leaf;

    boolean checked = true;//默认就选中

    private List<PropertyCheckTreeJson> children = new LinkedList<PropertyCheckTreeJson>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<PropertyCheckTreeJson> getChildren() {
        return children;
    }

    public void setChildren(List<PropertyCheckTreeJson> children) {
        this.children = children;
    }
}
