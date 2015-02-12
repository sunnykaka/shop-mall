package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.Property;
import com.kariqu.categorycenter.domain.repository.PropertyRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.List;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午3:18
 */
public class PropertyRepositoryImpl extends SqlMapClientDaoSupport implements PropertyRepository {
    @Override
    public void createProperty(Property property) {
        getSqlMapClientTemplate().insert("insertProperty", property);
    }

    @Override
    public Property getPropertyById(int id) {
        return (Property) getSqlMapClientTemplate().queryForObject("selectProperty", id);
    }

    @Override
    public Property getPropertyByName(String name) {
        return (Property) getSqlMapClientTemplate().queryForObject("getPropertyByName", name);
    }

    @Override
    public void updateProperty(Property property) {
        getSqlMapClientTemplate().update("updateProperty", property);
    }

    @Override
    public void deletePropertyById(int id) {
        getSqlMapClientTemplate().delete("deleteProperty", id);
    }

    @Override
    public void deleteAllProperty() {
        getSqlMapClientTemplate().delete("deleteAllProperty", new Date());
    }

    @Override
    public List<Property> queryAllProperties() {
        return getSqlMapClientTemplate().queryForList("selectAllProperty");
    }

}
