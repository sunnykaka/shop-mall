package com.kariqu.categorycenter.client.service.impl;

import com.kariqu.categorycenter.client.service.CategoryTree;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;

import java.util.Collections;
import java.util.List;

/**
 * 类目树形结构实现，此类常驻内存.
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-6-28 下午6:19
 */
public class CategoryTreeImpl implements CategoryTree {

    @Override
    public List<NavigateCategory> querySubCategories(int parentId) {
        return Collections.emptyList();
    }

    @Override
    public List<NavigateCategory> loadNavCategoryTree() {
        return Collections.emptyList();
    }

    @Override
    public List<Integer> queryNavigateCategoryAssociation(int navId) {
        return Collections.emptyList();

    }
}
