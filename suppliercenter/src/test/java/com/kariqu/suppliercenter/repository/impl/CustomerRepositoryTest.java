package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliercenter.repository.SupplierRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.assertEquals;


@ContextConfiguration(locations = {"/supplierCenter.xml"})
public class CustomerRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    @Rollback(false)
    public void testCustomerRepository() {
        Supplier customer = new Supplier();
        customer.setName("美亚");
        customer.setDefaultLogistics(DeliveryInfo.DeliveryType.ems);
        supplierRepository.createSupplier(customer);
        assertEquals("美亚", supplierRepository.querySupplierById(customer.getId()).getName());
        assertEquals(DeliveryInfo.DeliveryType.ems, supplierRepository.querySupplierById(customer.getId()).getDefaultLogistics());
    }

    @Test
    @Rollback(false)
    public void testCustomerAccountRepository() {
        SupplierAccount supplierAccount = new SupplierAccount();
        supplierAccount.setAccountName("测试");
        supplierAccount.setCustomerId(1);
        supplierAccount.setEmail("888@qq.com");
        supplierAccount.setMainAccount(true);
        supplierAccount.setNormal(true);
        supplierAccount.setPassword("666666");
        supplierRepository.insertSupplierAccount(supplierAccount);
        assertEquals(true, supplierRepository.querySupplierAccountByName(supplierAccount.getAccountName(), supplierAccount.getCustomerId()).isMainAccount());
    }

}
