package com.kariqu.productcenter.service;

import com.kariqu.productcenter.domain.ProductActivity;
import com.kariqu.productcenter.domain.ProductActivityType;
import com.kariqu.productcenter.domain.SkuPriceDetail;
import com.kariqu.productcenter.domain.StockKeepingUnit;

import java.util.Date;

/**
 * User: Asion
 * Date: 13-4-1
 * Time: 下午3:42
 */
public interface ProductActivityService {


    /**
     * 创建商品活动记录
     *
     */
    void createProductActivity(int productId,int activityId,ProductActivityType activityType,Date startDate,Date endDate,String activityPrice);

    /**
     * 更新商品活动记录, 并更新商品的时间戳被搜索引擎感知
     *
     * @param productActivity
     */
    void updateProductActivity(ProductActivity productActivity);

    /**
     * 检查商品是否可以被分配活动
     * 必须指定开始时间和结束时间
     *
     * @param productId
     * @param start
     * @param end
     */
    void checkProductActivityAssign(int productId, Date start, Date end) throws ProductActivityException;

    /**
     * 检查商品是否可以被分配活动 ,除去本身之外
     * 必须指定开始时间和结束时间
     *
     * @param productId
     * @param start
     * @param end
     * @param id
     * @throws ProductActivityException
     */
    void checkProductActivityAssignExceptSelf(int productId, Date start, Date end, int id) throws ProductActivityException;

    /**
     * 通过 sku 查询当前参加的活动, 若无则返回 null
     *
     * @param skuId
     * @return
     */
    ProductActivity getProductActivityBySkuIdWithNow(long skuId);

    /**
     * 查询指定类型(限时折扣)的活动信息.
     *
     * @param activityId
     * @param activityType
     * @return
     */
    ProductActivity getProductActivityByActivityIdAndType(int activityId, ProductActivityType activityType);

    /**
     * 获取 SKU 价格详情(按照每种活动进行其对应的查询).
     *
     * @param sku
     * @return
     */
    SkuPriceDetail getSkuMarketingPrice(StockKeepingUnit sku);

    /**
     * 检查是否商品是否在活动中
     *
     * @param productId
     * @return
     */
    boolean checkProductIfJoinActivity(int productId);


    /**
     * 删除某个商品的活动记录
     * @param productId
     * @param activityId
     */
    void deleteProductActivity(int productId, long activityId);

    /**
     * 查询某个sku在一次积分兑换活动或者积分优惠购活动中用户能购买的次数
     * @param skuId
     * @param startDate
     * @return
     */
    Integer getUserBuyCountForIntegralAndSuperConversionBySkuId(Long skuId, Date startDate);

}
