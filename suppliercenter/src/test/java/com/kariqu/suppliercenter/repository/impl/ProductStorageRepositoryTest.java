package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.repository.ProductStorageRepository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


@ContextConfiguration(locations = {"/supplierCenter.xml"})
public class ProductStorageRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private ProductStorageRepository productStorageRepository;


    @Test
    @Rollback(false)
    public void testProductStorageRepository() {
        ProductStorage productStorage = new ProductStorage();
        productStorage.setName("北京仓库");
        productStorage.setCustomerId(1);
        productStorageRepository.createProductStorage(productStorage);
    }


}
