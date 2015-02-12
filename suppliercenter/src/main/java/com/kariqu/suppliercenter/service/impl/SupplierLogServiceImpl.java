package com.kariqu.suppliercenter.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.SupplierLog;
import com.kariqu.suppliercenter.repository.SupplierLogRepository;
import com.kariqu.suppliercenter.service.SupplierLogQuery;
import com.kariqu.suppliercenter.service.SupplierLogService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-22
 * Time: 上午11:43
 */
public class SupplierLogServiceImpl implements SupplierLogService{
    @Autowired
    private SupplierLogRepository supplierLogRepository;

    @Override
    public Page<SupplierLog> querySupplierLogPageBySupplierId(SupplierLogQuery query, Page<SupplierLog> page) {
        return supplierLogRepository.querySupplierLogPageBySupplierId(query,page);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteSupplierLogById(long id) {
        supplierLogRepository.deleteSupplierLogById(id);
    }

    @Override
    public void createSupplierLog(SupplierLog supplierLog) {
        supplierLogRepository.createSupplierLog(supplierLog);
    }
}
