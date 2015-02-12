package com.kariqu.categorycenter.domain.service;

import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;

import java.util.List;

/**
 * 导航类目属性服务
 * 导航类目的属性和值是从关联的后台类目叶子上得到的
 * @Author: Tiger
 * @Since: 11-7-4 下午10:42
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public interface NavigateCategoryPropertyService {

    int createNavCategoryProperty(NavCategoryProperty navCategoryProperty);

    NavCategoryProperty getNavCategoryPropertyById(int id);

    void updateNavCategoryProperty(NavCategoryProperty navCategoryProperty);

    void deleteNavCategoryProperty(int id);

    void deleteAllNavCategoryProperty();

    void deleteNavCategoryPropertyByNavCategoryId(int navCategoryId);

    List<NavCategoryProperty> queryAllNavCategoryProperties();

    List<NavCategoryProperty> queryNavCategoryProperties(int navCategoryId);

    List<NavCategoryProperty> queryNavCategoryPropertiesSearchable(int navCategoryId);


    int createNavCategoryPropertyValue(NavCategoryPropertyValue navCategoryPropertyValue);

    NavCategoryPropertyValue getNavCategoryPropertyValueById(int id);

    void updateNavCategoryPropertyValue(NavCategoryPropertyValue navCategoryPropertyValue);

    void deleteNavCategoryPropertyValue(int id);

    void deleteAllNavCategoryPropertyValue();

    List<NavCategoryPropertyValue> queryNavCategoryPropertyValues(int navCategoryId, int propertyId);

    List<NavCategoryPropertyValue> queryAllNavCategoryPropertyValues();

    /**
     * 设置类目属性为可筛选
     * @param navCategoryId
     * @param propertyId
     */
    void makeNavigateCategoryPropertySearchable(int navCategoryId, int propertyId);

    /**
     *  设置类目属性不可筛选
     * @param categoryId
     */
    void makeAllNavigateCategoryPropertyUnSearchable(int categoryId);

    /**
     * 查询某个类目下的属性
     * @param categoryId
     * @return
     */
    List<NavCategoryProperty> querySearchableNavCategoryProperty(int categoryId);

}
