package com.kariqu.categorycenter.domain.repository;

import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;

import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 上午9:46
 */
public interface NavigateCategoryRepository {

    List<NavigateCategory> querySubCategories(int parentId);

    /**
     * 关联前台叶子到后台叶子，前台叶子和后台叶子时多对多关系，需要用关联表
     *
     * @param navId
     * @param categoryIds
     */
    void insertNavigateAssociation(int navId, List<Integer> categoryIds);


    /**
     * 查询所有的关联对，Map中是以前台类目ID为key的List
     *
     * @return
     */
    Map queryAllAssociation();


    /**
     * 根据后台类目ID查询关联的所有前台叶子ID
     *
     * @param categoryId
     * @return
     */
    List<Integer> queryAssociationByCategoryId(int categoryId);


    /**
     * 根据前提类目ID查询关联的后台类目ID
     *
     * @param navCategoryId
     * @return
     */
    List<Integer> queryAssociationByNavCategoryId(int navCategoryId);


    /**
     * 删除某个前台叶子的所有后台关联
     *
     * @param navId
     */
    void deleteAllNavigateAssociation(int navId);


    /**
     * 删除某个后台叶子的所有前台关联
     *
     * @param categoryId
     */
    void deleteAllCategoryAssociation(int categoryId);


    /**
     * 删除所有关联
     */
    void deleteAllAssociation();


    List<NavigateCategory> queryAllRootCategories();

    int queryNavigateCategoryByNameAndParentId(String name, int parentId);

    NavigateCategory queryNavCategoryByName(String name);


    NavigateCategory getNavigateCategoryById(int id);

    List<NavigateCategory> queryAllNavCategories();

    void deleteAllNavigateCategory();

    void deleteNavigateCategoryById(int id);

    void updateNavigateCategory(NavigateCategory navigateCategory);

    void updateNavigateCategorySettings(int id, String settings);

    void createNavigateCategory(NavigateCategory navigateCategory);
}
