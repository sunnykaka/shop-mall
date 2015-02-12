package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.suppliercenter.repository.SupplierRoleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.assertEquals;


@ContextConfiguration(locations = {"/supplierCenter.xml"})
public class SupplierRoleRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private SupplierRoleRepository supplierRoleRepository;

    @Test
    @Rollback(false)
    public void testSupplierRoleRepository() {
        supplierRoleRepository.insertRoleSupplier(12,11);
        assertEquals(1, supplierRoleRepository.queryRoleSupplierBySupplierId(11).size());
        supplierRoleRepository.deleteRoleSupplier(12,11);
        assertEquals(0, supplierRoleRepository.queryRoleSupplierBySupplierId(11).size());
    }
}
