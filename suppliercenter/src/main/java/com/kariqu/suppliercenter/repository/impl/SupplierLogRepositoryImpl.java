package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.SupplierLog;
import com.kariqu.suppliercenter.repository.SupplierLogRepository;
import com.kariqu.suppliercenter.service.SupplierLogQuery;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-22
 * Time: 上午10:36
 */
public class SupplierLogRepositoryImpl  extends SqlMapClientDaoSupport implements SupplierLogRepository{
    @Override
    public Page<SupplierLog> querySupplierLogPageBySupplierId(SupplierLogQuery query,Page<SupplierLog> page) {
        Map param = new HashMap();
        param.put("content",query.getContent());
        param.put("supplierId", query.getSupplierId());
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<SupplierLog> supplierLogListPage = getSqlMapClientTemplate().queryForList("querySupplierLogPageBySupplierId", param);
        page.setResult(supplierLogListPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForSupplierLog",param));
        return page;
    }

    @Override
    public void deleteSupplierLogById(long id) {
        getSqlMapClientTemplate().delete("deleteSupplierLogById",id);
    }

    @Override
    public void createSupplierLog(SupplierLog supplierLog) {
        getSqlMapClientTemplate().insert("insertSupplierLog", supplierLog);
    }
}
