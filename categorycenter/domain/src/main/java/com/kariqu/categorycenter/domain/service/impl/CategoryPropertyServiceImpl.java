package com.kariqu.categorycenter.domain.service.impl;

import com.kariqu.categorycenter.domain.model.*;
import com.kariqu.categorycenter.domain.repository.*;
import com.kariqu.categorycenter.domain.service.CategoryPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Asion
 * Date: 11-6-26
 * Time: 下午1:39
 */
public class CategoryPropertyServiceImpl implements CategoryPropertyService {

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
    public int createProperty(Property property) {
        propertyRepository.createProperty(property);
        return property.getId();
    }

    @Override
    public int createPropertyIfNotExist(Property property) {
        Property currentProperty = propertyRepository.getPropertyByName(property.getName());
        if (currentProperty == null) {
            propertyRepository.createProperty(property);
            return property.getId();
        } else {
            return currentProperty.getId();
        }
    }

    @Override
    public Property getPropertyById(int id) {
        return propertyRepository.getPropertyById(id);
    }

    @Override
    public Property getPropertyByName(String name) {
        return propertyRepository.getPropertyByName(name);
    }

    @Override
    public void updateProperty(Property property) {
        propertyRepository.updateProperty(property);
    }

    @Override
    public void deleteProperty(int id) {
        propertyRepository.deletePropertyById(id);
    }

    @Override
    public void deleteAllProperty() {
        propertyRepository.deleteAllProperty();
    }

    @Override
    public void deleteAllValue() {
        valueRepository.deleteAllValue();
    }

    /**
     * 清空类目属性和属性值(逻辑删除)
     * @param categoryId
     */
    @Override
    @Transactional
    public void deleteCategoryPropertyByCategoryId(int categoryId) {
        categoryPropertyRepository.deleteCategoryPropertyByCategoryId(categoryId);
        categoryPropertyValueRepository.deleteCategoryPropertyValueByCategoryId(categoryId);
    }


    @Override
    public void deleteAllCategoryProperty() {
        categoryPropertyRepository.deleteAllCategoryProperty();
    }

    @Override
    public void deleteAllCategoryPropertyValue() {
        categoryPropertyValueRepository.deleteAllCategoryPropertyValue();
    }

    @Override
    public void deleteAllCategoryPropertyValueDetail() {
        categoryPropertyValueDetailRepository.deleteAllPropertyValueDetail();
    }

    @Override
    public List<Property> queryAllProperties() {
        return propertyRepository.queryAllProperties();
    }

    @Override
    public int createValue(Value value) {
        valueRepository.createValue(value);
        return value.getId();
    }

    @Override
    public int createValueIfNotExist(Value value) {
        Value currentValue = valueRepository.getValueByName(value.getValueName());
        if (currentValue == null) {
            valueRepository.createValue(value);
            return value.getId();
        } else {
            return currentValue.getId();
        }
    }

    @Override
    public Value getValueById(int id) {
        return valueRepository.getValueById(id);
    }

    @Override
    public Value getValueByName(String valueName) {
        return valueRepository.getValueByName(valueName);
    }

    @Override
    public void updateValue(Value value) {
        valueRepository.updateValue(value);
    }

    @Override
    public void deleteValue(int id) {
        valueRepository.deleteValueById(id);
    }

    @Override
    public List<Value> queryAllValues() {
        return valueRepository.queryAllValues();
    }

    @Override
    public int createCategoryProperty(CategoryProperty categoryProperty) {
        // 根据类目ID和属性ID查询该类目下是否有该类目属性
        CategoryProperty dbCategoryProperty = this.queryCategoryPropertyByCategoryIdAndPropertyId(categoryProperty.getCategoryId(), categoryProperty.getPropertyId());
        if (dbCategoryProperty != null) {
            throw new RuntimeException("类目属性重复:" + categoryProperty.toString());
        }
        categoryPropertyRepository.createCategoryProperty(categoryProperty);
        return categoryProperty.getId();
    }

    @Override
    public CategoryProperty getCategoryPropertyById(int id) {
        return categoryPropertyRepository.getCategoryPropertyById(id);
    }

    @Override
    public CategoryProperty queryCategoryPropertyByCategoryIdAndPropertyId(int categoryId, int propertyId) {
        return categoryPropertyRepository.queryCategoryPropertyByCategoryIdAndPropertyId(categoryId, propertyId);
    }


    @Override
    public List<CategoryProperty> getCategoryProperties(int categoryId, PropertyType propertyType) {
        List<CategoryProperty> categoryProperties = categoryPropertyRepository.getCategoryProperties(categoryId, propertyType);
        return categoryProperties;
    }

    @Override
    public List<CategoryProperty> queryCategoryPropertyByCategoryId(int categoryId) {
        List<CategoryProperty> categoryProperties = categoryPropertyRepository.queryCategoryPropertyByCategoryId(categoryId);
        return categoryProperties;
    }

