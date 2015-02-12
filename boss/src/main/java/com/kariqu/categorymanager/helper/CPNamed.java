package com.kariqu.categorymanager.helper;

import com.kariqu.categorycenter.domain.model.PropertyType;

/**
 * 带有名字的类目属性
 * User: Asion
 * Date: 11-7-19
 * Time: 下午3:51
 */
public class CPNamed {
    
    private int id;

    private int cid;

    private int pid;

    private String name;

    private boolean multiValue;

    private boolean compareable;

    private PropertyType propertyType;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMultiValue() {
        return multiValue;
    }

    public void setMultiValue(boolean multiValue) {
        this.multiValue = multiValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public boolean isCompareable() {
        return compareable;
    }

    public void setCompareable(boolean compareable) {
        this.compareable = compareable;
    }
}
