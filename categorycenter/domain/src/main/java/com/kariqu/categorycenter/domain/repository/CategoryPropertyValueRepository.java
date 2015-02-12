package com.kariqu.categorycenter.domain.repository;

import com.kariqu.categorycenter.domain.model.CategoryPropertyValue;

import java.util.List;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午2:40
 */
public interface CategoryPropertyValueRepository{

    /* 根据类目ID和属性ID，查询某个类目下，某个属性的所有类目属性值 */
    List<CategoryPropertyValue> getCategoryPropertyValues(int categoryId, int propertyId);

    /* 查询某个类目下的所有属性值对 */
    List<CategoryPropertyValue> getCategoryPropertyValues(int categoryId);

    /* 删除CPV */
    void deleteCategoryPropertyValueByCPVId(int categoryId, int propertyId, int valueId);

    /**
     * 根据CP删除PCV
     */
    void deleteCategoryPropertyValueByCPId(int categoryId, int propertyId);


    /**
     * 根据类目ID删除类目属性值
     *
     * @param categoryId
     */
    void deleteCategoryPropertyValueByCategoryId(int categoryId);

    /**
     * 根据类目ID，属性ID，值ID查询到类目属性值对象
     *
     * @param categoryId
     * @param propertyId
     * @param valueId
     * @return
     */
    CategoryPropertyValue queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(int categoryId, int propertyId, int valueId);

    void createCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue);

    CategoryPropertyValue getCategoryPropertyValueById(int id);

    void updateCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue);

    void deleteCategoryPropertyValueById(int id);

    void deleteAllCategoryPropertyValue();

    List<CategoryPropertyValue> queryAllCategoryPropertyValues();
}
