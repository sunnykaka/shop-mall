package com.kariqu.categorycenter.domain.model;

import java.util.LinkedList;
import java.util.List;

/**
 * 类目的后代ID管理，同时包含了一个属性用于统计商品数量
 * 这些行为和属性用于在对类目树进行商品数量统计的时候，给定一个商品的数量就可以计算出
 * 整个类目树上的数量
 * User: Asion
 * Date: 11-10-25
 * Time: 下午4:35
 */
public abstract class DescendantManager {

    private long productNumber;//这个类目上的商品数量，用于统计计算的时候

    private List<Integer> descendantIds = new LinkedList<Integer>();//所有子孙后代的ID

    /**
     * 给定一个类目ID，判断是否是它的子孙后代
     *
     * @param categoryId
     * @return
     */
    public boolean containsDescendant(int categoryId) {
        return descendantIds.contains(categoryId);
    }

    /**
     * 加入一个子孙后代ID
     *
     * @param id
     */
    public void addDescendantCategoryId(int id) {
        descendantIds.add(id);
    }

    /**
     * 加入一串子孙后代ID
     * @param ids
     */
    public void addDescendantCategoryId(List<Integer> ids) {
        descendantIds.addAll(ids);
    }

    /**
     * 增加统计数量
     *
     * @param inc
     */
    public void increment(long inc) {
        productNumber = productNumber + inc;
    }


    public List<Integer> getDescendantIds() {
        return descendantIds;
    }

    public long getProductNumber() {
        return productNumber;
    }
}
