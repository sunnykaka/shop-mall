package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.CategoryPropertyValue;
import com.kariqu.categorycenter.domain.repository.CategoryPropertyValueRepository;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午7:10
 */
public class CategoryPropertyValueRepositoryImplTest extends IbatisBaseTest {

    @SpringBean("categoryPropertyValueRepository")
    private CategoryPropertyValueRepository categoryPropertyValueRepository;


    @Test
    public void testCategoryPropertyValue() throws InterruptedException {
        CategoryPropertyValue categoryPropertyValue = new CategoryPropertyValue();
        categoryPropertyValue.setCategoryId(2);
        categoryPropertyValue.setPropertyId(4);
        categoryPropertyValue.setValueId(5);
        categoryPropertyValue.setPriority(1);
        categoryPropertyValue.setId(1);
        categoryPropertyValueRepository.createCategoryPropertyValue(categoryPropertyValue);
        categoryPropertyValue = categoryPropertyValueRepository.getCategoryPropertyValueById(categoryPropertyValue.getId());
        assertEquals(2, categoryPropertyValue.getCategoryId());
        assertEquals(4, categoryPropertyValue.getPropertyId());
        assertEquals(5, categoryPropertyValue.getValueId());

        assertEquals(1, categoryPropertyValueRepository.queryAllCategoryPropertyValues().size());
        assertEquals(1, categoryPropertyValueRepository.getCategoryPropertyValues(2, 4).size());
        assertEquals(2, categoryPropertyValueRepository.queryCategoryPropertyValueByCategoryIdAndPropertyIdAndValueId(2, 4, 5).getCategoryId());


        categoryPropertyValue.setCategoryId(1);
        categoryPropertyValue.setPropertyId(1);
        categoryPropertyValue.setValueId(1);
        categoryPropertyValueRepository.updateCategoryPropertyValue(categoryPropertyValue);
        categoryPropertyValue = categoryPropertyValueRepository.getCategoryPropertyValueById(categoryPropertyValue.getId());
        categoryPropertyValueRepository.deleteCategoryPropertyValueById(categoryPropertyValue.getId());
        assertEquals(0, categoryPropertyValueRepository.queryAllCategoryPropertyValues().size());
    }

}
