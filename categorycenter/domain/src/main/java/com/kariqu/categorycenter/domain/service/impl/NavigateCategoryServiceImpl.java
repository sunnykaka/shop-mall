package com.kariqu.categorycenter.domain.service.impl;

import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategorySettings;
import com.kariqu.categorycenter.domain.repository.NavCategoryPropertyRepository;
import com.kariqu.categorycenter.domain.repository.NavCategoryPropertyValueRepository;
import com.kariqu.categorycenter.domain.repository.NavigateCategoryRepository;
import com.kariqu.categorycenter.domain.service.NavigateCategoryService;
import com.kariqu.common.json.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 下午2:24
 */
public class NavigateCategoryServiceImpl implements NavigateCategoryService {

    @Autowired
    private NavigateCategoryRepository navigateCategoryRepository;

    @Autowired
    private NavCategoryPropertyRepository navCategoryPropertyRepository;

    @Autowired
    private NavCategoryPropertyValueRepository navCategoryPropertyValueRepository;


    @Override
    @Transactional
    public int createNavigateCategory(NavigateCategory navigateCategory) {
        navigateCategoryRepository.createNavigateCategory(navigateCategory);
        return navigateCategory.getId();
    }


    @Override
    public NavigateCategory getNavigateCategory(int id) {
        return navigateCategoryRepository.getNavigateCategoryById(id);
    }

    @Override
    @Transactional
    public void updateNavigateCategory(NavigateCategory navigateCategory) {
        navigateCategoryRepository.updateNavigateCategory(navigateCategory);
    }

    @Override
    public void updateNavigateCategorySettings(NavigateCategorySettings navSetting) {
        navigateCategoryRepository.updateNavigateCategorySettings(navSetting.getNavId(), JsonUtil.objectToJson(navSetting));
    }

    @Override
    @Transactional
    public void deleteNavigateCategory(int id) {
        navigateCategoryRepository.deleteAllNavigateAssociation(id);
        navigateCategoryRepository.deleteNavigateCategoryById(id);
        navCategoryPropertyRepository.deleteNavCategoryPropertyByNavCategoryId(id);
        navCategoryPropertyValueRepository.deleteNavCategoryPropertyValueByNavCategoryId(id);
    }

    @Override
    @Transactional
    public void deleteAllNavigateCategory() {
        navigateCategoryRepository.deleteAllNavigateCategory();
    }

    private void init(List<NavigateCategory> children, NavigateCategory parent) {
        if (null == children || children.isEmpty()) {
            return;
        }
        parent.setChildren(children);
        for (NavigateCategory child : children) {
            List<NavigateCategory> subCategories = this.querySubCategories(child.getId());
            init(subCategories, child);
        }
    }

    @Override
    public List<NavigateCategory> loadNavCategoryTree() {
        List<NavigateCategory> productCategories = this.querySubCategories(-1);
        init(productCategories, new NavigateCategory());
        return productCategories;
    }

    @Override
    public List<NavigateCategory> getParentCategories(int navCategoryId, boolean includedAll) {
        List<NavigateCategory> navigateCategories = recursiveCategoryTree(navCategoryId, includedAll);
        Collections.reverse(navigateCategories);
        return navigateCategories;
    }

    private List<NavigateCategory> recursiveCategoryTree(int navCategoryId, boolean includedAll) {
        List<NavigateCategory> navigateCategories = new ArrayList<NavigateCategory>();
        NavigateCategory navigateCategory = getNavigateCategory(navCategoryId);
        if (navigateCategory.getParent().getId() == -1) {
            return Collections.emptyList();
        } else if (!includedAll) {
            navigateCategories.add(getNavigateCategory(navigateCategory.getParent().getId()));
            return navigateCategories;
        } else {
            NavigateCategory parent = getNavigateCategory(navigateCategory.getParent().getId());
            navigateCategories.add(parent);
            navigateCategories.addAll(recursiveCategoryTree(parent.getId(), true));
            return navigateCategories;
        }
    }

    @Override
    public List<NavigateCategory> querySubCategories(int parentId) {
        return navigateCategoryRepository.querySubCategories(parentId);
    }

    @Override
    public List<NavigateCategory> queryAllRootCategories() {
        return navigateCategoryRepository.queryAllRootCategories();
    }

    @Override
    public List<NavigateCategory> queryAllNavCategories() {
        return navigateCategoryRepository.queryAllNavCategories();
    }

    public void setNavigateCategoryRepository(NavigateCategoryRepository navigateCategoryRepository) {
        this.navigateCategoryRepository = navigateCategoryRepository;
    }

    public void setNavCategoryPropertyRepository(NavCategoryPropertyRepository navCategoryPropertyRepository) {
        this.navCategoryPropertyRepository = navCategoryPropertyRepository;
    }

    public void setNavCategoryPropertyValueRepository(NavCategoryPropertyValueRepository navCategoryPropertyValueRepository) {
        this.navCategoryPropertyValueRepository = navCategoryPropertyValueRepository;
    }

    @Override
    public int queryNavigateCategoryByNameAndParentId(String name, int parentId) {
        return navigateCategoryRepository.queryNavigateCategoryByNameAndParentId(name, parentId);
    }

    @Override
    public NavigateCategory queryNavigateCategoryByName(String name) {
        return navigateCategoryRepository.queryNavCategoryByName(name);
    }

}
