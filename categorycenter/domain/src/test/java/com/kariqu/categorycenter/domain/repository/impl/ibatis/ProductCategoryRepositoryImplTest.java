package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.ProductCategory;
import com.kariqu.categorycenter.domain.repository.ProductCategoryRepository;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午7:10
 */
public class ProductCategoryRepositoryImplTest extends IbatisBaseTest {

    @SpringBean("productCategoryRepository")
    private ProductCategoryRepository productCategoryRepository;

    @Test
    public void testProductCategoryRepository() {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName("手机");
        productCategory.setDescription("手机叶子类目");
        ProductCategory parent = new ProductCategory();
        parent.setId(2);
        productCategory.setParent(parent);
        productCategoryRepository.create(productCategory);
        assertEquals(1, productCategoryRepository.queryAllProductCategories().size());
        productCategory = productCategoryRepository.getProductCategoryById(productCategory.getId());
        assertEquals("手机", productCategory.getName());
        assertEquals("手机叶子类目", productCategory.getDescription());
        assertEquals(2, productCategory.getParent().getId());
        productCategory.setDescription("xxx");
        productCategory.setName("笔记本");
        parent.setId(3);
        productCategory.setParent(parent);
        productCategoryRepository.updateProductCategory(productCategory);
        productCategory = productCategoryRepository.getProductCategoryById(productCategory.getId());
        assertEquals("笔记本", productCategory.getName());
        assertEquals("xxx", productCategory.getDescription());
        assertEquals(3, productCategory.getParent().getId());
        productCategoryRepository.deleteProductCategoryById(productCategory.getId());
        assertEquals(0, productCategoryRepository.queryAllProductCategories().size());
    }


}
