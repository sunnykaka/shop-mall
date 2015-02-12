package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.CategoryPropertyValue;
import com.kariqu.categorycenter.domain.repository.CategoryPropertyValueRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午3:19
 */
public class CategoryPropertyValueRepositoryImpl extends SqlMapClientDaoSupport implements CategoryPropertyValueRepository {
    @Override
    public void deleteCategoryPropertyValueByCPVId(int categoryId, int propertyId, int valueId) {
        Map map = new HashMap();
        map.put("categoryId", categoryId);
        map.put("propertyId", propertyId);
        map.put("valueId", valueId);
        this.getSqlMapClientTemplate().delete("deleteCategoryPropertyValueByCPVId", map);
    }

    @Override
    public void deleteCategoryPropertyValueByCPId(int categoryId, int propertyId) {
        Map map = new HashMap();
        map.put("categoryId", categoryId);
        map.put("propertyId", propertyId);
        this.getSqlMapClientTemplate().delete("deleteCategoryPropertyValueByCPId", map);
    }

    @Override
    public void deleteCategoryPropertyValueByCategoryId(int categoryId) {
        this.getSqlMapClientTemplate().delete("deleteCategoryPropertyValueByCategoryId", categoryId);
    }

    @Override
    public List<CategoryPropertyValue> getCategoryPropertyValues(int categoryId, int propertyId) {
        Map map = new HashMap();
        map.put("categoryId", categoryId);
        map.put("propertyId", propertyId);
        return getSqlMapClientTemplate().queryForList("getCategoryPropertyValues", map);
    }

    @Override
    public CategoryPropertyValue queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(int categoryId, int propertyId, int valueId) {
        Map map = new HashMap();
        map.put("categoryId", categoryId);
        map.put("propertyId", propertyId);
        map.put("valueId", valueId);
        return (CategoryPropertyValue) getSqlMapClientTemplate().queryForObject("queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId", map);
    }

    @Override
    public List<CategoryPropertyValue> getCategoryPropertyValues(int categoryId) {
        return getSqlMapClientTemplate().queryForList("getCategoryPropertyValuesByCategoryId", categoryId);
    }

    @Override
    public void createCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue) {
        getSqlMapClientTemplate().insert("insertCategoryPropertyValue", categoryPropertyValue);
    }

    @Override
    public CategoryPropertyValue getCategoryPropertyValueById(int id) {
        return (CategoryPropertyValue) getSqlMapClientTemplate().queryForObject("selectCategoryPropertyValue", id);
    }

    @Override
    public void updateCategoryPropertyValue(CategoryPropertyValue categoryPropertyValue) {
        getSqlMapClientTemplate().update("updateCategoryPropertyValue", categoryPropertyValue);
    }

    @Override
    public void deleteCategoryPropertyValueById(int id) {
        getSqlMapClientTemplate().delete("deleteCategoryPropertyValue", id);
    }

    @Override
    public void deleteAllCategoryPropertyValue() {
        getSqlMapClientTemplate().delete("deleteAllCategoryPropertyValue", new Date());
    }

    @Override
    public List<CategoryPropertyValue> queryAllCategoryPropertyValues() {
        return getSqlMapClientTemplate().queryForList("selectAllCategoryPropertyValues");
    }


}
