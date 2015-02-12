package com.kariqu.productcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.LimitedTimeDiscount;
import com.kariqu.productcenter.query.LimitedTimeDiscountQuery;

import java.util.Date;
import java.util.List;

/**
 * 限时折扣
 *
 * @author Athens(刘杰)
 * @Time 2013-03-29 16:36
 * @since 1.0.0
 */
public interface LimitedTimeDiscountRepository {

    /**
     * 创建限时折扣
     *
     * @param limitedTimeDiscount
     */
    void insert(LimitedTimeDiscount limitedTimeDiscount);

    /**
     * 更新限时折扣
     *
     * @param limitedTimeDiscount
     */
    void update(LimitedTimeDiscount limitedTimeDiscount);

    /**
     * 逻辑删除限时折扣
     *
     * @param id
     */
    void delete(long id);

    /**
     * 查询所有的限时折扣.
     *
     * @return
     */
    Page<LimitedTimeDiscount> select(LimitedTimeDiscountQuery query);

    /**
     * 单个限时折扣
     *
     *
     * @param id@return
     */
    LimitedTimeDiscount selectById(long id);

    /**
     * 查询单个商品的所有限时折扣
     *
     * @param productId
     * @return
     */
    List<LimitedTimeDiscount> selectByProductId(int productId);

    /**
     * 查询单个商品指定时间内的限时折扣信息.
     *
     * @param productId 商品Id
     * @param date 时间
     * @return
     */
    LimitedTimeDiscount selectByProductIdAndTime(long productId, Date date);

}
