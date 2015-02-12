package com.kariqu.categorycenter.domain.service;

import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategorySettings;

import java.util.List;

/**
 * 导航类目服务
 *
 * @Author: Tiger
 * @Since: 11-7-4 下午10:41
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public interface NavigateCategoryService {

    public int createNavigateCategory(NavigateCategory navigateCategory);

    public NavigateCategory getNavigateCategory(int id);

    public void updateNavigateCategory(NavigateCategory navigateCategory);

    void updateNavigateCategorySettings(NavigateCategorySettings navSetting);

    public void deleteNavigateCategory(int id);

    public void deleteAllNavigateCategory();

    /**
     * 加载整个前台类目树
     *
     * @return
     */
    List<NavigateCategory> loadNavCategoryTree();

    /**
     * 根据儿子类目反向查询父亲类目
     *
     * @param navCategoryId
     * @param includedAll
     * @return
     */
    List<NavigateCategory> getParentCategories(int navCategoryId, boolean includedAll);

    /**
     * 根据父亲类目查询儿子类目不包括孙子类目
     *
     * @param parentId
     * @return
     */
    List<NavigateCategory> querySubCategories(int parentId);

    /**
     * 查询所有的前台根类目
     *
     * @return
     */
    List<NavigateCategory> queryAllRootCategories();

    List<NavigateCategory> queryAllNavCategories();

    int queryNavigateCategoryByNameAndParentId(String name, int parentId);

    /**
     * 通过类目名查询类目
     *
     * @param name
     * @return
     */
    NavigateCategory queryNavigateCategoryByName(String name);

}
