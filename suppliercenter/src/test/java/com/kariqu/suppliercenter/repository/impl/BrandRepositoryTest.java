package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.suppliercenter.domain.Brand;
import com.kariqu.suppliercenter.repository.BrandRepository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.assertEquals;


@ContextConfiguration(locations = {"/supplierCenter.xml"})
public class BrandRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private BrandRepository brandRepository;


    @Test
    @Rollback(false)
    public void testBrandRepository() {
        Brand brand = new Brand();
        brand.setName("美亚");
        brand.setCustomerId(2);
        brandRepository.createBrand(brand);

        brand.setName("波美");
        brand.setDesc("abcde");
        brand.setPicture("http://abc.jpg");
        brand.setId(brandRepository.queryBrandByCustomerId(brand.getCustomerId()).get(0).getId());
        brandRepository.updateBrand(brand);
        assertEquals("波美", brandRepository.queryBrandByCustomerId(brand.getCustomerId()).get(0).getName());
        assertEquals("abcde", brandRepository.queryBrandByCustomerId(brand.getCustomerId()).get(0).getDesc());
        assertEquals("http://abc.jpg", brandRepository.queryBrandByCustomerId(brand.getCustomerId()).get(0).getPicture());

        brand.setId(0);
        brandRepository.createBrand(brand);
        assertEquals(2, brandRepository.queryBrandByCustomerId(brand.getCustomerId()).size());
        brandRepository.deleteBrandByCustomerId(brand.getCustomerId());
        assertEquals(0, brandRepository.queryBrandByCustomerId(brand.getCustomerId()).size());

    }


}
