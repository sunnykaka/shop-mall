package com.kariqu.suppliercenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.SupplierLog;
import com.kariqu.suppliercenter.service.SupplierLogQuery;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-22
 * Time: 上午10:37
 */
public interface SupplierLogRepository {

    Page<SupplierLog> querySupplierLogPageBySupplierId(SupplierLogQuery query,Page<SupplierLog> page);

    void deleteSupplierLogById(long id);

    void createSupplierLog(SupplierLog supplierLog);
}
