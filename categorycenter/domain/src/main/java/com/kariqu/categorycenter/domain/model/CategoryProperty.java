package com.kariqu.categorycenter.domain.model;

/**
 * 类目属性 领域对象，它代表类目的某个特定属性，比如手机类目的品牌属性，这里
 * 品牌就是手机类目的一个类目属性
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午1:05
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class CategoryProperty {

    private int id; //没有业务意义，只是数据库主键

    private int categoryId;

    private int propertyId;

    private PropertyType propertyType;

    /**
     * 表示在发布商品的时候，这个属性的值是checkbox，可勾选多个，而不是一个单选下拉框
     */
    private boolean multiValue;

    private boolean compareable;

    /**
     * 类目属性的排序优先级，数字越小越靠前，这个优先级目前没有作用，展示给用户界面的是前台类目属性优先级
     */
    private int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public boolean isMultiValue() {
        return multiValue;
    }

    public void setMultiValue(boolean multiValue) {
        this.multiValue = multiValue;
    }

    public boolean isCompareable() {
        return compareable;
    }

    public void setCompareable(boolean compareable) {
        this.compareable = compareable;
    }

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

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public String toString() {
        return "CategoryProperty{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", propertyId=" + propertyId +
                ", propertyType=" + propertyType +
                ", multiValue=" + multiValue +
                ", compareable=" + compareable +
                ", priority=" + priority +
                '}';
    }
}
