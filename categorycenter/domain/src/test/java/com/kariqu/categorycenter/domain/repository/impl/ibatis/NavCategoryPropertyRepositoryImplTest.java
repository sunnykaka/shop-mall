package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.repository.NavCategoryPropertyRepository;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * User: Asion
 * Date: 11-7-5
 * Time: 下午2:09
 */
public class NavCategoryPropertyRepositoryImplTest extends IbatisBaseTest {

    @SpringBean("navCategoryPropertyRepository")
    private NavCategoryPropertyRepository navCategoryPropertyRepository;

    @Test
    public void testNavCategoryPropertyRepository() throws InterruptedException {
        NavCategoryProperty navCategoryProperty = new NavCategoryProperty();
        navCategoryProperty.setNavCategoryId(1);
        navCategoryProperty.setPropertyId(4);
        navCategoryProperty.setPriority(2);
        navCategoryPropertyRepository.createNavCategoryProperty(navCategoryProperty);
        assertEquals(1, navCategoryPropertyRepository.queryAllNavCategoryProperties().size());
        navCategoryProperty.setNavCategoryId(4);
        navCategoryProperty.setPropertyId(5);
        navCategoryProperty.setPriority(6);
        navCategoryPropertyRepository.updateNavCategoryProperty(navCategoryProperty);
        navCategoryPropertyRepository.deleteNavCategoryPropertyById(navCategoryProperty.getId());
        assertEquals(0, navCategoryPropertyRepository.queryAllNavCategoryProperties().size());
    }

    @Test
    @Rollback(false)
    public void testNavCategoryPropertyRepositorySearchable() throws InterruptedException {
        NavCategoryProperty navCategoryProperty = new NavCategoryProperty();
        navCategoryProperty.setNavCategoryId(1);
        navCategoryProperty.setPropertyId(4);
        navCategoryProperty.setPriority(2);

        navCategoryPropertyRepository.createNavCategoryProperty(navCategoryProperty);
        assertEquals(1, navCategoryPropertyRepository.queryAllNavCategoryProperties().size());
        assertEquals(false, navCategoryPropertyRepository.queryAllNavCategoryProperties().get(0).isSearchable());

        navCategoryPropertyRepository.makeNavigateCategoryPropertySearchable(navCategoryProperty.getNavCategoryId(),navCategoryProperty.getPropertyId());
        assertEquals(true, navCategoryPropertyRepository.queryAllNavCategoryProperties().get(0).isSearchable());
    }

    @Test
    @Rollback(false)
    public void testNavCategorySearchableProperty() throws InterruptedException {
        NavCategoryProperty navCategoryProperty = new NavCategoryProperty();
        navCategoryProperty.setNavCategoryId(1);
        navCategoryProperty.setPropertyId(1);
        navCategoryProperty.setPriority(1);

        NavCategoryProperty navCategoryProperty2 = new NavCategoryProperty();
        navCategoryProperty2.setNavCategoryId(1);
        navCategoryProperty2.setPropertyId(2);
        navCategoryProperty2.setPriority(2);

        NavCategoryProperty navCategoryProperty3 = new NavCategoryProperty();
        navCategoryProperty3.setNavCategoryId(1);
        navCategoryProperty3.setPropertyId(3);
        navCategoryProperty3.setPriority(3);

        navCategoryPropertyRepository.createNavCategoryProperty(navCategoryProperty);

        assertEquals(1, navCategoryPropertyRepository.queryAllNavCategoryProperties().size());

        navCategoryPropertyRepository.makeNavigateCategoryPropertySearchable(1, navCategoryProperty.getPropertyId());
        navCategoryPropertyRepository.makeNavigateCategoryPropertySearchable(1, navCategoryProperty2.getPropertyId());

        List<NavCategoryProperty> list = navCategoryPropertyRepository.queryNavCategoryPropertiesSearchable(1);

        assertEquals(1, list.size());
    }

}
