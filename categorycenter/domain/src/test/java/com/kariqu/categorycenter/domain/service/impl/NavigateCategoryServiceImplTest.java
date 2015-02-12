package com.kariqu.categorycenter.domain.service.impl;

import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.NavigateCategoryService;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 下午2:46
 */
@SpringApplicationContext({"classpath:categoryCenter.xml"})
public class NavigateCategoryServiceImplTest extends UnitilsJUnit4 {

    @SpringBean("navigateCategoryService")
    private NavigateCategoryService navigateCategoryService;

    @Test
    public void testQuerySubCategories() {
        NavigateCategory parent = new NavigateCategory();
        parent.setId(1);
        NavigateCategory navigateCategory = new NavigateCategory();
        navigateCategory.setParent(parent);
        navigateCategory.setName("手机");
        navigateCategory.setConditions("1");
        navigateCategoryService.createNavigateCategory(navigateCategory);

        NavigateCategory navigateCategory1 = new NavigateCategory();
        navigateCategory1.setParent(parent);
        navigateCategory1.setName("摄像机");
        navigateCategory1.setConditions("2");
        navigateCategoryService.createNavigateCategory(navigateCategory1);

        assertEquals(2, navigateCategoryService.querySubCategories(1).size());

    }



    @Test
    public void testGetParentCategories() {

        NavigateCategory virtual = new NavigateCategory();
        virtual.setId(-1);

        NavigateCategory parent1 = new NavigateCategory();
        parent1.setName("电子");
        parent1.setParent(virtual);
        navigateCategoryService.createNavigateCategory(parent1);

        NavigateCategory parent2 = new NavigateCategory();
        parent2.setName("数码产品");
        parent2.setParent(parent1);
        navigateCategoryService.createNavigateCategory(parent2);

        NavigateCategory navigateCategory = new NavigateCategory();
        navigateCategory.setParent(parent2);
        navigateCategory.setName("手机");
        navigateCategory.setConditions("1");
        navigateCategoryService.createNavigateCategory(navigateCategory);

        NavigateCategory navigateCategory1 = new NavigateCategory();
        navigateCategory1.setParent(parent2);
        navigateCategory1.setName("摄像机");
        navigateCategory1.setConditions("2");
        navigateCategoryService.createNavigateCategory(navigateCategory1);

        List<NavigateCategory> parentCategories = navigateCategoryService.getParentCategories(parent1.getId(), true);
        for (NavigateCategory parentCategory : parentCategories) {
            System.out.println(parentCategory.getName());
        }

    }


    @Test
    public void testAddNavigateCategory(){
        NavigateCategory navigateCategory = new NavigateCategory();
        navigateCategory.setId(300);

        NavigateCategory navigateCategory1 = new NavigateCategory();
        navigateCategory1.setName("1234");
        navigateCategory1.setParent(navigateCategory);

        NavigateCategory navigateCategory2 = new NavigateCategory();
        navigateCategory2.setName("1234");
        navigateCategory2.setParent(navigateCategory);

        navigateCategoryService.createNavigateCategory(navigateCategory1);
        navigateCategoryService.createNavigateCategory(navigateCategory2);

        int total = navigateCategoryService.queryNavigateCategoryByNameAndParentId(navigateCategory1.getName(), navigateCategory1.getParent().getId());

        System.out.println(total);
    }

}
