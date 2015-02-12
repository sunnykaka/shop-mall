package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;
import com.kariqu.categorycenter.domain.repository.NavCategoryPropertyValueRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 上午10:14
 */
public class NavCategoryPropertyValueRepositoryImpl extends SqlMapClientDaoSupport implements NavCategoryPropertyValueRepository {
    @Override
    public List<NavCategoryPropertyValue> queryNavCategoryPropertyValues(int navCategoryId, int propertyId) {
        Map map = new HashMap();
        map.put("navCategoryId", navCategoryId);
        map.put("propertyId", propertyId);
        return getSqlMapClientTemplate().queryForList("queryNavCategoryPropertyValues", map);
    }

    @Override
    public void createNavCategoryPropertyValue(NavCategoryPropertyValue navCategoryPropertyValue) {
        getSqlMapClientTemplate().insert("insertNavCategoryPropertyValue", navCategoryPropertyValue);
    }

    @Override
    public NavCategoryPropertyValue getNavCategoryPropertyValueById(int id) {
        return (NavCategoryPropertyValue) getSqlMapClientTemplate().queryForObject("selectNavCategoryPropertyValue",id);
    }

    @Override
    public void updateNavCategoryPropertyValue(NavCategoryPropertyValue navCategoryPropertyValue) {
        getSqlMapClientTemplate().update("updateNavCategoryPropertyValue", navCategoryPropertyValue);
    }

    @Override
    public void deleteNavCategoryPropertyValueById(int id) {
        getSqlMapClientTemplate().delete("deleteNavCategoryPropertyValue", id);
    }

    @Override
    public void deleteNavCategoryPropertyValueByNavCategoryId(int navCategoryId) {
        getSqlMapClientTemplate().delete("deleteNavCategoryPropertyValueByNavCategoryId", navCategoryId);
    }

    @Override
    public void deleteAllNavCategoryPropertyValue() {
        getSqlMapClientTemplate().delete("deleteAllNavCategoryPropertyValue");
    }

    @Override
    public List<NavCategoryPropertyValue> queryAllNavCategoryPropertyValues() {
        return getSqlMapClientTemplate().queryForList("selectAllNavCategoryPropertyValues");
    }


}
