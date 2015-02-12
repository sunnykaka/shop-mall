package com.kariqu.categorycenter.domain.service.impl;

import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;
import com.kariqu.categorycenter.domain.repository.NavCategoryPropertyRepository;
import com.kariqu.categorycenter.domain.repository.NavCategoryPropertyValueRepository;
import com.kariqu.categorycenter.domain.service.NavigateCategoryPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 下午2:27
 */
public class NavigateCategoryPropertyServiceImpl implements NavigateCategoryPropertyService {

    @Autowired
    private NavCategoryPropertyRepository navCategoryPropertyRepository;

    @Autowired
    private NavCategoryPropertyValueRepository navCategoryPropertyValueRepository;


    @Override
    @Transactional
    public int createNavCategoryProperty(NavCategoryProperty navCategoryProperty) {
        navCategoryPropertyRepository.createNavCategoryProperty(navCategoryProperty);
        return navCategoryProperty.getId();
    }

    @Override
    public NavCategoryProperty getNavCategoryPropertyById(int id) {
        return navCategoryPropertyRepository.getNavCategoryPropertyById(id);
    }

    @Override
    @Transactional
    public void updateNavCategoryProperty(NavCategoryProperty navCategoryProperty) {
        navCategoryPropertyRepository.updateNavCategoryProperty(navCategoryProperty);
    }

    @Override
    @Transactional
    public void deleteNavCategoryProperty(int id) {
        navCategoryPropertyRepository.deleteNavCategoryPropertyById(id);
    }

    @Override
    @Transactional
    public void deleteAllNavCategoryProperty() {
        navCategoryPropertyRepository.deleteAllNavCategoryProperty();
    }

    @Override
    @Transactional
    public void deleteAllNavCategoryPropertyValue() {
        navCategoryPropertyValueRepository.deleteAllNavCategoryPropertyValue();
    }

    @Override
    public void deleteNavCategoryPropertyByNavCategoryId(int navCategoryId) {
        navCategoryPropertyRepository.deleteNavCategoryPropertyByNavCategoryId(navCategoryId);
        navCategoryPropertyValueRepository.deleteNavCategoryPropertyValueByNavCategoryId(navCategoryId);
    }

    @Override
    public List<NavCategoryProperty> queryAllNavCategoryProperties() {
        return navCategoryPropertyRepository.queryAllNavCategoryProperties();
    }

    @Override
    public int createNavCategoryPropertyValue(NavCategoryPropertyValue navCategoryPropertyValue) {
        navCategoryPropertyValueRepository.createNavCategoryPropertyValue(navCategoryPropertyValue);
        return navCategoryPropertyValue.getId();
    }

    @Override
    public NavCategoryPropertyValue getNavCategoryPropertyValueById(int id) {
        return navCategoryPropertyValueRepository.getNavCategoryPropertyValueById(id);
    }

    @Override
    public void updateNavCategoryPropertyValue(NavCategoryPropertyValue navCategoryPropertyValue) {
        navCategoryPropertyValueRepository.updateNavCategoryPropertyValue(navCategoryPropertyValue);
    }

    @Override
    public void deleteNavCategoryPropertyValue(int id) {
        navCategoryPropertyValueRepository.deleteNavCategoryPropertyValueById(id);
    }

    @Override
    public List<NavCategoryPropertyValue> queryNavCategoryPropertyValues(int navCategoryId, int propertyId) {
        return navCategoryPropertyValueRepository.queryNavCategoryPropertyValues(navCategoryId, propertyId);
    }

    @Override
    public List<NavCategoryPropertyValue> queryAllNavCategoryPropertyValues() {
        return navCategoryPropertyValueRepository.queryAllNavCategoryPropertyValues();
    }

    @Override
    public void makeNavigateCategoryPropertySearchable(int navCategoryId, int propertyId) {
        navCategoryPropertyRepository.makeNavigateCategoryPropertySearchable(navCategoryId, propertyId);
    }

    @Override
    public void makeAllNavigateCategoryPropertyUnSearchable(int categoryId) {
        navCategoryPropertyRepository.makeAllNavigateCategoryPropertyUnSearchable(categoryId);
    }

    @Override
    public List<NavCategoryProperty> querySearchableNavCategoryProperty(int categoryId) {
        List<NavCategoryProperty> navCategoryProperties = navCategoryPropertyRepository.queryNavCategoryPropertiesSearchable(categoryId);
        return navCategoryProperties;
    }

    @Override
    public List<NavCategoryProperty> queryNavCategoryProperties(int navCategoryId) {
        List<NavCategoryProperty> navCategoryProperties = navCategoryPropertyRepository.queryNavCategoryProperties(navCategoryId);
        return navCategoryProperties;
    }

    @Override
    public List<NavCategoryProperty> queryNavCategoryPropertiesSearchable(int navCategoryId) {
        List<NavCategoryProperty> navCategoryProperties = navCategoryPropertyRepository.queryNavCategoryPropertiesSearchable(navCategoryId);
        return navCategoryProperties;
    }

    public void setNavCategoryPropertyRepository(NavCategoryPropertyRepository navCategoryPropertyRepository) {
        this.navCategoryPropertyRepository = navCategoryPropertyRepository;
    }

    public void setNavCategoryPropertyValueRepository(NavCategoryPropertyValueRepository navCategoryPropertyValueRepository) {
        this.navCategoryPropertyValueRepository = navCategoryPropertyValueRepository;
    }
}
