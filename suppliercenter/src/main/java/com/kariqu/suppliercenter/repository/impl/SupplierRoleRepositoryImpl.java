package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.suppliercenter.repository.SupplierRoleRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SupplierRoleRepositoryImpl extends SqlMapClientDaoSupport implements SupplierRoleRepository {

    @Override
    public List<Integer> queryRoleSupplierBySupplierId(int supplierId) {
        return getSqlMapClientTemplate().queryForList("queryRoleSupplierBySupplierId", supplierId);
    }

    @Override
    public void deleteRoleSupplier(int roleId,int supplierId) {
        Map map=new HashMap();
        map.put("roleId",roleId);
        map.put("supplierId", supplierId);
        getSqlMapClientTemplate().delete("deleteRoleSupplier", map);
    }

    @Override
    public void insertRoleSupplier(int roleId,int supplierId) {
        Map map=new HashMap();
        map.put("roleId",roleId);
        map.put("supplierId", supplierId);
        getSqlMapClientTemplate().insert("insertRoleSupplier", map);
    }
}
