package com.kariqu.categorycenter.domain.service;

import com.kariqu.categorycenter.domain.model.ProductCategory;

import java.util.List;

/**
 * 产品类目服务
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午1:42
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public interface ProductCategoryService {

    int createProductCategory(ProductCategory productCategory);

    ProductCategory getProductCategoryById(int id);

    void updateProductCategory(ProductCategory productCategory);

    void deleteProductCategory(int id);

    void deleteAllProductCategory();

    List<ProductCategory> queryAllProductCategories();

    /**
     * 根据父亲类目查询所有的儿子类目不包括孙子类目
     * @param parentId
     * @return
     */
    List<ProductCategory> querySubCategories(int parentId);

    /**
     * 根据类目ID反向查询父亲类目
     *
     * @param categoryId
     * @param includedAll 如果为true，则表示递归查询出所有的类目
     * @return
     */
    List<ProductCategory> getParentCategories(int categoryId, boolean includedAll);

    /**
     * 获取最顶级的类目
     * @param categoryId
     * @return
     */
    ProductCategory queryTopCategoryById(int categoryId);

    /**
     * 递归加载整个类目
     * @return
     */
    List<ProductCategory> loadCategoryTree();

    /**
     * 根据该类目名称和父类目ID查询此类目是否存在
     * @param name
     * @param parentId
     * @return
     */
    int queryProductCategoryByNameAndParentId(String name, int parentId);

    /**
     * 递归加载当前类目
     * @return
     */
    List<ProductCategory> loadCategoryTreeById(int categoryId);
}
