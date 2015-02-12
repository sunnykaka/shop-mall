package com.kariqu.categorycenter.domain.repository;

import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;

import java.util.List;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 上午9:48
 */
public interface NavCategoryPropertyValueRepository{

    List<NavCategoryPropertyValue> queryNavCategoryPropertyValues(int navCategoryId,int propertyId);

    void deleteNavCategoryPropertyValueByNavCategoryId(int navCategoryId);

    void createNavCategoryPropertyValue(NavCategoryPropertyValue navCategoryPropertyValue);

    NavCategoryPropertyValue getNavCategoryPropertyValueById(int id);

    void updateNavCategoryPropertyValue(NavCategoryPropertyValue navCategoryPropertyValue);

    void deleteNavCategoryPropertyValueById(int id);

    void deleteAllNavCategoryPropertyValue();

    List<NavCategoryPropertyValue> queryAllNavCategoryPropertyValues();
}
