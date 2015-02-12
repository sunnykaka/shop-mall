package com.kariqu.tradecenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductActivityType;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.service.OrderQuery;
import com.kariqu.usercenter.domain.AccountType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单仓库
 * User: Asion
 * Date: 11-10-14
 * Time: 下午2:34
 */
public interface OrderRepository {

    VirtualOrder queryVirtualOrderById(long virtualOrderId);

    void createVirtualOrder(VirtualOrder virtualOrder);

    boolean checkVirtualOrderIfPaySuccessful(long virtualOrderId);

    /**
     * 按活动id查询商品成交量
     * @param squIds 商品的squid
     * @param skuActivityType 活动类型
     * @param skuMarketingId  活动id
     * @return
     */
    int selectCountOrderItemByMarketingId(List<Long> squIds, ProductActivityType skuActivityType, Integer skuMarketingId);

    /**
     * 查找用户已参加指定活动的次数
     * @param userId  用户id
     * @param squIds  商品的squid
     * @param skuActivityType 活动类型
     * @param skuMarketingId  活动id
     * @return
     */
    int selectUserJoinSepecialActivityCount(int userId, List<Long> squIds, ProductActivityType skuActivityType, Integer skuMarketingId);

    void createOrder(Order order);

    /**
     * 获取单个订单对象.
     *
     *
     * @param orderId
     * @return
     */
    Order getOrderById(long orderId);

    /**
     * 使用订单编号查询订单详情
     *
     * @param orderNo
     * @return
     */
    Order getOrderByOrderNo(long orderNo);

    Order getOrderByUserIdAndOrderNo(int userId,long orderNo);

    /**
     * 更新订单.
     *
     * @param order
     * @return 更新成功则返回 1
     */
    int updateOrder(Order order);

    /**
     * 更新订单状态
     *
     * @param orderId 订单Id
     * @param orderState 要更改的订单状态
     * @param previousState 必须的前置状态
     * @return
     */
    int updateOrderState(long orderId, OrderState orderState, OrderState previousState);

    /**
     * 客服审核通过货到付款的订单.
     *
     * @param orderId
     * @return
     */
    int updateOrderStateToConfirm(long orderId);

    /**
     * 通过订单状态查询订单.
     *
     * @param orderState 订单状态
     * @return
     */
    List<Order> queryOrderByState(OrderState orderState);

    List<Long> queryOrderByCanConfirm(int day, int limit, String userNameRegex);

    /**
     * 查询所有货到付款的订单.
     *
     * @return
     */
    List<Order> selectCashOnDeliveryOrder();

    void deleteOrder(long orderId);

    void createOrderItem(OrderItem orderItem);

    /**
     * 给定一个期限查询未付款的订单
     * 和当前系统时间做比较，发现相隔时间再delay之内就返回
     *
     * @param delay
     * @return
     */
    List<Long> queryNotPayOrder(int delay);

    /**
     * 查询最新的订单状态为 "已发送", 则已经过了 <指定时间> 的订单Id
     *
     * @param delay 单位: 天
     * @return
     */
    List<Long> queryNotConfirmSuccessOrder(int delay);

    /**
     * 更新互斥状态
     *
     * @param orderId
     * @param orderState
     */
    void updateOrderMutexStateHistory(long orderId, OrderState orderState);

    /**
     * 创建
     *
     * @param orderStateHistory
     */
    void createOrderStateHistory(OrderStateHistory orderStateHistory);

    /**
     * 更新订单历史记录成用户不可见状态(主要针对商家将状态置回时)
     *
     * @param orderId
     * @param orderState
     */
    void updateOrderStateHistoryToVisibleWithUser(long orderId, OrderState orderState);

    /**
     * 查询用户可见的订单状态历史(创建, 付款, 发送, 成功)
     *
     * @param orderId
     * @return
     */
    List<OrderStateHistory> queryUserModeOrderStateHistory(long orderId);

    /**
     * 查询去重复之后最新的订单状态历史
     *
     * @param orderId
     * @return
     */
    List<OrderStateHistory> queryOrderStateHistoryDistinct(long orderId);

    /**
     * 查询订单的所有状态历史
     *
     * @param orderId
     * @return
     */
    List<OrderStateHistory> queryAllOrderStateHistory(long orderId);

    List<OrderItem> queryOrderItemsByOrderId(long orderId);

    OrderItem queryOrderItemById(long orderItemId);

    List<Order> queryOrderByUserId(int userId, AccountType accountType);

    Page<Order> queryOrderByUserIdPage(int userId, Integer orderState, Page<Order> page);

    Page<Order> getNotCancelOrderPageByUserId(int userId, Page<Order> page);

    Page<Order> queryBackGoodsApplyByUserIdPage(int userId, Page<Order> page);

    int queryOrderCountByUserIdAndState(int userId, Integer orderState);

    /** 查询用户未处理的订单数 */
    int queryCountForNoProcessOrder(int userId);

    /**
     * 查询用户待评价订单项数量
     *
     * @param userId
     * @param appraise 是否已评价(0.未评价, 1.已评价)
     * @return 等待评价的总数 和 待评价的订单项总金额
     */
    Map<String, Object> queryValuationCountByUserId(int userId, int appraise);

    /**
     * 根据订单号查询能退货的订单项
     * @param orderNo
     * @return
     */
    public List<OrderItem> queryCanBackOrderItemByOrderNo(long orderNo);

