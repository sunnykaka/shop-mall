package com.kariqu.tradecenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.domain.payment.SkuTradeResult;
import com.kariqu.usercenter.domain.AccountType;

import java.util.List;
import java.util.Map;

/**
 * 只负责查询订单信息
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-4
 *        Time: 下午2:37
 */
public interface OrderQueryService {

    /** 查询用户未处理的订单数 */
    int queryCountForNoProcessOrder(int userId);

    /**
     * 查询所有 等待客服审核 的未删除订单数量.
     *
     * @return
     */
    int queryOrderCountByWaitApproval();

    /**
     * 搜索订单
     *
     * @param orderQuery
     * @return
     */
    Page<Order> searchOrderByQuery(OrderQuery orderQuery);

    /**
     * 搜索订单, 无分页信息. 主要用来订单导出
     *
     * @param orderQuery
     * @return
     */
    List<List<Order>> searchOrderListByQuery(OrderQuery orderQuery);

    Page<Order> searchQuery(Query query, int customerId, Page<Order> orderPage);

    List<Order> searchQueryList(Query query, int customerId);

    List<SystemEvent> querySystemEvents(long orderId);


    Logistics getLogistics(long id);

    Logistics getLogisticsByOrderId(long orderId);

    List<LogisticsEvent> queryLogisticsEvents(long logisticsId);

    List<PaymentEvent> queryPaymentEvents(long id);

    /**
     * 查询某个订单的订单项
     *
     * @param orderId
     * @return
     */
    List<OrderItem> queryOrderItemsByOrderId(long orderId);

    /**
     * 查询单个订单项.
     *
     * @param orderItemId
     * @return
     */
    OrderItem queryOrderItemsById(long orderItemId);

    /**
     * 查询未付款但超期了的订单Id
     *
     * @param delay
     * @return
     */
    List<Long> queryNotPayOrder(int delay);

    /**
     * 查询 订单状态最新为 "已发送", 且已经过了 <指定时间> 的订单Id
     *
     * @param delay 单位: 天
     * @return
     */
    List<Long> queryNotConfirmSuccessOrder(int delay);

    /**
     * 查询用户可见的订单状态历史(创建, 付款, 发送, 成功)
     *
     * @param orderId
     * @return
     */
    List<OrderStateHistory> queryUserModeOrderStateHistory(long orderId);

    List<Order> queryOrderByUserId(int userId, AccountType accountType);

    /**
     * 查询商家下指定状态的订单
     *
     * @param customerId 商家编号
     * @param orderState 订单状态
     * @return
     */
    List<Order> queryOrderByCustomerId(int customerId,OrderState orderState);

    Page<Order> queryOrderByUserIdPage(int userId, Integer orderState, Page<Order> page);

    Page<Order> getNotCancelOrderPageByUserId(int userId, Page<Order> page);

    Page<Order> queryBackGoodsApplyByUserId(int userId, Page<Order> page);

    int queryOrderCountByUserIdAndState(int userId,Integer orderState);

    /**
     * 查询用户待评价订单项数量
     *
     * @param userId
     * @param appraise
     * @return 等待评价的总数 和 待评价的订单项总金额
     */
    Map<String, Object> getValuationCountByUserIdAndAppraise(int userId, int appraise);

    /**
     * 根据订单号查询能退货的订单项
     * @param orderNo
     * @return
     */
    List<OrderItem> queryCanBackOrderItemByOrderNo(long orderNo);

    /**
     * 查询用户所有待评价的订单项
     *
     * @param userId
     * @param appraise
     * @param page  @return
     */
    Page<OrderItem> queryValuationByUserIdAndAppraise(int userId, int appraise, Page<OrderItem> page);

    /**
     * 查询用户一个月内的订单
     *
     * @param userId
     * @return
     */
    Page<Order> queryRecentOrderByUserId(int userId, Page<Order> page);

    /**
     * 查询用户一个月内的订单总量
     *
     * @param userId
     * @return
     */
    int queryRecentOrderCountByUserId(int userId);

    /**
     * 查询用户未付款的订单数量
     *
     * @param userId
     * @return
     */
    int queryWaitPayOrderCountByUserId(int userId);

