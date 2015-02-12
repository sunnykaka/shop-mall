package com.kariqu.categorymanager.helper;

/**
 * User: Asion
 * Date: 13-2-26
 * Time: 下午3:18
 */
public class PropertyCheckJson {

    private long id;

    private String text;

    boolean leaf;

    boolean checked = true;//默认就选中

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
}
