package com.kariqu.productcenter.service.impl;

import com.kariqu.productcenter.domain.MealItem;
import com.kariqu.productcenter.domain.MealSet;
import com.kariqu.productcenter.repository.MealSetRepository;
import com.kariqu.productcenter.service.MealSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: Asion
 * Date: 13-5-4
 * Time: 下午1:43
 */
public class MealSetServiceImpl implements MealSetService {

    @Autowired
    private MealSetRepository mealSetRepository;

    @Override
    public MealSet getMealSetById(int id) {
        return mealSetRepository.getMealSetById(id);
    }

    @Override
    @Transactional
    public void addMealSet(MealSet mealSet) {
        mealSetRepository.addMealSet(mealSet);
    }

    @Override
    @Transactional
    public void updateMealSet(MealSet mealSet) {
        mealSetRepository.updateMealSet(mealSet);
    }

    @Override
    @Transactional
    public void deleteMealSet(int id) {
        mealSetRepository.deleteMealSet(id);
    }

    @Override
    @Transactional
    public void deleteMealItemById(int id) {
        mealSetRepository.deleteMealItemById(id);
    }

    @Override
    public List<MealSet> queryAllMealSet() {
        return mealSetRepository.queryAllMealSet();
    }

    @Override
    @Transactional
    public void addMealItem(MealItem mealItem) {
        mealSetRepository.addMealItem(mealItem);
    }

    @Override
    public List<MealSet> queryMealByProductId(int productId) {
        List<MealItem> mealItems = mealSetRepository.queryMealItemListByProductId(productId);

        //如果一个商品下的很多sku都可以套餐，这个列表要根据商品分组筛选

        Set<Integer> set = new HashSet<Integer>();
        for (MealItem mealItem : mealItems) {
            set.add(mealItem.getMealSetId());
        }

        List<MealSet> mealSetList = new ArrayList<MealSet>();
        for (Integer id : set) {
            mealSetList.add(getMealSetById(id));
        }
        return mealSetList;
    }

    public void main (String arg[]){

    }

    @Override
    public Collection<MealItem> queryMealItemListByMealId(int mealId) {
        //如果一个商品的多个sku都可以加进套餐，则通过商品ID过滤出价格最低的
        /*Collection<MealItem> mealItems = mealSetRepository.queryMealItemListByMealId(mealId);
        Map<Integer, MealItem> map = new LinkedHashMap<Integer, MealItem>();
        for (MealItem mealItem : mealItems) {
            if (map.get(mealItem.getProductId()) == null) {
                map.put(mealItem.getProductId(), mealItem);
            } else if (map.get(mealItem.getProductId()).getSkuPrice() > mealItem.getSkuPrice()) {
                map.put(mealItem.getProductId(), mealItem);
            }
        }
        return map.values();*/
        return mealSetRepository.queryMealItemListByMealId(mealId);
    }

    @Override
    public MealItem queryMealItemBySkuIdAndMealId(long skuId, int mealId) {
        return mealSetRepository.queryMealItemBySkuIdAndMealId(skuId, mealId);
    }

    public void setMealSetRepository(MealSetRepository mealSetRepository) {
        this.mealSetRepository = mealSetRepository;
    }
}