    /**
     * 查询用户所有待评价的订单项
     *
     * @param userId
     * @param appraise
     * @param page
     * @return
     */
    Page<OrderItem> queryWaitValuationByUserId(int userId, int appraise, Page<OrderItem> page);

    /**
     * 查询用户一个月内的订单
     *
     * @param userId
     * @return
     */
    Page<Order> queryRecentOrderByUserId(int userId, Page<com.kariqu.tradecenter.domain.Order> page);

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
     * 查询用户某个指定状态的订单数量
     *
     * @param userId
     * @param orderState
     * @return
     */
    Map<String, Object> queryOrderCountByUserIdAndOrderState(int userId, OrderState orderState);

    void createSystemEvent(SystemEvent systemEvent);

    List<SystemEvent> querySystemEvents(long orderId);

    void deleteSystemEvents(long orderId);

    /**
     * 查询指定订单的详情数量.
     *
     * @param orderId 订单编号
     * @return
     */
    int queryOrderItemCountByOrderId(long orderId);

    /**
     * 查询指定订单下的指定状态的详情数量.
     *
     * @param orderId 订单编号
     * @param state 订单状态
     * @return
     */
    int queryOrderItemCountByOrderIdAndState(long orderId, OrderState state);

    Page<Order> searchOrderByQuery(OrderQuery orderQuery);

    /**
     * 查询订单, 无分页. 主要用来数据导出
     *
     * @param orderQuery
     * @return
     */
    List<List<Order>> searchOrderListByQuery(OrderQuery orderQuery);

    void updateVirtualOrder(VirtualOrder virtualOrder);

    List<Order> queryOrderByVirtualOrderId(long virtualOrderId);

    List<Order> queryOrderByCustomerId(int customerId, OrderState orderState);

    /**
     * 在订单表中查找指定虚拟订单编号所有不包含此订单状态的订单记录
     *
     * @param virtualOrderId 虚拟订单Id
     * @param orderState     订单状态
     * @return
     */
    List<Order> queryOrderByVirtualIdExcludeState(long virtualOrderId, OrderState orderState);

    Page<VirtualOrder> queryVirtualOrderByPage(int userId, AccountType accountType,
                                               String orderTime, String orderStatus, Page<VirtualOrder> page);

    Page<Order> searchQuery(Map param, Page<Order> page);

    List<Order> searchQueryList(Map param);

    int updateOrderItem(OrderItem orderItem);


    /**
     * 更新订单项状态
     *
     * @param orderItemId 订单项 Id
     * @param orderState 要更改的订单项状态
     * @param previousState 必须的前置订单项状态
     * @return 若更新正常则返回 1
     */
    int updateOrderItemState(long orderItemId, OrderState orderState, OrderState previousState);

    /**
     * 修改订单项状态为已评价.
     *
     * @param orderItemId 订单项 Id
     * @return
     */
    int updateOrderItemAppraise(long orderItemId);

    String getOrderPriceByOrderNo(long orderNo);

    /**
     * 更新订单支付银行信息.
     *
     * @param orderNo 订单编号
     * @param bank 支付银行
     */
    void updateOrderPayBank(long orderNo, PayBank bank);

    void updateOrderDeliveryType(long orderId, DeliveryInfo.DeliveryType type);

    /**
     * 查询指定订单编号, 指定状态的最新历史记录.
     *
     *
     * @param orderId
     * @param orderState
     * @return
     */
    OrderStateHistory queryHistoryByState(long orderId, OrderState orderState);

    /**
     * 查询指定订单编号下除了指定状态以外最新的一条历史记录.
     *
     * @param orderId 订单编号
     * @param state 排除的订单状态
     * @return
     */
    OrderStateHistory queryHistoryWithOutState(long orderId, OrderState state);

    /**
     * 查询指定状态未删除的订单数量
     *
     * @param state 订单状态
     * @return
     */
    int queryOrderCountByState(OrderState state);

    /**
     * 查询所有等待客服审核的订单数量.
     *
     * @return
     */
    int queryOrderCountByWaitApproval();

    /**
     * 客服修改 用户订单发票信息冗余
     * @param orderId
     * @return
     */
    InvoiceInfo queryOrderInvoiceInfoRedundancy(long orderId);


    void updateOrderInvoiceInfoRedundancy(long orderId, InvoiceInfo invoiceInfo);

    /**
     * 添加订单留言信息
     *
     * @param orderMessage
     */
    void createOrderMessage(OrderMessage orderMessage);

    /**
     * 查询订单的所有留言信息.
     *
     * @param orderId 订单Id
     * @return
     */
    List<OrderMessage> getOrderMessage(long orderId);

    /**
     * 查询订单的指定留言信息.
     *
     * @param orderId 订单Id
     * @param userType 用户, 客服, 商家
     * @return
     */
    OrderMessage getOrderMessageByIdAndType(long orderId, OrderMessage.UserType userType);

    /**
     * 查询指定优惠券Id 作用在订单上的数量(不包括指定的订单Id 和 已取消 或 已关闭 状态的订单)
     *
     *
     * @param couponId
     * @param orderId
     * @return
     */
    List<Map<String, Object>> getCountByCouponIdWithoutId(int couponId, long orderId);

    /**
     * 订单刷单
     * @param orderId
     * @return
     */
    int brush(long orderId);
}