    @Override
    public void updateCategoryProperty(CategoryProperty categoryProperty) {
        categoryPropertyRepository.updateCategoryProperty(categoryProperty);
    }

    @Override
    public void deleteCategoryProperty(int id) {
        categoryPropertyRepository.deleteCategoryPropertyById(id);
    }

    @Override
    public List<CategoryProperty> queryAllCategoryProperties() {
        return categoryPropertyRepository.queryAllCategoryProperties();
    }

    /**
     * 添加类目属性值(类目、属性、属性值关系表)
     * @param categoryPropertyValue
     * @return
     */
    @Override
    public int createCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue) {
        CategoryPropertyValue current = this.queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(categoryPropertyValue.getCategoryId(), categoryPropertyValue.getPropertyId(), categoryPropertyValue.getValueId());
        if (current != null) {
            throw new RuntimeException("类目属性值发现重复:" + categoryPropertyValue.toString());
        }
        categoryPropertyValueRepository.createCategoryPropertyValue(categoryPropertyValue);
        return categoryPropertyValue.getId();
    }

    @Override
    public CategoryPropertyValue getCategoryPropertyValueById(int id) {
        return categoryPropertyValueRepository.getCategoryPropertyValueById(id);
    }

    @Override
    public CategoryPropertyValue queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(int categoryId, int propertyId, int valueId) {
        return categoryPropertyValueRepository.queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(categoryId, propertyId, valueId);
    }

    @Override
    public List<CategoryPropertyValue> getCategoryPropertyValues(int categoryId) {
        return categoryPropertyValueRepository.getCategoryPropertyValues(categoryId);
    }

    @Override
    public List<CategoryPropertyValue> getCategoryPropertyValues(int categoryId, int propertyId) {
        return categoryPropertyValueRepository.getCategoryPropertyValues(categoryId, propertyId);
    }

    @Override
    public void updateCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue) {
        categoryPropertyValueRepository.updateCategoryPropertyValue(categoryPropertyValue);
    }

    @Override
    public void deleteCategoryPropertyValue(int id) {
        categoryPropertyValueRepository.deleteCategoryPropertyValueById(id);
    }

    @Override
    public List<CategoryPropertyValue> queryAllCategoryPropertyValues() {
        return categoryPropertyValueRepository.queryAllCategoryPropertyValues();
    }

    @Override
    public int createCategoryPropertyValueDetail(PropertyValueDetail propertyValueDetail) {
        categoryPropertyValueDetailRepository.createPropertyValueDetail(propertyValueDetail);
        return propertyValueDetail.getId();
    }

    @Override
    public PropertyValueDetail getCategoryPropertyValueDetail(int id) {
        return categoryPropertyValueDetailRepository.getPropertyValueDetailById(id);
    }

    @Override
    public PropertyValueDetail getPropertyValueDetail(int propertyId, int valueId) {
        return categoryPropertyValueDetailRepository.getPropertyValueDetail(propertyId, valueId);
    }

    @Override
    public void updateCategoryPropertyValueDetail(PropertyValueDetail propertyValueDetail) {
        categoryPropertyValueDetailRepository.updatePropertyValueDetail(propertyValueDetail);
    }

    @Override
    public void deleteCategoryPropertyValueDetail(int id) {
        categoryPropertyValueDetailRepository.deletePropertyValueDetailById(id);
    }

    @Override
    public List<PropertyValueDetail> queryAllCategoryPropertyValueDetails() {
        return categoryPropertyValueDetailRepository.queryAllPropertyValueDetails();
    }

    @Override
    public void deleteCategoryPropertyByCPId(int categoryId, int propertyId) {
        categoryPropertyRepository.deleteCategoryPropertyByCPId(categoryId, propertyId);
    }

    @Override
    public void deleteCategoryPropertyValueByCPVId(int categoryId, int propertyId, int valueId) {
        categoryPropertyValueRepository.deleteCategoryPropertyValueByCPVId(categoryId, propertyId, valueId);
    }

    @Override
    public void deleteCategoryPropertyValueByCPId(int categoryId, int propertyId) {
        categoryPropertyValueRepository.deleteCategoryPropertyValueByCPId(categoryId, propertyId);
    }

    public void setPropertyRepository(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public void setValueRepository(ValueRepository valueRepository) {
        this.valueRepository = valueRepository;
    }

    public void setCategoryPropertyRepository(CategoryPropertyRepository categoryPropertyRepository) {
        this.categoryPropertyRepository = categoryPropertyRepository;
    }

    public void setCategoryPropertyValueRepository(CategoryPropertyValueRepository categoryPropertyValueRepository) {
        this.categoryPropertyValueRepository = categoryPropertyValueRepository;
    }

    public void setCategoryPropertyValueDetailRepository(PropertyValueDetailRepository categoryPropertyValueDetailRepository) {
        this.categoryPropertyValueDetailRepository = categoryPropertyValueDetailRepository;
    }
}
