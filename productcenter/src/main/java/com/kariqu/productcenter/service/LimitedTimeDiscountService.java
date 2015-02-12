package com.kariqu.productcenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.LimitedTimeDiscount;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.query.LimitedTimeDiscountQuery;

import java.util.Date;
import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-03-29 17:14
 * @since 1.0.0
 */
public interface LimitedTimeDiscountService {

    /**
     * 创建限时折扣
     *
     * @param limitedTimeDiscount
     */
    void createLimitedTimeDiscount(LimitedTimeDiscount limitedTimeDiscount) throws ProductActivityException;

    /**
     * 更新限时折扣
     *
     * @param limitedTimeDiscount
     */
    void updateLimitedTimeDiscount(LimitedTimeDiscount limitedTimeDiscount);

    /**
     * 更新单个商品限时折扣对应 sku 的单个价格
     *
     * @param limitedTimeDiscountId
     * @param skuId
     * @param skuPrice
     */
    void updateLimitedTimeDiscountSkuPrice(long limitedTimeDiscountId, long skuId, long skuPrice);

    /**
     * 逻辑删除限时折扣
     *
     * @param limitedTimeDiscountId
     */
    void deleteLimitedTimeDiscount(long limitedTimeDiscountId);

    /**
     * 查询所有的限时折扣
     *
     * @return
     * @param query
     */
    Page<LimitedTimeDiscount> queryLimitedTimeDiscount(LimitedTimeDiscountQuery query);

    /**
     * 查询单个商品Id的所有限时折扣
     *
     * @param productId 还是商品名?
     * @return
     */
    List<LimitedTimeDiscount> queryLimitedTimeDiscountByProductId(int productId);

    /**
     * 查询单个订单某个时间的限时折扣信息(单个时间区间只能有一个折扣信息).
     *
     * @param productId
     * @param date
     * @return
     */
    LimitedTimeDiscount queryLimitedTimeDiscountByProductIdAndTime(int productId, Date date);

    /**
     * 查询单条限时折扣
     *
     * @param limitedTimeDiscountId
     * @return
     */
    LimitedTimeDiscount queryLimitedTimeDiscount(long limitedTimeDiscountId);

    /**
     * 查询某个 sku 当前的限时折扣价格
     *
     * @param sku
     * @return 若无限时折扣信息, 则返回 sku 价格, 若有限时折扣信息则返回, 若无 sku 信息则计算限时折扣信息
     */
    long querySkuPriceByLimitedTimeDiscount(StockKeepingUnit sku);

}
