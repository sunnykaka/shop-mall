package com.kariqu.categorycenter.client.service.impl;

import com.kariqu.categorycenter.client.domain.PropertyValueStatsInfo;
import com.kariqu.categorycenter.client.service.CategoryPropertyQueryService;
import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import com.kariqu.categorycenter.domain.service.NavigateCategoryPropertyService;
import com.kariqu.categorycenter.domain.util.PropertyValueUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 封装类目Domain的服务，只提供查询方法
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-6-28 下午7:23
 */
public class CategoryPropertyQueryServiceImpl implements CategoryPropertyQueryService {

    private static final Log logger = LogFactory.getLog(CategoryPropertyQueryServiceImpl.class);

    @Autowired
    private CategoryPropertyService categoryPropertyService;

    @Autowired
    private NavigateCategoryPropertyService navigateCategoryPropertyService;


    @Override
    public Property getPropertyById(int propertyId) {
        return categoryPropertyService.getPropertyById(propertyId);
    }

    @Override
    public Property getPropertyByName(String name) {
        return categoryPropertyService.getPropertyByName(name);
    }

    @Override
    public Value getValueById(int valueId) {
        return categoryPropertyService.getValueById(valueId);
    }

    @Override
    public Value getValueByName(String valueName) {
        return categoryPropertyService.getValueByName(valueName);
    }

    /**
     * 没有包含统计信息
     * 根据名称查询属性，取出属性Id(pid)与类目id(cid)组合条件查询得到类目某个属性包含的属性值
     * @param navId
     * @param propertyName
     * @return
     */
    @Override
    public List<PropertyValueStatsInfo> queryValueByNavCategoryIdAndPropertyName(int navId, String propertyName) {
        Property property = categoryPropertyService.getPropertyByName(propertyName);
        if (property == null) {
            return Collections.emptyList();
        }
        List<NavCategoryPropertyValue> navCategoryPropertyValues = navigateCategoryPropertyService.queryNavCategoryPropertyValues(navId, property.getId());
        List<PropertyValueStatsInfo> valueList = new LinkedList<PropertyValueStatsInfo>();
        for (NavCategoryPropertyValue navCategoryPropertyValue : navCategoryPropertyValues) {
            Value value = categoryPropertyService.getValueById(navCategoryPropertyValue.getValueId());
            if (value == null)
                logger.warn("值表中没有 id(" + navCategoryPropertyValue.getValueId() + ") 的信息");
            else
                valueList.add(new PropertyValueStatsInfo(value.getValueName(), PropertyValueUtil.mergePidVidToLong(property.getId(), value.getId())));
        }
        return valueList;
    }

    @Override
    public CategoryProperty getCategoryPropertyById(int id) {
        return categoryPropertyService.getCategoryPropertyById(id);
    }

    @Override
    public List<CategoryProperty> getCategoryProperties(int categoryId, PropertyType propertyType) {
        return categoryPropertyService.getCategoryProperties(categoryId, propertyType);
    }

    @Override
    public List<CategoryProperty> queryCategoryPropertyByCategoryId(int categoryId) {
        return categoryPropertyService.queryCategoryPropertyByCategoryId(categoryId);
    }

    @Override
    public CategoryPropertyValue getCategoryPropertyValueById(int id) {
        return categoryPropertyService.getCategoryPropertyValueById(id);
    }

    @Override
    public CategoryPropertyValue queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(int categoryId, int propertyId, int valueId) {
        return categoryPropertyService.queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(categoryId, propertyId, valueId);
    }

    @Override
    public List<CategoryPropertyValue> getCategoryPropertyValues(int categoryId) {
        return categoryPropertyService.getCategoryPropertyValues(categoryId);
    }

    @Override
    public List<CategoryPropertyValue> queryCategoryPropertyValues(int categoryId, int propertyId) {
        return categoryPropertyService.getCategoryPropertyValues(categoryId, propertyId);
    }

    @Override
    public PropertyValueDetail getCategoryPropertyValueDetail(int id) {
        return categoryPropertyService.getCategoryPropertyValueDetail(id);
    }

    @Override
    public PropertyValueDetail getCategoryPropertyValueDetail(int propertyId, int valueId) {
        return categoryPropertyService.getPropertyValueDetail(propertyId, valueId);
    }

    @Override
    public List<NavCategoryProperty> queryNavCategoryProperties(int navCategoryId) {
        return navigateCategoryPropertyService.queryNavCategoryProperties(navCategoryId);
    }

    @Override
    public NavCategoryProperty getNavCategoryPropertyById(int id) {
        return navigateCategoryPropertyService.getNavCategoryPropertyById(id);
    }

    @Override
    public NavCategoryPropertyValue getNavCategoryPropertyValueById(int id) {
        return navigateCategoryPropertyService.getNavCategoryPropertyValueById(id);
    }

    @Override
    public List<NavCategoryPropertyValue> queryNavCategoryPropertyValues(int navCategoryId, int propertyId) {
        return navigateCategoryPropertyService.queryNavCategoryPropertyValues(navCategoryId, propertyId);
    }

}
