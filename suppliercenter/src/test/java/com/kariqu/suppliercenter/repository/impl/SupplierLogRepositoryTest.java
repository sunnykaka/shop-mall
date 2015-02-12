package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.suppliercenter.domain.SupplierLog;
import com.kariqu.suppliercenter.repository.SupplierLogRepository;
import com.kariqu.suppliercenter.service.SupplierLogQuery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.assertEquals;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-22
 * Time: 下午1:57
 */
@ContextConfiguration(locations = {"/supplierCenter.xml"})
public class SupplierLogRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private SupplierLogRepository supplierLogRepository;

    @Test
    @Rollback(false)
    public void supplierLogTest(){
        SupplierLog supplierLog=new SupplierLog();
        supplierLog.setContent("测试");
        supplierLog.setTitle("测试");
        supplierLog.setIp("192.168.1.1");
        supplierLog.setSupplierId(1);
        supplierLog.setOperator("wendy");
        for(int i=0;i<5;i++){
            supplierLogRepository.createSupplierLog(supplierLog);
        }

        Page<SupplierLog> supplierLogPage=new Page<SupplierLog>();
        supplierLogPage.setPageNo(1);
        supplierLogPage.setPageSize(10);
        SupplierLogQuery query=new SupplierLogQuery();
        query.setSupplierId(1);
        assertEquals(5, supplierLogRepository.querySupplierLogPageBySupplierId(query, supplierLogPage).getResult().size());
    }
}
