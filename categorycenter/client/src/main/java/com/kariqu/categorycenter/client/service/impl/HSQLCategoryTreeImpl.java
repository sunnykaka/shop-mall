package com.kariqu.categorycenter.client.service.impl;

import com.kariqu.categorycenter.client.service.CategoryTree;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.CategoryAssociationService;
import com.kariqu.categorycenter.domain.service.NavigateCategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 类目树的HSQL实现
 *
 * @Author: Tiger
 * @Since: 11-6-29 下午10:25
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class HSQLCategoryTreeImpl implements CategoryTree {

    /**
     * 委托给domain的前台类目服务，使用关系数据库
     */
    @Autowired
    private NavigateCategoryService navigateCategoryService;

    @Autowired
    private CategoryAssociationService categoryAssociationService;


    @Override
    public List<NavigateCategory> querySubCategories(int parentId) {
        return navigateCategoryService.querySubCategories(parentId);
    }

    @Override
    public List<NavigateCategory> loadNavCategoryTree() {
        return navigateCategoryService.loadNavCategoryTree();
    }


    @Override
    public List<Integer> queryNavigateCategoryAssociation(int navId) {
        return categoryAssociationService.queryAssociationByNavCategoryId(navId);
    }


}
