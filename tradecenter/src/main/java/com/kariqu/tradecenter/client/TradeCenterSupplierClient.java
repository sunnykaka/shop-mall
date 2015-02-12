package com.kariqu.tradecenter.client;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.service.Query;

import java.util.List;

/**
 * 交易中心商家客户端
 *
 * @author Tiger
 * @version 1.0
 * @since 13-1-25 上午10:08
 */
public interface TradeCenterSupplierClient {

    /**
     * 修改物流信息.
     *
     * @param logistics
     */
    void updateLogistics(Logistics logistics);

    /**
     * 打印订单.
     *
     * @param orderId
     * @param supplierName
     * @throws OrderBaseException
     */
    void printOrderForSupplier(long orderId, String supplierName) throws OrderBaseException;

    /**
     * 将 已打印 的订单置回 已确认等待打印.
     *
     * @param orderId
     * @param supplierName
     */
    void turnBackOrderToConfirmForSupplier(long orderId, String supplierName) throws OrderBaseException;

    /**
     * 验证订单(验货).
     *
     * @param orderId
     * @param supplierName
     */
    void validateOrderForSupplier(long orderId, String supplierName) throws OrderBaseException;

    /**
     * 将 已验货 的订单置回 已打印等待验货.
     *
     * @param orderId
     * @param supplierName
     */
    void turnBackOrderToPrintForSupplier(long orderId, String supplierName) throws OrderBaseException;

    /**
     * 发货.
     *
     * @param orderId
     * @param supplierName
     */
    void deliveryOrderForSupplier(long orderId, String supplierName) throws OrderBaseException;

    /**
     * 查询订单项的实际发货数量(把已经退货的订单项数量去除).
     *
     * @param orderId
     * @return
     */
    List<OrderItem> queryOrderItemWithoutBackingNumberByOrderId(long orderId);

    /**
     * 查询某个订单修改过的物流信息
     * @param logisticsId
     * @return
     */
    LogisticsRedundancy queryLogisticsRedundancy(long logisticsId);

    /**
     * 查询订单的用户留言
     *
     * @param orderId
     * @return
     */
    OrderMessage queryUserMessage(long orderId);

    /**
     * 查询订单的客服留言
     *
     * @param orderId
     * @return
     */
    OrderMessage queryServerMessage(long orderId);

    /**
     * 查询某个订单的默认物流信息
     * @param orderId
     * @return
     */
    Logistics getLogisticsByOrderId(long orderId);

    /**
     * 查询指定订单编号, 指定状态的最新历史记录.
     *
     * @param orderId
     * @param orderState
     * @return
     */
    OrderStateHistory queryHistoryByState(long orderId, OrderState orderState);

    /**
     * 根据物流单号查询订单Id(最新的那个)
     *
     * @param expressNo 物流单号
     * @return
     */
    long getOrderIdByExpressNo(String expressNo);

    /**
     * 根据物流单号查询订单Id
     *
     * @param expressNo 物流单号
     * @return
     */
    List<Long> getOrderIdsByExpressNo(String expressNo);

    /**
     * 获取简单的订单对象, 不包含 orderItem 物流等相关信息.
     *
     * @param orderId
     * @return
     */
    Order getSimpleOrder(long orderId);

    /**
     * 查询指定退货单的 指定退货单状态 最新的日志信息
     *
     * @param orderId 订单编号
     * @return
     */
    BackGoodsLog queryBackGoodsLogByState(long orderId);

    /**
     * 根据订单刷条件查询订单集合 对其分页
     * @param query
     * @param customerId
     * @param orderPage
     * @return
     */
    Page<Order> searchQuery(Query query, int customerId, Page<Order> orderPage);

    /**
     * 根据订单刷条件所有查询订单集合
     * @param query
     * @param customerId
     * @return
     */
    List<Order> searchQueryList(Query query, int customerId);

    /**
     * 查询第三方物流信息. 若返回 "true" 则前台轮询去查询数据库, 返回 "false" 表示请求失败, 若返回 json 字段, 则前台直接解析.
     *
     *
     * @param company 快递公司
     * @param number  物流单号
     * @param from    从哪来
     * @param to      到哪去
     * @return 若物流单已经完成, 则返回此物流单的 json 数据; 如果向三方物流发送请求并返回了成功数据则返回 "true", 否则返回 "false"
     */
    void queryThirdLogisticsInfo(String company, String number, String from, String to);

    /**
     * 查询订单发票信息
     *
     * @param orderId
     * @return
     */
    InvoiceInfo queryOrderInvoiceInfoRedundancy(long orderId);

}
