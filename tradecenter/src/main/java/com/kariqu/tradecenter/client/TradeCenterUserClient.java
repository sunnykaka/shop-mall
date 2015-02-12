package com.kariqu.tradecenter.client;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductActivityType;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.excepiton.BackGoodsBaseException;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.usercenter.domain.User;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 交易中心用户客户端
 *
 * @author Tiger
 * @version 1.0
 * @since 13-1-25 上午10:07
 */
public interface TradeCenterUserClient {

    /**
     * 提交订单
     *
     * @param submitOrderInfo 用户信息
     * @return 提交订单成功后的数据
     * @throws OrderBaseException
     */
    List<Order> submitOrder(SubmitOrderInfo submitOrderInfo) throws OrderBaseException;

    List<Order> submitCurrencyOrder(SubmitOrderInfo submitOrderInfo) throws OrderBaseException;

    /**
     * 计算积分对应的钱
     *
     * @param integral 积分分
     * @return 钱数, 单位元.
     */
    String calculateIntegralWithMoney(long integral);

    /**
     * 查询用户能使用的现金券信息.
     *
     * @param totalPrice 订单价格
     * @param userId 用户Id
     * @return
     */
    List<Coupon> queryCanUseCouponByTotalPriceAndUserId(long totalPrice, int userId);

    /**
     * 以积分为重点(<span style="color:red;">先计算现金券, 再计算积分</span>)的总价计算(基于整个订单List, 而非单个订单)
     *
     * @param totalPrice 原订单价
     * @param couponCode 现金券编码, 若未使用现金券, 空值即可
     * @param integral 积分数, 若未使用积分, 值为 0 即可
     * @param user 用户信息, 要判断用户是否足够积分
     * @return
     * @throws OrderBaseException
     */
    SubmitOrderForPrice reducePriceBeforeCouponAfterIntegral(long totalPrice, String couponCode, long integral,
                                                             User user) throws OrderNoTransactionalException;

    /**
     * 用户取消订单(需要判断是否是此客户的订单).
     *
     * @param orderId 订单id
     * @param userId  用户id
     * @throws OrderBaseException
     */
    void cancelOrder(long orderId, int userId) throws OrderBaseException;

    /**
     * 用户确认订单成功(需要判断是否是此客户的订单).
     *
     * @param orderId 订单id
     * @param userId  用户id
     * @throws OrderBaseException
     */
    void confirmOrderSuccess(long orderId, int userId) throws OrderBaseException;

    /**
     * 更新订单的支付银行.
     *
     * @param orderNo
     * @param bank
     */
    void updateOrderPayBank(long orderNo, PayBank bank);

    /**
     * 订单支付成功后.
     *
     * @param tradeInfo 交易信息
     */
    void changeOrderStateWhenPaySuccess(TradeInfo tradeInfo) throws OrderBaseException;

    /**
     * 更新订单项为已评价.
     *
     * @param valuation
     */
    void changeOrderItemAppraise(Valuation valuation) throws OrderBaseException;

    /**
     * 查询订单最新的用户留言
     *
     * @param orderId
     * @return
     */
    OrderMessage queryUserMessage(long orderId);

    /**
     * 关闭订单(系统).
     *
     * @param orderId 订单Id
     * @throws OrderBaseException
     */
    void closeOrder(long orderId) throws OrderBaseException;

    /**
     * 订单确认完成(系统).
     *
     * @param orderId 订单Id
     * @throws OrderBaseException
     */
    void confirmOrderSuccess(long orderId) throws OrderBaseException;

    /**
     * 查询未付款但超期了的订单Id
     *
     * @param delay 单位: 小时
     * @return 超过时间还未付款的订单Id
     */
    List<Long> queryNotPayOrder(int delay);

    /**
     * 查询 最新的订单状态是 "已发送", 且已经过了 <指定时间> 的订单Id
     *
     * @param delay 单位: 天
     * @return
     */
    List<Long> queryNotSuccessOrder(int delay);


    boolean existTradeInfo(String tradeNo, String outerTradeNo);

    List<Long> queryOrderListByTradeNo(String tradeNo);

    /**
     * 通过 交易号 查询其对应的所有订单信息.
     *
     * @param tradeNo 交易号
     * @return
     */
    List<Order> queryOrdersByTradeNo(String tradeNo);

    /**
     * 订单查询列表
     *
     * @param userId
     * @param orderState 订单状态区间：分为四个区间 1.未付款 2.已付款 3.交易完成 4.无效交易
     * @param page
     * @return
     */
    Page<Order> getOrderPageByUserId(int userId, Integer orderState, Page<Order> page);

    /**
     * 查询出不是取消的订单
     * @param userId
     * @param page
     * @return
     */
    Page<Order> getNotCancelOrderPageByUserId(int userId, Page<Order> page);

    /**
     * 查询申请退单的列表
     * @param userId
     * @param page
     * @return
     */
    Page<Order> getBackGoodsApplyByUserId(int userId, Page<Order> page);

    /**
     * 根据订单状态查询出该订单数量
     *
     * @param userId
     * @param orderState 订单状态区间：分为四个区间 1.未付款 2.已付款 3.交易完成 4.无效交易
     * @return
     */
    int queryOrderCountByUserIdAndState(int userId,Integer orderState);

    /**
     * 获取订单详细信息
     * 1.订单基本信息 2.发票信息
     * 3.商品列表 4.物流信息（包括客服修改用户收货信息 根据editor是否为空来判断是否改过）
     *
     * @param orderNo 订单编号
     * @param userId 用户Id
     * @return
     */
    Order getOrderDetails(long orderNo, int userId);

