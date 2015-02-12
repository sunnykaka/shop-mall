package com.kariqu.categorycenter.domain.model;

/**
 * 类目的属性，它是可以在类目之间共享的，比如品牌这个属性，可以在多个类目共享
 * 在数据库中成为数据字典，要使用这个值的时候就取出对应的ID
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午1:05
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class Property {

    private int id;

    private String name;

    public Property() {
    }

    public Property(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property property = (Property) o;

        if (id != property.id) return false;
        if (name != null ? !name.equals(property.name) : property.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
