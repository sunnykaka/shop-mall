package com.kariqu.categorycenter.client.service;

import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;

import java.util.List;

/**
 * 树形类目的树行为
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-6-28 下午7:27
 */
public interface CategoryTree {

    /**
     * 根据父亲类目查询儿子类目不包括孙子类目
     *
     * @param parentId
     * @return
     */
    List<NavigateCategory> querySubCategories(int parentId);

    /**
     * 加载整个前台类目
     *
     * @return
     */
    List<NavigateCategory> loadNavCategoryTree();


    /**
     * 查询某个前台类目关联的所有后台类目
     * @param navId
     * @return
     */
    List<Integer> queryNavigateCategoryAssociation(int navId);


}
