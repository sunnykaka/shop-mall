package com.kariqu.categorycenter.domain.model;

/**
 * 类目属性的值，它是可以在不同的类目中共享，比如红色是颜色这个类目属性的一个特定值，而颜色
 * 可以在多个类目共享， 在数据库中成为数据字典，要使用这个值的时候就取出对应的ID
 * @Author: Tiger
 * @Since: 11-6-25 下午1:06
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class Value {

    private int id;

    private String valueName;

    public Value() {
    }

    public Value(String valueName) {
        this.valueName = valueName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value value = (Value) o;

        if (id != value.id) return false;
        if (valueName != null ? !valueName.equals(value.valueName) : value.valueName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (valueName != null ? valueName.hashCode() : 0);
        return result;
    }
}
