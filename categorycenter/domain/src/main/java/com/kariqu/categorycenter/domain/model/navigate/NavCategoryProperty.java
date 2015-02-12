package com.kariqu.categorycenter.domain.model.navigate;

/**
 * 导航类目类目属性
 * 来源于对关联后台的属性
 *
 * @Author: Tiger
 * @Since: 11-7-4 下午10:33
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class NavCategoryProperty {

    private int id;

    private int navCategoryId;

    private int propertyId;

    /**
     * 类目属性的排序优先级，数字越小越靠前
     */
    private int priority;

    /**
     * 是否是可筛选属性
     */
    private boolean searchable;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

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

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NavCategoryProperty that = (NavCategoryProperty) o;

        if (id != that.id) return false;
        if (navCategoryId != that.navCategoryId) return false;
        if (priority != that.priority) return false;
        if (propertyId != that.propertyId) return false;
        if (searchable != that.searchable) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + navCategoryId;
        result = 31 * result + propertyId;
        result = 31 * result + priority;
        result = 31 * result + (searchable ? 1 : 0);
        return result;
    }
}
