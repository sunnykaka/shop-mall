package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.repository.NavigateCategoryRepository;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.unitils.spring.annotation.SpringBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 下午1:05
 */
public class NavigateCategoryRepositoryImplTest extends IbatisBaseTest {

    @SpringBean("navigateCategoryRepository")
    private NavigateCategoryRepository navigateCategoryRepository;


    @Test
    @Rollback(true)
    public void testNavigateCategoryRepository() throws InterruptedException {
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(4);
        ids.add(12);
        ids.add(13);
        ids.add(14);
        ids.add(15);
        navigateCategoryRepository.insertNavigateAssociation(3, ids);

        Map hashMap = navigateCategoryRepository.queryAllAssociation();
        assertEquals(1, hashMap.size());
        assertEquals(5, ((List) hashMap.get(3)).size());

        NavigateCategory navigateCategory = new NavigateCategory();
        navigateCategory.setName("ssss");
        navigateCategory.setDescription("ddddddddd");
        navigateCategory.setConditions("111");
        navigateCategory.setKeyWord("key");
        navigateCategory.setSettings("xxx");
        NavigateCategory parent = new NavigateCategory();
        parent.setId(3);
        navigateCategory.setParent(parent);
        navigateCategoryRepository.createNavigateCategory(navigateCategory);

        navigateCategory = navigateCategoryRepository.getNavigateCategoryById(navigateCategory.getId());
        assertEquals(3, navigateCategory.getParent().getId());
        assertEquals("key", navigateCategory.getKeyWord());
        assertEquals("xxx", navigateCategory.getSettings());

        assertEquals(ids.toString(), (navigateCategoryRepository.queryAssociationByNavCategoryId(navigateCategory.getParent().getId()).toString()));

        assertEquals(1, navigateCategoryRepository.queryAllNavCategories().size());
        assertEquals(1, navigateCategoryRepository.querySubCategories(3).size());
        navigateCategory.setName("手机");
        navigateCategory.setDescription("dgewwe");
        parent.setId(4);
        navigateCategory.setParent(parent);
        navigateCategory.setConditions("23433");
        navigateCategoryRepository.updateNavigateCategory(navigateCategory);
        navigateCategoryRepository.deleteNavigateCategoryById(navigateCategory.getId());
        assertEquals(0, navigateCategoryRepository.queryAllNavCategories().size());
    }


    @Test
    public void testRootCategoryQuery() {
        NavigateCategory parent = new NavigateCategory();
        parent.setId(-1);
        NavigateCategory navigateCategory = new NavigateCategory();
        navigateCategory.setPriority(1);
        navigateCategory.setName("ssss");
        navigateCategory.setDescription("ddddddddd");
        navigateCategory.setConditions("111");
        navigateCategory.setKeyWord("key");
        navigateCategory.setParent(parent);
        navigateCategoryRepository.createNavigateCategory(navigateCategory);
        assertEquals(1, navigateCategoryRepository.queryAllRootCategories().size());
    }

}
