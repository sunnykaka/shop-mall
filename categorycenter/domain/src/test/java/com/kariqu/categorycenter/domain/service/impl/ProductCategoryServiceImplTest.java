package com.kariqu.categorycenter.domain.service.impl;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.service.ProductCategoryService;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-6-26
 * Time: 下午3:05
 */
@SpringApplicationContext({"classpath:categoryCenter.xml"})
public class ProductCategoryServiceImplTest extends UnitilsJUnit4 {

    @SpringBean("productCategoryService")
    private ProductCategoryService productCategoryService;

    @Test
    public void testQuerySubCategories() throws Exception {
        ProductCategory parent = new ProductCategory();
        parent.setId(3);

        ProductCategory productCategory1 = new ProductCategory();
        productCategory1.setParent(parent);
        productCategory1.setName("手机");


        ProductCategory productCategory2 = new ProductCategory();
        productCategory2.setParent(parent);
        productCategory2.setName("笔记本");

        productCategoryService.createProductCategory(productCategory1);
        productCategoryService.createProductCategory(productCategory2);

        assertEquals(2, productCategoryService.querySubCategories(3).size());

        System.out.println(productCategoryService.querySubCategories(4).size());
    }

    @Test
    public void testGetParentCategories() throws Exception {
        ProductCategory productCategory1 = new ProductCategory();
        productCategory1.setName("手机");

        ProductCategory productCategory2 = new ProductCategory();
        productCategory2.setName("数码产品");

        ProductCategory productCategory3 = new ProductCategory();
        ProductCategory parent = new ProductCategory();
        parent.setId(-1);
        productCategory3.setParent(parent);
        productCategory3.setName("大家电");

        productCategoryService.createProductCategory(productCategory3);
        productCategory2.setParent(productCategory3);
        productCategoryService.createProductCategory(productCategory2);

        productCategory1.setParent(productCategory2);
        productCategoryService.createProductCategory(productCategory1);


        assertEquals(0,productCategoryService.getParentCategories(productCategory3.getId(),false).size());
        assertEquals(0,productCategoryService.getParentCategories(productCategory3.getId(),true).size());
        assertEquals(1,productCategoryService.getParentCategories(productCategory1.getId(),false).size());
        assertEquals(2,productCategoryService.getParentCategories(productCategory1.getId(),true).size());
        List<ProductCategory> parentCategories = productCategoryService.getParentCategories(productCategory1.getId(), true);


        for (ProductCategory parentCategory : parentCategories) {
            System.out.println(parentCategory.getName());
        }
    }


    @Test
    public void testAddTheSameProductCategory() throws Exception{
        ProductCategory parent = new ProductCategory();
        parent.setId(200);

        ProductCategory childProductCategory1 = new ProductCategory();
        childProductCategory1.setName("AAAA");
        childProductCategory1.setParent(parent);

        ProductCategory childProductCategory2 = new ProductCategory();
        childProductCategory2.setName("AAAA");
        childProductCategory2.setParent(parent);

        productCategoryService.createProductCategory(childProductCategory1);
        productCategoryService.createProductCategory(childProductCategory2);

        assertEquals(2, productCategoryService.queryProductCategoryByNameAndParentId(childProductCategory1.getName(), 200));
    }


}
