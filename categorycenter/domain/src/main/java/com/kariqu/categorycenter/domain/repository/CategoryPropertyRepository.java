package com.kariqu.categorycenter.domain.repository;

import com.kariqu.categorycenter.domain.model.CategoryProperty;
import com.kariqu.categorycenter.domain.model.PropertyType;

import java.util.List;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午2:41
 */
public interface CategoryPropertyRepository{

    /* 根据类目ID和属性类型，查询某个类目下某种属性类型的所有的类目属性 */
    List<CategoryProperty> getCategoryProperties(int categoryId, PropertyType propertyType);

    /**
     * 删除CP
     */
    void deleteCategoryPropertyByCPId(int categoryId, int propertyId);

    /**
     * 删除类目属性By类目ID
     * @param categoryId
     */
    void deleteCategoryPropertyByCategoryId(int categoryId);

    /**
     * 根据类目ID查询所有的类目属性
     * @param categoryId
     * @return
     */
    List<CategoryProperty> queryCategoryPropertyByCategoryId(int categoryId);

    /**
     * 根据类目ID和属性ID查询类目属性
     * @param categoryId
     * @param propertyId
     * @return
     */
    CategoryProperty queryCategoryPropertyByCategoryIdAndPropertyId(int categoryId,int propertyId);

    void createCategoryProperty(CategoryProperty categoryProperty);

    CategoryProperty getCategoryPropertyById(int id);

    void updateCategoryProperty(CategoryProperty categoryProperty);

    void deleteCategoryPropertyById(int id);

    void deleteAllCategoryProperty();

    List<CategoryProperty> queryAllCategoryProperties();
}
