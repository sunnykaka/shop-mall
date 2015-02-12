package com.kariqu.productcenter.service;

import com.kariqu.productcenter.domain.MealItem;
import com.kariqu.productcenter.domain.MealSet;

import java.util.Collection;
import java.util.List;

/**
 * User: Asion
 * Date: 13-5-4
 * Time: 下午1:32
 */
public interface MealSetService {

    MealSet getMealSetById(int id);

    void addMealSet(MealSet mealSet);

    void updateMealSet(MealSet mealSet);

    void deleteMealSet(int id);

    void deleteMealItemById(int id);

    List<MealSet> queryAllMealSet();


    /**
     * 添加套餐项
     *
     * @param mealItem
     */
    void addMealItem(MealItem mealItem);


    /**
     * 查询某个商品的套餐列表
     *
     * @param productId
     * @return
     */
    List<MealSet> queryMealByProductId(int productId);


    /**
     * 根据mealId查询套餐项
     *
     * @param mealId
     * @return
     */
    Collection<MealItem> queryMealItemListByMealId(int mealId);


    /**
     * 根据mealId和skuId唯一定位mealItem
     *
     * @param skuId
     * @param mealId
     * @return
     */
    MealItem queryMealItemBySkuIdAndMealId(long skuId, int mealId);


}
