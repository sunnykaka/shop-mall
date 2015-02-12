package com.kariqu.productcenter.repository;

import com.kariqu.productcenter.domain.ProductActivity;
import com.kariqu.productcenter.domain.ProductActivityType;

import java.util.Date;

/**
 * User: Asion
 * Date: 13-4-1
 * Time: 下午4:41
 */
public interface ProductActivityRepository {

    /**
     * 创建商品活动记录
     *
     * @param productActivity
     */
    void createProductActivity(ProductActivity productActivity);

    /**
     * 更新商品活动记录
     *
     * @param productActivity
     */
    void updateProductActivity(ProductActivity productActivity);

    /**
     * 返回落在给定时间周围的活动数量
     *
     * @param dateInterval
     * @return
     */
    Integer getActivityCountWrapperGivingDate(int productId, Date dateInterval);

    /**
     * 返回落在给定时间周围的活动数量,除去本身之外
     * @param productId
     * @param dateInterval
     * @param id
     * @return
     */
    Integer getActivityCountWrapperGivingDateExceptSelf(int productId, Date dateInterval, int id);

    /**
     * 查询某个商品在某个特定时间点的活动，如果没有则返回 null.
     * 在同一时间只能有一个活动
     *
     * @param productId
     * @param specificDate
     * @return
     */
    ProductActivity getProductActivityByGivingTime(int productId, Date specificDate);

    /**
     * 查询指定类型(微博购或限时折扣)的活动信息.
     *
     * @param activityId
     * @param activityType
     * @return
     */
    ProductActivity getProductActivityByActivityIdAndType(int activityId, ProductActivityType activityType);

    /**
     * 返回商品在当前时间或者以后的活动个数
     *
     * @param productId
     * @return
     */
    Integer getCountOfProductJoinActivityAfterCurrentTime(int productId);


    /**
     * 删除某个商品参加某个活动的记录
     *
     * @param productId
     * @param activityId
     */
    void deleteProductActivity(int productId, long activityId);


    /**
     * 查询给定时间段内某个商品的活动数量
     * @param productId
     * @param start
     * @param end
     * @return
     */
    Integer getActivityCountInDateRange(int productId, Date start, Date end);

    /**
     *  查询给定时间段内某个商品的活动数量，除去本身之外
     * @param productId
     * @param start
     * @param end
     * @param id
     * @return
     */
    Integer getActivityCountInDateRangeExceptSelf(int productId, Date start, Date end, int id);

    /**
     * 查询某个sku在一次积分兑换活动或者积分优惠购活动中用户能购买的次数
     * @param skuId
     * @param startDate
     * @return
     */
    Integer getUserBuyCountForIntegralAndSuperConversionBySkuId(Long skuId, Date startDate);
}
