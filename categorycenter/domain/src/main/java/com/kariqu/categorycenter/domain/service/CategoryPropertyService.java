package com.kariqu.categorycenter.domain.service;

import com.kariqu.categorycenter.domain.model.*;

import java.util.List;

/**
 * 类目属性相关服务
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午1:54
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public interface CategoryPropertyService {

    /* property 相关 */
    int createProperty(Property property);

    /**
     * 如果这个属性在数据库中不存在则创建,返回属性的ID
     *
     * @param property
     * @return
     */
    int createPropertyIfNotExist(Property property);

    Property getPropertyById(int id);

    Property getPropertyByName(String name);

    void updateProperty(Property property);

    void deleteProperty(int id);

    void deleteAllProperty();


    List<Property> queryAllProperties();

    int createValue(Value value);

    /**
     * 如果这个值在数据库中不存在则创建，返回值的ID
     *
     * @param value
     * @return
     */
    int createValueIfNotExist(Value value);

    Value getValueById(int id);

    Value getValueByName(String valueName);

    void updateValue(Value value);

    void deleteValue(int id);

    void deleteAllValue();

    List<Value> queryAllValues();


    /* categoryProperty 相关*/
    int createCategoryProperty(CategoryProperty categoryProperty);

    CategoryProperty getCategoryPropertyById(int id);

    CategoryProperty queryCategoryPropertyByCategoryIdAndPropertyId(int categoryId,int propertyId);


    /* 根据类目ID和属性类型，查询某个类目下某种属性类型的所有的类目属性 */
    List<CategoryProperty> getCategoryProperties(int categoryId, PropertyType propertyType);

    /**
     * 根据类目ID查询所有的类目属性
     *
     * @param categoryId
     * @return
     */
    List<CategoryProperty> queryCategoryPropertyByCategoryId(int categoryId);

    void updateCategoryProperty(CategoryProperty categoryProperty);

    void deleteCategoryProperty(int id);

    /**
     * 删除类目属性By类目ID
     *
     * @param categoryId
     */
    void deleteCategoryPropertyByCategoryId(int categoryId);

    void deleteAllCategoryProperty();

    /**
     * 删除CP，使用的时候和deleteCategoryPropertyValueByCPId配合保证数据一致
     */
    void deleteCategoryPropertyByCPId(int categoryId, int propertyId);


    List<CategoryProperty> queryAllCategoryProperties();


    /* categoryPropertyValue 相关 */
    int createCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue);

    CategoryPropertyValue getCategoryPropertyValueById(int id);

    CategoryPropertyValue queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(int categoryId, int propertyId, int valueId);


    List<CategoryPropertyValue> getCategoryPropertyValues(int categoryId);

    /* 根据类目ID和属性ID，查询某个类目下，某个属性的所有类目属性值 */
    List<CategoryPropertyValue> getCategoryPropertyValues(int categoryId, int propertyId);

    void updateCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue);

    void deleteCategoryPropertyValue(int id);

    void deleteAllCategoryPropertyValue();

    /* 删除CPV */
    void deleteCategoryPropertyValueByCPVId(int categoryId, int propertyId, int valueId);

    /**
     * 根据CP删除CPV
     */
    void deleteCategoryPropertyValueByCPId(int categoryId, int propertyId);

    List<CategoryPropertyValue> queryAllCategoryPropertyValues();


    /* propertyValueDetail 相关 */
    int createCategoryPropertyValueDetail(PropertyValueDetail propertyValueDetail);

    PropertyValueDetail getCategoryPropertyValueDetail(int id);

    PropertyValueDetail getPropertyValueDetail(int propertyId, int valueId);

    void updateCategoryPropertyValueDetail(PropertyValueDetail propertyValueDetail);

    void deleteCategoryPropertyValueDetail(int id);

    void deleteAllCategoryPropertyValueDetail();

    List<PropertyValueDetail> queryAllCategoryPropertyValueDetails();


}
