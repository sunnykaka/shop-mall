package com.kariqu.categorycenter.domain.repository;

import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;

import java.util.List;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 上午9:48
 */
public interface NavCategoryPropertyRepository{

    List<NavCategoryProperty> queryNavCategoryProperties(int navCategoryId);

    void deleteNavCategoryPropertyByNavCategoryId(int navCategoryId);

    List<NavCategoryProperty> queryNavCategoryPropertiesSearchable(int navCategoryId);

    void makeNavigateCategoryPropertySearchable(int categoryId, int propertyId);

    void makeAllNavigateCategoryPropertyUnSearchable(int categoryId);

    List<NavCategoryProperty> queryAllNavCategoryProperties();

    void deleteAllNavCategoryProperty();

    void deleteNavCategoryPropertyById(int id);

    void updateNavCategoryProperty(NavCategoryProperty navCategoryProperty);

    NavCategoryProperty getNavCategoryPropertyById(int id);

    void createNavCategoryProperty(NavCategoryProperty navCategoryProperty);
}
