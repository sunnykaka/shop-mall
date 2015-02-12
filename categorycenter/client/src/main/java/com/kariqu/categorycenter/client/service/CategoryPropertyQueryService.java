package com.kariqu.categorycenter.client.service;

import com.kariqu.categorycenter.client.domain.PropertyValueStatsInfo;
import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;

import java.util.List;

/**
 * 类目属性查询服务
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-6-27 下午3:43
 */
public interface CategoryPropertyQueryService {


    /**
     * 查询某个前台类目下某个属性的所有值
     * 比如查询厨房用品下品牌这个属性的所有值
     * @param navId
     * @param propertyName
     * @return
     */
    List<PropertyValueStatsInfo> queryValueByNavCategoryIdAndPropertyName(int navId, String propertyName);

    Property getPropertyById(int propertyId);

    Property getPropertyByName(String name);

    Value getValueById(int valueId);

    Value getValueByName(String valueName);


    CategoryProperty getCategoryPropertyById(int id);

    List<CategoryProperty> getCategoryProperties(int categoryId, PropertyType propertyType);

    List<CategoryProperty> queryCategoryPropertyByCategoryId(int categoryId);


    CategoryPropertyValue getCategoryPropertyValueById(int id);

    CategoryPropertyValue queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(int categoryId, int propertyId, int valueId);


    List<CategoryPropertyValue> getCategoryPropertyValues(int categoryId);

    List<CategoryPropertyValue> queryCategoryPropertyValues(int categoryId, int propertyId);


    PropertyValueDetail getCategoryPropertyValueDetail(int id);

    PropertyValueDetail getCategoryPropertyValueDetail(int propertyId, int valueId);


    NavCategoryProperty getNavCategoryPropertyById(int id);

    List<NavCategoryProperty> queryNavCategoryProperties(int navCategoryId);


    NavCategoryPropertyValue getNavCategoryPropertyValueById(int id);

    List<NavCategoryPropertyValue> queryNavCategoryPropertyValues(int navCategoryId, int propertyId);



}
