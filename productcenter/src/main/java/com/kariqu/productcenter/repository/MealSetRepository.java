package com.kariqu.productcenter.repository;

import com.kariqu.productcenter.domain.MealItem;
import com.kariqu.productcenter.domain.MealSet;

import java.util.Collection;
import java.util.List;

/**
 * User: Asion
 * Date: 13-5-4
 * Time: 下午1:57
 */
public interface MealSetRepository {

    void addMealSet(MealSet mealSet);

    void updateMealSet(MealSet mealSet);

    void deleteMealSet(int id);

    void deleteMealItemById(int id);


    List<MealSet> queryAllMealSet();

    MealSet getMealSetById(int id);

    void addMealItem(MealItem mealItem);

    MealItem queryMealItemBySkuIdAndMealId(long skuId, int mealId);

    Collection<MealItem> queryMealItemListByMealId(int mealId);

    List<MealItem> queryMealItemListByProductId(int productId);
}
