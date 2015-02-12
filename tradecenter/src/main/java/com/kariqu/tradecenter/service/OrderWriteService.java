package com.kariqu.tradecenter.service;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.domain.payment.SkuTradeResult;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.excepiton.OrderTransactionalException;

/**
 * 订单写入服务
 *
 * @author Tiger
 * @version 1.0
 * @since 13-1-10 下午3:38
 */
public interface OrderWriteService {

    /**
     * 创建订单.
     * 前置条件： order 中需要带上 orderItem 和 logistics 的信息
     *
     * @param order 订单及订单项.
     * @param messageInfo
     */
    void createNewOrder(Order order, String messageInfo);

    /**
     * 取消订单(对象应该是已经检查过用户名的).
     *
     * @param order
     * @throws OrderBaseException
     */
    void cancelOrder(Order order) throws OrderBaseException;

    /**
     * 关闭订单.
     *
     *
     * @param orderId 订单Id
     * @param operator
     * @throws OrderBaseException
     */
    void closeOrder(long orderId, String operator) throws OrderBaseException;

    /**
     * 订单付款完成.
     *
     * @param orderNo
     */
    void changeOrderToPaySuccess(long orderNo) throws OrderBaseException;

    /**
     * 确认订单(对象应该是已经检查过用户名的).
     *
     *
     * @param order 订单
     * @param operator
     * @throws OrderBaseException
     */
    void confirmSuccessOrder(Order order, String operator) throws OrderBaseException;

    /**
     * 确认订单.
     *
     * @param orderId 订单Id
     * @param operator
     * @throws OrderBaseException
     */
    Order confirmSuccessOrder(long orderId, String operator) throws OrderBaseException;

    /**
     * 更新订单的支付银行.
     *
     * @param orderNo
     * @param bank
     */
    void updateOrderPayBank(long orderNo, PayBank bank);

    /**
     * 审核已付款成功的订单.
     *
     * @param orderId 订单编号
     * @param operator 审核人(boss 系统对应的用户名)
     * @throws OrderBaseException
     */
    void approvalOrderOfPaySuccess(long orderId, String operator) throws OrderBaseException;

    /**
     * 审核货到付款的订单.
     *
     * @param orderId
     * @throws OrderBaseException
     */
    void approvalOrderOfCashOnDelivery(long orderId) throws OrderBaseException;

    /**
     * 打印订单.
     *
     *
     * @param orderId
     * @param supplierName
     * @throws OrderBaseException
     */
    void printOrderForSupplier(long orderId, String supplierName) throws OrderBaseException;

    /**
     * 将 已打印 的订单置回 已确认等待打印.
     *
     *
     * @param orderId
     * @param supplierName
     * @throws OrderBaseException
     */
    void turnBackOrderToConfirmForSupplier(long orderId, String supplierName) throws OrderBaseException;

    /**
     * 验证订单(验货).
     *
     *
     * @param orderId
     * @param supplierName
     * @throws OrderBaseException
     */
    void validateOrderForSupplier(long orderId, String supplierName) throws OrderBaseException;

    /**
     * 将 已验货 的订单置回 已打印等待验货.
     *
     *
     * @param orderId
     * @param supplierName
     * @throws OrderBaseException
     */
    void turnBackOrderToPrintForSupplier(long orderId, String supplierName) throws OrderBaseException;

    /**
     * 发货.
     *
     *
     * @param orderId
     * @param supplierName
     * @throws OrderBaseException
     */
    void deliveryOrderForSupplier(long orderId, String supplierName) throws OrderBaseException;

    /**
     * 退货单操作完成后, 更新订单项及订单状态.
     *
     * @param backGoods
     * @throws OrderBaseException
     */
    void triggerOperaOrderWhenBackSuccess(BackGoods backGoods) throws OrderBaseException;

    /**
     * 创建订单项.
     *
     * @param orderItem
     */
    void createOrderItem(OrderItem orderItem);

    /**
     * 更新订单项状态
     *
     * @param orderItemId   订单项 Id
     * @param orderState    要更改的订单项状态
     * @param previousState 必须的前置订单项状态
     * @return 若更新正常则返回 1
     */
    int updateOrderItemState(long orderItemId, OrderState orderState, OrderState previousState);

    /**
     * 修改订单项状态为 已评价
     *
     * @param orderItemId 订单项 Id
     * @return
     */
    int updateOrderItemAppraise(long orderItemId);

    /**
     * 更新订单的发票信息.
     *
     * @param orderId
     * @param invoiceInfo
     */
    void updateOrderInvoiceInfoRedundancy(long orderId, InvoiceInfo invoiceInfo);

    /**
     * 更新订单的物流快递公司
     *
     * @param orderId
     * @param type
     */
    void updateOrderDeliveryType(long orderId, DeliveryInfo.DeliveryType type);

    /**
     * 修改订单备注(每次修改都是添加记录).
     *
     * @param orderMessage
     */
    void updateOrderMessage(OrderMessage orderMessage);

    /**
     * 新增销售数据.
     *
     * @param skuTradeResult
     */
    public void insertSkuTradeResult(SkuTradeResult skuTradeResult);

    /**
     * 更新销售数据.
     *
     * @param skuTradeResult
     */
    public void updateSkuTradeResult(SkuTradeResult skuTradeResult);

    /**
     * 将订单置为刷单.
     *
     * @param orderId
     */
    void brushOrder(long orderId);
}
