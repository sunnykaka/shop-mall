package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.MealItem;
import com.kariqu.productcenter.domain.MealSet;
import com.kariqu.productcenter.repository.MealSetRepository;
import com.kariqu.productcenter.service.impl.MealSetServiceImpl;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * User: Asion
 * Date: 13-5-4
 * Time: 下午2:10
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class MealSetRepositoryTest extends UnitilsJUnit4 {

    @SpringBean("mealSetRepository")
    private MealSetRepository mealSetRepository;

    @Test
    public void testMealSetRepository() {
        MealSet mealSet = new MealSet();
        mealSet.setName("xxx");
        mealSet.setPrice("xxxddddd");
        mealSetRepository.addMealSet(mealSet);

        MealItem mealItem = new MealItem();
        mealItem.setProductId(1);
        mealItem.setSkuId(2);
        mealItem.setMealSetId(mealSet.getId());
        mealItem.setNumber(1);
        mealItem.setSkuPrice(3);
        mealSetRepository.addMealItem(mealItem);
        mealItem.setSkuId(3);
        mealSetRepository.addMealItem(mealItem);


        MealSetServiceImpl mealSetService = new MealSetServiceImpl();
        mealSetService.setMealSetRepository(mealSetRepository);

        assertEquals(2,mealSetRepository.queryMealItemListByMealId(mealSet.getId()).size());

        assertEquals(1, mealSetService.queryAllMealSet().size());
        List<MealSet> mealSetList = mealSetService.queryMealByProductId(1);
        assertEquals(1, mealSetList.size());
        assertNotNull(mealSetService.queryMealItemBySkuIdAndMealId(2, mealSet.getId()));
        Collection<MealItem> mealItems = mealSetService.queryMealItemListByMealId(mealSet.getId());
        assertEquals(2, mealItems.size());

    }

}
