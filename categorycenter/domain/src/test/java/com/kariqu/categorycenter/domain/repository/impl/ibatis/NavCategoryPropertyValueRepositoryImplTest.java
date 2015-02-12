package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.navigate.NavCategoryPropertyValue;
import com.kariqu.categorycenter.domain.repository.NavCategoryPropertyValueRepository;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 下午2:17
 */
public class NavCategoryPropertyValueRepositoryImplTest extends IbatisBaseTest {

    @SpringBean("navCategoryPropertyValueRepository")
    private NavCategoryPropertyValueRepository navCategoryPropertyValueRepository;

    @Test
    public void testNavCategoryPropertyValueRepository() throws InterruptedException {
        NavCategoryPropertyValue navCategoryPropertyValue = new NavCategoryPropertyValue();
        navCategoryPropertyValue.setNavCategoryId(2);
        navCategoryPropertyValue.setPropertyId(3);
        navCategoryPropertyValue.setValueId(4);
        navCategoryPropertyValue.setPriority(3);
        navCategoryPropertyValueRepository.createNavCategoryPropertyValue(navCategoryPropertyValue);
        assertEquals(2,navCategoryPropertyValueRepository.getNavCategoryPropertyValueById(navCategoryPropertyValue.getId()).getNavCategoryId());
        assertEquals(1, navCategoryPropertyValueRepository.queryAllNavCategoryPropertyValues().size());
        assertEquals(1, navCategoryPropertyValueRepository.queryNavCategoryPropertyValues(2, 3).size());
        navCategoryPropertyValue.setNavCategoryId(5);
        navCategoryPropertyValue.setPropertyId(6);
        navCategoryPropertyValue.setValueId(7);
        navCategoryPropertyValueRepository.updateNavCategoryPropertyValue(navCategoryPropertyValue);
        navCategoryPropertyValueRepository.deleteNavCategoryPropertyValueById(navCategoryPropertyValue.getId());
        assertEquals(0, navCategoryPropertyValueRepository.queryAllNavCategoryPropertyValues().size());
    }


}
