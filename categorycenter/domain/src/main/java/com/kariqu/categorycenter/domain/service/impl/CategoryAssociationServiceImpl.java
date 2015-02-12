package com.kariqu.categorycenter.domain.service.impl;

import com.kariqu.categorycenter.domain.repository.NavCategoryPropertyRepository;
import com.kariqu.categorycenter.domain.repository.NavCategoryPropertyValueRepository;
import com.kariqu.categorycenter.domain.repository.NavigateCategoryRepository;
import com.kariqu.categorycenter.domain.service.CategoryAssociationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-10-25
 * Time: 下午1:16
 */
public class CategoryAssociationServiceImpl implements CategoryAssociationService {

    @Autowired
    private NavigateCategoryRepository navigateCategoryRepository;

    @Autowired
    private NavCategoryPropertyRepository navCategoryPropertyRepository;

    @Autowired
    private NavCategoryPropertyValueRepository navCategoryPropertyValueRepository;

    /**
     * 关联的时候是先删除原来的所有关联再建立新的关联
     * @param navCategoryId
     * @param categoryIds
     */
    @Override
    public void createNavigateCategoryAssociation(int navCategoryId, List<Integer> categoryIds) {
        navigateCategoryRepository.deleteAllNavigateAssociation(navCategoryId);
        navCategoryPropertyRepository.deleteNavCategoryPropertyByNavCategoryId(navCategoryId);
        navCategoryPropertyValueRepository.deleteNavCategoryPropertyValueByNavCategoryId(navCategoryId);
        navigateCategoryRepository.insertNavigateAssociation(navCategoryId, categoryIds);
    }

    @Override
    public void deleteAllAssociation() {
        navigateCategoryRepository.deleteAllAssociation();
    }

    @Override
    public Map queryAllAssociation() {
        return navigateCategoryRepository.queryAllAssociation();
    }

    @Override
    public List<Integer> queryAssociationByCategoryId(int categoryId) {
        return navigateCategoryRepository.queryAssociationByCategoryId(categoryId);
    }

    @Override
    public List<Integer> queryAssociationByNavCategoryId(int navCategoryId) {
        return navigateCategoryRepository.queryAssociationByNavCategoryId(navCategoryId);
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
}
