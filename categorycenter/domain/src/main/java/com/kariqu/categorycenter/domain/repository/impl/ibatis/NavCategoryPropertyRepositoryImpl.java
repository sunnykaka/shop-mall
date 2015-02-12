package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.repository.NavCategoryPropertyRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 上午10:14
 */
public class NavCategoryPropertyRepositoryImpl extends SqlMapClientDaoSupport implements NavCategoryPropertyRepository {

    @Override
    public void createNavCategoryProperty(NavCategoryProperty navCategoryProperty) {
        getSqlMapClientTemplate().insert("insertNavCategoryProperty", navCategoryProperty);
    }

    @Override
    public NavCategoryProperty getNavCategoryPropertyById(int id) {
        return (NavCategoryProperty) getSqlMapClientTemplate().queryForObject("selectNavCategoryProperty", id);
    }

    @Override
    public void updateNavCategoryProperty(NavCategoryProperty navCategoryProperty) {
        getSqlMapClientTemplate().update("updateNavCategoryProperty", navCategoryProperty);
    }

    @Override
    public void deleteNavCategoryPropertyById(int id) {
        getSqlMapClientTemplate().delete("deleteNavCategoryProperty", id);
    }

    @Override
    public void deleteAllNavCategoryProperty() {
        getSqlMapClientTemplate().delete("deleteAllNavCategoryProperty");
    }

    @Override
    public List<NavCategoryProperty> queryNavCategoryProperties(int navCategoryId) {
        return getSqlMapClientTemplate().queryForList("queryNavCategoryProperties", navCategoryId);
    }

    @Override
    public List<NavCategoryProperty> queryNavCategoryPropertiesSearchable(int navCategoryId) {
        return getSqlMapClientTemplate().queryForList("queryNavCategoryPropertiesSearchable", navCategoryId);
    }

    @Override
    public void deleteNavCategoryPropertyByNavCategoryId(int navCategoryId) {
        getSqlMapClientTemplate().delete("deleteNavCategoryPropertyByNavCategoryId", navCategoryId);
    }

    @Override
    public void makeNavigateCategoryPropertySearchable(int navCategoryId, int propertyId) {
        Map map = new HashMap();
        map.put("navCategoryId", navCategoryId);
        map.put("propertyId", propertyId);
        map.put("searchable", true);
        getSqlMapClientTemplate().update("updateNavigateCategoryPropertySearchable", map);
    }

    @Override
    public void makeAllNavigateCategoryPropertyUnSearchable(int categoryId) {
        getSqlMapClientTemplate().update("updatePropertiesSearchable", categoryId);
    }

    @Override
    public List<NavCategoryProperty> queryAllNavCategoryProperties() {
        return getSqlMapClientTemplate().queryForList("selectAllNavCategoryProperty");
    }


}
