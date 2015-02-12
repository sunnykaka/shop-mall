package com.kariqu.categorymanager.helper;

import com.kariqu.categorycenter.domain.model.PropertyType;

/**
 * 类目属性值编辑对象
 * 对应在Ext Grid的一行数据
 */
public class CpvRow {

    private int id;//类目属性表的ID

    private int cid;

    private int pid;

    private String property;

    private int priority;

    private String value;//被中文逗号隔开的多值

    private PropertyType propertyType;

    private boolean compareable;

    private boolean multi;

    public CpvRow() {
    }

    public CpvRow(int id, int cid, int pid, String property, String value, PropertyType propertyType, boolean multi, int priority,boolean compareable) {
        this.id = id;
        this.cid = cid;
        this.pid = pid;
        this.property = property;
        this.value = value;
        this.propertyType = propertyType;
        this.multi = multi;
        this.priority = priority;
        this.compareable=compareable;
    }

    public boolean isCompareable() {
        return compareable;
    }

    public void setCompareable(boolean compareable) {
        this.compareable = compareable;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}