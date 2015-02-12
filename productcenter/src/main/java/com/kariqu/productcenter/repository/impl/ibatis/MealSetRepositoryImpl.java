package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.MealItem;
import com.kariqu.productcenter.domain.MealSet;
import com.kariqu.productcenter.repository.MealSetRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 13-5-4
 * Time: 下午1:58
 */
public class MealSetRepositoryImpl extends SqlMapClientDaoSupport implements MealSetRepository {

    @Override
    public void addMealSet(MealSet mealSet) {
        getSqlMapClientTemplate().insert("addMealSet", mealSet);
    }

    @Override
    public MealSet getMealSetById(int id) {
        return (MealSet) getSqlMapClientTemplate().queryForObject("getMealSetById", id);
    }

    @Override
    public void updateMealSet(MealSet mealSet) {
        getSqlMapClientTemplate().update("updateMealSet", mealSet);
    }

    @Override
    public void deleteMealSet(int id) {
        getSqlMapClientTemplate().delete("deleteMealSet", id);
        getSqlMapClientTemplate().delete("deleteMealItemByMealId", id);
    }

    @Override
    public void deleteMealItemById(int id) {
        getSqlMapClientTemplate().delete("deleteMealItemById", id);
    }

    @Override
    public List<MealSet> queryAllMealSet() {
        return getSqlMapClientTemplate().queryForList("queryAllMealSet");
    }

    @Override
    public void addMealItem(MealItem mealItem) {
        getSqlMapClientTemplate().insert("addMealItem", mealItem);
    }

    @Override
    public MealItem queryMealItemBySkuIdAndMealId(long skuId, int mealId) {
        Map map = new HashMap();
        map.put("skuId", skuId);
        map.put("mealSetId", mealId);
        return (MealItem) getSqlMapClientTemplate().queryForObject("queryMealItemBySkuIdAndMealId", map);
    }


    @Override
    public Collection<MealItem> queryMealItemListByMealId(int mealId) {
        return getSqlMapClientTemplate().queryForList("queryMealItemListByMealId", mealId);
    }

    @Override
    public List<MealItem> queryMealItemListByProductId(int productId) {
        return getSqlMapClientTemplate().queryForList("queryMealItemListByProductId", productId);
    }
}
