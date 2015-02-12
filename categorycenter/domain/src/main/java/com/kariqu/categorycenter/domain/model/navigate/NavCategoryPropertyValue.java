package com.kariqu.categorycenter.domain.model.navigate;

/**
 * 导航类目类目属性值
 * 来源于对关联后台类目的属性
 *
 * @Author: Tiger
 * @Since: 11-7-4 下午10:35
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class NavCategoryPropertyValue {

    private int id;

    private int navCategoryId;

    private int propertyId;

    private int valueId;

    /**
     * 类目属性值优先级
     */
    private int priority;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNavCategoryId() {
        return navCategoryId;
    }

    public void setNavCategoryId(int navCategoryId) {
        this.navCategoryId = navCategoryId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