    /**
     * 获取订单的基本信息(不包含订单项, 物流等)
     *
     * @param userId 用户Id
     * @param orderId 订单Id
     * @return
     */
    Order getOrderBasic(long orderId, int userId);

    /**
     * 获取订货单详情进度
     *
     * @param orderId
     * @return
     */
    Progress getOrderProgress(long orderId);

    /**
     * 获取订单的详细信息(若有物流包含物流).
     *
     * @param userId
     * @param orderId
     * @param top 查询条数, 若为 0 则表示查询出全部
     * @return
     */
    List<ProgressDetail> getProgressDetail(int userId, long orderId, int top);

    /**
     * 查询用户未付款的订单数量
     *
     * @param userId
     * @return
     */
    int queryWaitPayOrderCountByUserId(int userId);

    /**
     * 查询用户待评价订单项数量
     *
     * @param userId
     * @param appraise 是否已评价
     * @return 等待评价的总数 和 待评价的订单项总金额
     */
    Map<String, Object> queryValuationCountByUserIdAndAppraise(int userId, int appraise);

    /**
     *
     * @param orderNo
     * @return
     */
    List<OrderItem> queryCanBackOrderItemByOrderNo(long orderNo);

    /**
     * 提交退货申请时生成10的缩略图
     * @param newFileName
     * @param savePosition
     * @return
     */
    String backGoodsImgCompress(String newFileName, String savePosition);

    /**
     *退货提交时保存退单凭证图片
     * @param inputStream
     * @param savePosition
     */
    void saveUploadPicture(InputStream inputStream, String savePosition);

    /**
     * 查询用户某个指定状态的订单数量.
     *
     * @param userId
     * @param orderState
     * @return
     */
    Map<String, Object> queryOrderCountByUserIdAndOrderState(int userId, OrderState orderState);

    /**
     * 查询用户一个月内的订单总量
     *
     * @param userId
     * @return
     */
    int queryRecentOrderCountByUserId(int userId);

    /**
     * 查询用户一个月内的订单
     *
     * @param userId
     * @return
     */
    Page<Order> queryRecentOrderByUserId(int userId, Page<Order> page);

    /**
     * 查询用户所有待评价的订单项
     *
     * @param userId
     * @param appraise 是否已评价
     * @param page 分页条件
     * @return 分页数据
     */
    Page<OrderItem> queryValuationByUserIdAndAppraise(int userId, int appraise, Page<OrderItem> page);


    // ====================== 退货单 ======================

    /**
     * 用户退货单列表
     * PS：这里的BackGoods 中商品列表 要包含 名称 sku说明
     *
     * @param userId
     * @param page
     * @return
     */
    Page<BackGoods> getBackGoodsPageByUserId(int userId, Page<BackGoods> page);

    /**
     * 提交退货单.
     *
     * @param backGoods 包含退货单项
     * @throws BackGoodsBaseException
     */
    void submitBackGoods(BackGoods backGoods) throws BackGoodsBaseException;

    /**
     * 取消退货单
     *
     * @param backGoodsId
     * @param userId
     * @throws BackGoodsBaseException
     */
    void cancelBackGoods(long backGoodsId, int userId) throws BackGoodsBaseException;

    /**
     * 获取退货单详情 1.退单基本信息 2.退单商品列表
     *
     * @param backGoodsId
     * @param userId
     * @return
     */
    BackGoods getBackGoodsDetails(long backGoodsId, int userId);

    /**
     * 是否可以申请退单
     *
     * @param userId
     * @param orderNo
     * @return
     * @throws BackGoodsBaseException
     */
    Result<OrderItem, Order> getCanBackOrder(int userId, long orderNo) throws BackGoodsBaseException;

    /**
     * 获取退货单历史
     *
     * @param backGoodsId
     * @return
     */
    List<BackGoodsLog> getBackGoodsLog(long backGoodsId);

    /**
     * 获取退货单详情进度
     *
     * @param backGoodsId
     * @return
     */
    Progress getBackGoodsProgress(long backGoodsId);


    // =============== 抽奖活动 ===============

    /**
     * 获取抽奖活动数据
     *
     * @param id
     * @return 抽奖活动信息
     */
    Rotary getRotaryById(int id);

    /**
     * 查询单个抽奖活动所有的中奖数据
     *
     * @param rotaryId 抽奖活动Id
     */
    List<Lottery> getAllLotteryByRotaryId(int rotaryId);

    /**
     * 抽奖
     *
     *
     *
     * @param rotaryId 抽奖活动id
     * @param userId
     *@param userName 用户名  @return 用户抽中的奖品项
     */
    Map<String, Object> lottery(int rotaryId, int userId, String userName);

    /**
     * 更新需要发货的中奖信息, 主要是收货人收货人相关信息.
     *
     * @param userName 用户名
     * @param lotteryId 中奖Id
     * @param consigneeName 收货人
     * @param consigneePhone 收货人电话
     * @param consigneeAddress 收货人地址
     */
    void updateSendLottery(String userName, int lotteryId, String consigneeName, String consigneePhone, String consigneeAddress);

    /**
     * 积分兑换
     *
     * @param userId 用户id
     * @param count 兑换数, 20, 50 还是 100?
     */
    void exchangeCoupon(int userId, int count);

    /**
     * 双十二活动: 领取优惠券
     * @param userId 用户id
     * @param count 领取券值
     */
    void receiveCoupon(int userId, int count);

}
