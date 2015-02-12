package com.kariqu.categorycenter.domain.repository;

import com.kariqu.categorycenter.domain.model.ProductCategory;

import java.util.List;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午2:40
 */
public interface ProductCategoryRepository{

    List<ProductCategory> querySubCategoryTree(int parentId);

    int queryProductCategoryByNameAndParentId(String name, int parentId);

    void create(ProductCategory productCategory);

    ProductCategory getProductCategoryById(int id);

    void updateProductCategory(ProductCategory productCategory);

    void deleteProductCategoryById(int id);

    void deleteAllProductCategory();

    List<ProductCategory> queryAllProductCategories();
}
