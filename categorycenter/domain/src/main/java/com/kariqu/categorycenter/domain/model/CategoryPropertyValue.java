package com.kariqu.categorycenter.domain.model;

/**
 * 类目属性值 领域对象，它代表某个类目属性的一个特定的值，比如手机类目
 * 品牌类目属性的三星，这里三星就是类目属性值
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午1:12
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class CategoryPropertyValue {

    private int id;//没有业务意义，只是数据库主键

    private int categoryId;

    private int propertyId;

    private int valueId;

    private int priority;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    @Override
    public String toString() {
        return "CategoryPropertyValue{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", propertyId=" + propertyId +
                ", valueId=" + valueId +
                ", priority=" + priority +
                '}';
    }
}
