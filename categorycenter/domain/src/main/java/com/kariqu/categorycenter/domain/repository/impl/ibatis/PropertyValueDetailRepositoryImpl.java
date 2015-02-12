package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.PropertyValueDetail;
import com.kariqu.categorycenter.domain.repository.PropertyValueDetailRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午3:19
 */
public class PropertyValueDetailRepositoryImpl extends SqlMapClientDaoSupport implements PropertyValueDetailRepository {

    @Override
    public void createPropertyValueDetail(PropertyValueDetail propertyValueDetail) {
        getSqlMapClientTemplate().insert("insertCategoryPropertyValueDetail", propertyValueDetail);
    }

    @Override
    public PropertyValueDetail getPropertyValueDetailById(int id) {
        return (PropertyValueDetail) getSqlMapClientTemplate().queryForObject("selectCategoryPropertyValueDetail", id);
    }

    @Override
    public PropertyValueDetail getPropertyValueDetail(int propertyId, int valueId) {
        Map map = new HashMap();
        map.put("propertyId", propertyId);
        map.put("valueId", valueId);
        return (PropertyValueDetail) getSqlMapClientTemplate().queryForObject("getPropertyValueDetail", map);
    }

    @Override
    public void updatePropertyValueDetail(PropertyValueDetail propertyValueDetail) {
        getSqlMapClientTemplate().update("updateCategoryPropertyValueDetail", propertyValueDetail);
    }

    @Override
    public void deletePropertyValueDetailById(int id) {
        getSqlMapClientTemplate().delete("deleteCategoryPropertyValueDetail", id);
    }

    @Override
    public void deleteAllPropertyValueDetail() {
        getSqlMapClientTemplate().delete("deleteAllCategoryPropertyValueDetail");
    }

    @Override
    public List<PropertyValueDetail> queryAllPropertyValueDetails() {
        return getSqlMapClientTemplate().queryForList("selectAllCategoryPropertyValueDetails");
    }

}
