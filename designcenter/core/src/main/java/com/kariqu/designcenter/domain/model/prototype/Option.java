package com.kariqu.designcenter.domain.model.prototype;

import java.io.Serializable;

/**
 * 在模块参数配置中对select和radio进行配置
 *
 * @Author: Tiger
 * @Since: 2011-5-8 12:22:27
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class Option implements Serializable{
    private static final long serialVersionUID = -8359518507462271933L;

    /**
     * 在select和radio中表示value需要显示的值，因为往往label和value是不同的，比如value是male，但显示的是男
     */
    private String label;

    private boolean selected;

    private String value;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
