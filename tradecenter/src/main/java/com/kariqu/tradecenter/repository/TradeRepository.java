package com.kariqu.tradecenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.domain.RefundTradeOrder;
import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.service.TradeQuery;
import com.kariqu.tradecenter.domain.RefundTrade;
import com.kariqu.tradecenter.service.CouponQuery;

import java.util.List;
import java.util.Map;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-15
 * Time: 下午3:33
 */
public interface TradeRepository {
    /**
     * 创建交易信息
     *
     * @param tradeInfo
     */
    void createTradeInfo(TradeInfo tradeInfo);

    /**
     * 更新交易信息
     *
     * @param tradeInfo
     */
    void updateTradeInfo(TradeInfo tradeInfo);

    TradeInfo selectTradeInfoById(int id);

    int triggerTradeOrderPaySuccessful(String tradeNo);

    int queryTradeInfo(String tradeNo, String outerTradeNo);

    List<Long> queryOrderListByTradeNo(String tradeNo);
    /**
     * 根据交易号查询订单号列表,并且是交易成果的
     * 修改人：Json.zhu
     * 修改时间：2013.12.12
     * @param tradeNo
     * @return
     */
    public List<Long> queryOrderListByTradeNoAndPayFlag(String tradeNo);

    void createOrderTradeInfo(String tradeNo, Long[] orderList);

    void triggerTradeOrderPayRecharge(String tradeNo, Long orderNo);

    String queryOuterTradeNoByOrderNo(long orderNo);
    String queryTradeNoByOrderNo(long orderNo);

    void createRefundTradeOrder(RefundTradeOrder refundTradeOrder);

    void createRefundTrade(Map map);

    /**
     * 使某批次某个交易号的退款标识成功
     *
     * @param batchNo
     * @param outerTradeNo
     * @param cent
     */
    int updateRefundTradeOrderSuccess(String batchNo, String outerTradeNo, long cent);


    long getBackIdByBatchNoAndTradeNo(String batchNo, String outerTradeNo);

    void createCoupon(Coupon coupon);

    Page<Coupon> queryNotUsedCoupon(Page<Coupon> page, String code, int userId);

    /**
     * 查询用户未使用的现金券.
     *
     * @param userId
     * @param page
     * @return
     */
    Page<Coupon> queryNotUsedCoupon(int userId, Page<Coupon> page);

    /**
     * 查询用户未使用的现金券数量
     *
     * @param userId
     * @return
     */
    int queryCountNotUsedCouponByUserId(int userId);

    Page<Coupon> queryUsedCoupon(Page<Coupon> page, String code, int userId);

    Page<Coupon> queryExpireCoupon(Page<Coupon> page, String code, int userId);

    Page<Coupon> queryAllocationCoupon(Page<Coupon> page, String code);

    Page<Coupon> queryAllUMPayCoupon(Page<Coupon> page, String code);

    Coupon queryCouponByCode(String code);

    Coupon getCouponById(int id);

    /**
     * 获取所有需要短信提醒的现金券数据
     *
     * @param interval 时间(比如今天是 1 号, 此值是 5, 则提醒 5 号开始过期的现金券)
     * @return 符合条件的数据
     */
    List<Coupon> getMsgRemindCoupon(int interval);

    int updateCoupon(Coupon coupon);

    void deleteCoupon(int id);

    /**
     * 查询用户所有能用的现金券信息.
     *
     * @param totalPrice 订单总价
     * @param userId     用户Id
     * @return
     */
    List<Coupon> getCanUseCouponByTotalPriceAndUserId(long totalPrice, int userId);

    int makeCouponUsed(String code, long orderNo);

    List<Coupon> queryCouponByUserId(int couponId);

    Page<Coupon> queryCoupon(CouponQuery couponQuery);

    /**
     * 返回指定金额和指定个数的没有被分配的现金券
     *
     * @param number
     */
    List<Coupon> queryNotUsedAndNotAssignCoupon(long price, int number);


    RefundTradeOrder queryRefundTradeOrderByBackGoodsId(long backGoodsId);

    /**
     * 根据指定的(交易方式，支付方式，交易开始时间和交易结束时间)条件来查询
     * 带分页的
     * 修改人：Json.zhu
     * 修改时间：2013.12.09，15：13
     * @param tradeQuery
     */
    Page<TradeInfo> getTradeByConditions(TradeQuery tradeQuery);

    /**
     * 根据指定的(交易方式，支付方式，交易开始时间和交易结束时间)条件来查询
     * 修改人：Json.zhu
     * 修改时间：2013.12.09，15：13
     * @param tradeQuery
     */
    List<TradeInfo> getTradeListByListConditions(TradeQuery tradeQuery);

    /**
     * 查询出所有成功退款的项
     * 修改人：Json.zhu
     * 修改时间：2013.12.13，13：02
     * @param startDate
     * @param endDate
     * @return
     */
    List<RefundTrade> getRefundTradeAllInfo(String startDate, String endDate);

    /**
     * 根据batchNo查询出所有的退款中的订单项
     * 修改人：Json.zhu
     * 修改时间：2013.12.13，13：32
     * @param batchNo
     * @return
     */
    List<RefundTradeOrder> getBackIdByBatchNo(String batchNo);
}
