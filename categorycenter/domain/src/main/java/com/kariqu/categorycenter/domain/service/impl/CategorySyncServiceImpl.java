package com.kariqu.categorycenter.domain.service.impl;

import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.repository.*;
import com.kariqu.categorycenter.domain.service.CategorySyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-7-15
 * Time: 下午11:43
 */
public class CategorySyncServiceImpl extends SqlMapClientDaoSupport implements CategorySyncService {


    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private NavigateCategoryRepository navigateCategoryRepository;


    @Autowired
    private NavCategoryPropertyRepository navCategoryPropertyRepository;

    @Autowired
    private NavCategoryPropertyValueRepository navCategoryPropertyValueRepository;


    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ValueRepository valueRepository;

    @Autowired
    private CategoryPropertyRepository categoryPropertyRepository;

    @Autowired
    private CategoryPropertyValueRepository categoryPropertyValueRepository;

    @Autowired
    private PropertyValueDetailRepository categoryPropertyValueDetailRepository;


    @Override
    public List<ProductCategory> queryAddedCategoriesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryAddedProductCategoryFromGivingTime", time);
    }

    @Override
    public List<NavigateCategory> queryAddedNavCategoriesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryAddedNavigateCategoryFromGivingTime", time);
    }

    @Override
    public List<Property> queryAddedPropertiesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryAddedPropertyFromGivingTime", time);
    }

    @Override
    public List<Value> queryAddedValuesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryAddedValueFromGivingTime", time);
    }

    @Override
    public List<CategoryProperty> queryAddedCategoryPropertiesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryAddedCategoryPropertyFromGivingTime", time);
    }

    @Override
    public List<CategoryPropertyValue> queryAddedCategoryPropertyValuesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryAddedCategoryPropertyValueFromGivingTime", time);
    }

    @Override
    public List<PropertyValueDetail> queryAddedPropertyValueDetailsFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryAddedPropertyValueDetailFromGivingTime", time);
    }

    @Override
    public List<NavCategoryProperty> queryAddedNavCategoryPropertiesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryAddedNavCategoryPropertyFromGivingTime", time);
    }

    @Override
    public List<ProductCategory> queryUpdatedCategoriesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryUpdatedProductCategoryFromGivingTime", time);
    }

    @Override
    public List<NavigateCategory> queryUpdatedNavCategoriesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryUpdatedNavigateCategoryFromGivingTime", time);
    }

    @Override
    public List<Property> queryUpdatedPropertiesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryUpdatedPropertyFromGivingTime", time);
    }

    @Override
    public List<Value> queryUpdatedValuesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryUpdatedValueFromGivingTime", time);
    }

    @Override
    public List<CategoryProperty> queryUpdatedCategoryPropertiesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryUpdatedCategoryPropertyFromGivingTime", time);
    }

    @Override
    public List<CategoryPropertyValue> queryUpdatedCategoryPropertyValuesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryUpdatedCategoryPropertyValueFromGivingTime", time);
    }

    @Override
    public List<PropertyValueDetail> queryUpdatedPropertyValueDetailsFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryAddedPropertyValueDetailFromGivingTime", time);
    }

    @Override
    public List<NavCategoryProperty> queryUpdatedNavCategoryPropertiesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryUpdatedNavCategoryPropertyFromGivingTime", time);
    }

    @Override
    public List<ProductCategory> queryDeletedCategoriesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryDeletedProductCategoryFromGivingTime", time);
    }

    @Override
    public List<NavigateCategory> queryDeletedNavCategoriesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryDeletedNavigateCategoryFromGivingTime", time);
    }

    @Override
    public List<Property> queryDeletedPropertiesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryDeletedPropertyFromGivingTime", time);
    }

    @Override
    public List<Value> queryDeletedValuesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryDeletedValueFromGivingTime", time);
    }

    @Override
    public List<CategoryProperty> queryDeletedCategoryPropertiesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryDeletedCategoryPropertyFromGivingTime", time);
    }

    @Override
    public List<CategoryPropertyValue> queryDeletedCategoryPropertyValuesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryDeletedCategoryPropertyValueFromGivingTime", time);
    }

    @Override
    public List<PropertyValueDetail> queryDeletedPropertyValueDetailsFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryDeletedPropertyValueDetailFromGivingTime", time);
    }

    @Override
    public List<NavCategoryProperty> queryDeletedNavCategoryPropertiesFromGivingTime(Date time) {
        return getSqlMapClientTemplate().queryForList("queryDeletedNavCategoryPropertyFromGivingTime", time);
    }

    @Override
    public List<ProductCategory> queryAllProductCategories() {
        return productCategoryRepository.queryAllProductCategories();
    }

    @Override
    public List<CategoryProperty> queryAllCategoryProperties() {
        return categoryPropertyRepository.queryAllCategoryProperties();
    }

    @Override
    public List<CategoryPropertyValue> queryAllCategoryPropertyValues() {
        return categoryPropertyValueRepository.queryAllCategoryPropertyValues();
    }

    @Override
    public List<PropertyValueDetail> queryAllCategoryPropertyValueDetails() {
        return categoryPropertyValueDetailRepository.queryAllPropertyValueDetails();
    }

    @Override
    public List<Property> queryAllProperties() {
        return propertyRepository.queryAllProperties();
    }

    @Override
    public List<Value> queryAllValues() {
        return valueRepository.queryAllValues();
    }

    @Override
    public List<NavigateCategory> queryAllNavCategories() {
        return navigateCategoryRepository.queryAllNavCategories();
    }

    @Override
    public List<NavCategoryPropertyValue> queryAllNavCategoryPropertyValues() {
        return navCategoryPropertyValueRepository.queryAllNavCategoryPropertyValues();
    }

    @Override
    public List<NavCategoryProperty> queryAllNavCategoryProperties() {
        return navCategoryPropertyRepository.queryAllNavCategoryProperties();
    }

    @Override
    public Map<Integer, List<Integer>> queryAllAssociation() {
        return navigateCategoryRepository.queryAllAssociation();
    }
}
