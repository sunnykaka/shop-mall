package com.kariqu.suppliercenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.SupplierLog;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-22
 * Time: 上午11:42
 */
public interface SupplierLogService {
    Page<SupplierLog> querySupplierLogPageBySupplierId(SupplierLogQuery query,Page<SupplierLog> page);

    void deleteSupplierLogById(long id);

    void createSupplierLog(SupplierLog supplierLog);
}