    /**
     * 查询用户某个指定状态的订单数量.
     *
     * @param userId
     * @param orderState
     * @return
     */
    Map<String, Object> queryOrderCountByUserIdAndOrderState(int userId, OrderState orderState);

    /**
     * 查询订单(订单、订单项、收货地址信息)
     *
     * @param orderId 订单Id
     * @return
     */
    Order getDetailsOrder(long orderId);

    /**
     * 获取订单数据.
     *
     * @param orderId
     * @return
     */
    Order getSimpleOrder(long orderId);

    Order getSimpleOrderByOrderNo(long orderNo);

    /**
     * 查询订单(订单、订单项、收货地址信息)
     *
     * @param orderNo
     * @return
     */
    Order getOrderByOrderNo(long orderNo);

    Order getOrderByUserIdAndOrderNo(int userId,long orderNo);

    String getOrderPriceByOrderNo(long orderNo);

    /**
     * 筛选实体订单中对应虚拟订单下不存在此订单状态的 实体订单 数据
     *
     * @param virtualOrderId 虚拟订单编号
     * @param orderState     不存在的订单状态
     * @return
     */
    List<Order> queryOrderByVirtualIdExcludeState(long virtualOrderId, OrderState orderState);

    LogisticsRedundancy queryLogisticsRedundancy(long id);

    InvoiceInfo queryOrderInvoiceInfoRedundancy(long orderId);

    /**
     * 查询去重复之后最新的订单状态历史
     *
     * @param orderId
     * @return
     */
    List<OrderStateHistory> queryOrderStateHistoryDistinct(long orderId);

    /**
     * 查询单个订单号的相差信息(若有物流则包含物流信息.)
     *
     * @param orderId 订单号
     * @param number 订单条数, 若为 0 则返回全部
     * @return
     */
    List<ProgressDetail> queryProgressDetail(long orderId, int number);

    /**
     * 查询第三方物流返回的物流信息.
     *
     * @param orderId 订单Id
     * @return
     */
    BackMsg queryLogisticsInfoByOrderId(long orderId);

    /**
     * 查询单个订单Id 的所有进度条状态.
     *
     * @param orderId 订单Id
     * @return
     */
    Progress queryOrderProgress(long orderId);

    /**
     * 查询订单项的实际发货数量(把已经退货的订单项数量去除).
     *
     * @param orderId
     * @return
     */
    List<OrderItem> queryRealOrderItemNumberByOrderId(long orderId);

    /**
     * 查询某一类状态的订单信息.
     *
     * @param orderState
     * @return
     */
    List<Order> queryOrderByState(OrderState orderState);

    List<Long> queryOrderByCanConfirm(int day, int limit, String userNameRegex);

    /**
     * 查询所有货到付款的订单.
     *
     * @return
     */
    List<Order> queryCashOnDeliveryOrder();

    /**
     * 查询指定 SKU 对应的销售对象
     *
     * @param skuId
     * @return
     */
    SkuTradeResult querySkuTradeResultBySkuId(long skuId);

    /**
     * 查询订单的所有留言信息.
     *
     * @param orderId
     * @return
     */
    List<OrderMessage> queryAllOrderMessage(long orderId);

    /**
     * 查询订单的用户留言.
     *
     * @param orderId
     * @return
     */
    OrderMessage queryUserOrderMessage(long orderId);

    /**
     * 查询订单的客服留言.
     *
     * @param orderId
     * @return
     */
    OrderMessage queryServerOrderMessage(long orderId);

    /**
     * 查询指定优惠券Id 作用在订单上的数量(不包括指定的订单Id 和 已取消 或 已关闭 状态的订单)
     *
     *
     * @param couponId
     * @param orderId
     * @return
     */
    List<Map<String, Object>> queryCountByCouponIdWithoutId(int couponId, long orderId);

    /**
     * 查询指定订单下指定状态的最新历史记录.
     *
     * @param orderId
     * @param orderState
     * @return
     */
    OrderStateHistory queryHistoryByState(long orderId, OrderState orderState);
}
