package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.CategoryProperty;
import com.kariqu.categorycenter.domain.model.PropertyType;
import com.kariqu.categorycenter.domain.repository.CategoryPropertyRepository;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午7:10
 */
public class CategoryPropertyRepositoryImplTest extends IbatisBaseTest {

    @SpringBean("categoryPropertyRepository")
    private CategoryPropertyRepository categoryPropertyRepository;

    @Test
    public void testCategoryPropertyRepository() throws InterruptedException {
        CategoryProperty categoryProperty = new CategoryProperty();
        categoryProperty.setId(1);
        categoryProperty.setCategoryId(2);
        categoryProperty.setPropertyId(3);
        categoryProperty.setMultiValue(true);
        categoryProperty.setPriority(3);
        categoryProperty.setCompareable(true);
        categoryProperty.setPropertyType(PropertyType.SELL_PROPERTY);
        categoryPropertyRepository.createCategoryProperty(categoryProperty);
        assertEquals(1, categoryPropertyRepository.queryAllCategoryProperties().size());
        assertEquals(true, categoryPropertyRepository.getCategoryPropertyById(categoryProperty.getId()).isMultiValue());
        assertEquals(true, categoryPropertyRepository.getCategoryPropertyById(categoryProperty.getId()).isCompareable());
        assertEquals(true, categoryPropertyRepository.queryCategoryPropertyByCategoryIdAndPropertyId(2, 3).isMultiValue());
        assertEquals(2, categoryPropertyRepository.getCategoryPropertyById(categoryProperty.getId()).getCategoryId());
        assertEquals(3, categoryPropertyRepository.getCategoryPropertyById(categoryProperty.getId()).getPropertyId());
        assertEquals(PropertyType.SELL_PROPERTY, categoryPropertyRepository.getCategoryPropertyById(categoryProperty.getId()).getPropertyType());

        categoryProperty.setCategoryId(5);
        categoryProperty.setPropertyId(1);
        categoryProperty.setMultiValue(false);
        categoryProperty.setPropertyType(PropertyType.KEY_PROPERTY);
        categoryPropertyRepository.updateCategoryProperty(categoryProperty);
        assertEquals(5, categoryPropertyRepository.getCategoryPropertyById(categoryProperty.getId()).getCategoryId());
        assertEquals(1, categoryPropertyRepository.getCategoryPropertyById(categoryProperty.getId()).getPropertyId());
        assertEquals(false, categoryPropertyRepository.getCategoryPropertyById(categoryProperty.getId()).isMultiValue());
        assertEquals(PropertyType.KEY_PROPERTY, categoryPropertyRepository.getCategoryPropertyById(categoryProperty.getId()).getPropertyType());

        assertEquals(1, categoryPropertyRepository.getCategoryProperties(5, PropertyType.KEY_PROPERTY).size());

        categoryPropertyRepository.deleteCategoryPropertyById(categoryProperty.getId());
        assertEquals(0, categoryPropertyRepository.queryAllCategoryProperties().size());
    }

}
