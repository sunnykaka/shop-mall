package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.CategoryProperty;
import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.categorycenter.domain.repository.CategoryPropertyRepository;
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
public class CategoryPropertyRepositoryImpl extends SqlMapClientDaoSupport implements CategoryPropertyRepository {
    @Override
    public void deleteCategoryPropertyByCPId(int categoryId, int propertyId) {
        Map map = new HashMap();
        map.put("categoryId", categoryId);
        map.put("propertyId", propertyId);
        getSqlMapClientTemplate().delete("deleteCategoryPropertyByCPId", map);

    }

    @Override
    public List<CategoryProperty> getCategoryProperties(int categoryId, PropertyType propertyType) {
        Map map = new HashMap();
        map.put("categoryId", categoryId);
        map.put("propertyType", propertyType);
        return getSqlMapClientTemplate().queryForList("getCategoryProperties", map);
    }

    @Override
    public List<CategoryProperty> queryCategoryPropertyByCategoryId(int categoryId) {
        return getSqlMapClientTemplate().queryForList("queryCategoryPropertyByCategoryId", categoryId);
    }

    @Override
    public CategoryProperty queryCategoryPropertyByCategoryIdAndPropertyId(int categoryId, int propertyId) {
        Map map = new HashMap();
        map.put("categoryId", categoryId);
        map.put("propertyId", propertyId);
        return (CategoryProperty) getSqlMapClientTemplate().queryForObject("queryCategoryPropertyByCategoryIdAndPropertyId", map);
    }

    @Override
    public void createCategoryProperty(CategoryProperty categoryProperty) {
        getSqlMapClientTemplate().insert("insertCategoryProperty", categoryProperty);
    }

    @Override
    public CategoryProperty getCategoryPropertyById(int id) {
        return (CategoryProperty) getSqlMapClientTemplate().queryForObject("selectCategoryProperty", id);
    }

    @Override
    public void updateCategoryProperty(CategoryProperty categoryProperty) {
        getSqlMapClientTemplate().update("updateCategoryProperty", categoryProperty);
    }

    @Override
    public void deleteCategoryPropertyById(int id) {
        getSqlMapClientTemplate().delete("deleteCategoryProperty", id);
    }

    @Override
    public void deleteCategoryPropertyByCategoryId(int categoryId) {
        getSqlMapClientTemplate().delete("deleteCategoryPropertyByCategoryId", categoryId);
    }

    @Override
    public void deleteAllCategoryProperty() {
        getSqlMapClientTemplate().delete("deleteAllCategoryProperty", new Date());
    }

    @Override
    public List<CategoryProperty> queryAllCategoryProperties() {
        return getSqlMapClientTemplate().queryForList("selectAllCategoryProperty");
    }


}
