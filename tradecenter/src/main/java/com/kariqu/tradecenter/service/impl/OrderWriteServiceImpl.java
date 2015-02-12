package com.kariqu.tradecenter.service.impl;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.domain.payment.SkuTradeResult;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.tradecenter.excepiton.OrderTransactionalException;
import com.kariqu.tradecenter.helper.OrderAssert;
import com.kariqu.tradecenter.repository.OrderRepository;
import com.kariqu.tradecenter.repository.SkuTradeResultRepository;
import com.kariqu.tradecenter.service.*;
import com.kariqu.usercenter.service.UserPointService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Tiger
 * @version 1.0
 * @since 13-1-10 下午4:32
 */
public class OrderWriteServiceImpl implements OrderWriteService {

    private static final Logger LOGGER = Logger.getLogger(OrderWriteServiceImpl.class);

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private BackGoodsQueryService backGoodsQueryService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private OrderRepository orderRepository;

    /** 交易完成后记录商品销售数 */
    @Autowired
    private SkuTradeResultRepository skuTradeResultRepository;

    @Override
    @Transactional
    public void createNewOrder(Order order, String messageInfo) {
        // 若使用了现金券并在平摊回订单项后的总价为 0, 则直接将订单状态置为 付款完成
        long total = Money.YuanToCent(order.getTotalPrice());
        if (total == 0)
            // 支付成功
            order.setOrderState(OrderState.Pay);
        else
            // 创建订单
            order.setOrderState(OrderState.Create);

        OrderAssert.assertOrder(order);
        orderRepository.createOrder(order);

        // 操作库存及创建订单项
        order.createOrderItemsAndOperateStorage(this, skuService, orderQueryService);

        // 物流信息
        Logistics logistics = order.getLogistics();
        logistics.setOrderId(order.getId());
        logisticsService.createLogistics(logistics);

        if (StringUtils.isNotBlank(messageInfo)) {
            // 用户留言信息
            updateOrderMessage(new OrderMessage(order, OrderMessage.UserType.User, messageInfo));
        }

        // 记录创建历史
        order.setOrderState(OrderState.Create);
        createOrderStateHistory(new OrderStateHistory(order));
        if (total == 0) {
            // 记录支付历史
            order.setOrderState(OrderState.Pay);
            createOrderStateHistory(new OrderStateHistory(order));
        }
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void cancelOrder(Order order) throws OrderBaseException {
        cancelOrCloseOrder(order, OrderState.Cancel, order.getUserName());
    }

    private void cancelOrCloseOrder(Order order, OrderState newState, String operator) throws OrderBaseException {
        if (order.getOrderState().checkCanNotCancel()) {
            throw new OrderNoTransactionalException("订单[" + order.getOrderNo() + "]不能" + (newState == OrderState.Cancel ? "取消" : "关闭") + "!");
        }

        OrderState oldState = order.getOrderState();
        order.setMustPreviousState(OrderState.Create);
        order.setOrderState(newState);
        if (updateOrderStateByStrictState(order.getId(), order.getOrderState(), order.getMustPreviousState()) != 1) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("订单[" + order.getOrderNo() + "]不能从当前状态[" + oldState.serviceDesc() + "]更新为["
                        + order.getOrderState().serviceDesc() + "](只能从[" + order.getMustPreviousState().serviceDesc() + "]变更)");
            }

            throw new OrderTransactionalException("订单[" + order.getOrderNo() + "]不能从当前状态[" + oldState.serviceDesc() + "]更新为["
                    + order.getOrderState().serviceDesc() + "](只能从[" + order.getMustPreviousState().serviceDesc() + "]变更)");
        }

        order.cancelOrCloseOrderItems(this, skuService);
        createOrderStateHistory(new OrderStateHistory(order, operator, order.getOrderState().serviceDesc(), ""));

        // 回加积分, 并让现金券可用
        order.backToCouponAndIntegral(couponService, userPointService, orderQueryService);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void closeOrder(long orderId, String operator) throws OrderBaseException {
        Order order = checkOrder(orderId);

        cancelOrCloseOrder(order, OrderState.Close, operator);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("系统取消订单[" + order.getOrderNo() + "](此时订单状态[" + order.getOrderState().serviceDesc() + "])");
        }
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void changeOrderToPaySuccess(long orderNo) throws OrderBaseException {
        Order order = orderQueryService.getOrderByOrderNo(orderNo);
        if (order == null) {
            throw new OrderNoTransactionalException("没有此订单[" + orderNo + "]");
        }

        OrderState oldState = order.getOrderState();
        // 订单状态只能从 创建 到 付款成功
        order.setMustPreviousState(OrderState.Create);
        order.setOrderState(OrderState.Pay);
        if (updateOrderStateByStrictState(order.getId(), order.getOrderState(), order.getMustPreviousState()) != 1) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("订单[" + orderNo + "]不能从当前状态[" + oldState.serviceDesc() + "]更新为["
                        + order.getOrderState().serviceDesc() + "](只能从[" + order.getMustPreviousState().serviceDesc() + "]变更)");
            }

            throw new OrderTransactionalException("订单[" + orderNo + "]不能从当前状态[" + oldState.serviceDesc() + "]更新为["
                    + order.getOrderState().serviceDesc() + "](只能从[" + order.getMustPreviousState().serviceDesc() + "]变更)");
        }

        // 更新订单项信息, 且操作库存
        order.payOrderItems(this, skuService, orderQueryService);
        createOrderStateHistory(new OrderStateHistory(order));
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void confirmSuccessOrder(Order order, String operator) throws OrderBaseException {
        if (order.getOrderState().checkCanNotConfirmSuccess()) {
            throw new OrderNoTransactionalException("订单[" + order.getOrderNo() + "], 状态[" + order.getOrderState().serviceDesc() + "]不能确认完成!");
        }

        OrderState oldState = order.getOrderState();
        order.setMustPreviousState(OrderState.Send);
        order.setOrderState(OrderState.Success);
        if (updateOrderStateByStrictState(order.getId(), order.getOrderState(), order.getMustPreviousState()) != 1) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("订单[" + order.getOrderNo() + "]不能从当前状态[" + oldState.serviceDesc() + "]更新为["
                        + order.getOrderState().serviceDesc() + "](只能从[" + order.getMustPreviousState().serviceDesc() + "]变更)");
            }

            throw new OrderTransactionalException("订单[" + order.getOrderNo() + "]不能从当前状态[" + oldState.serviceDesc() + "]更新为["
                    + order.getOrderState().serviceDesc() + "](只能从[" + order.getMustPreviousState().serviceDesc() + "]变更)");
        }

        order.successOrderItems(this, orderQueryService);
        createOrderStateHistory(new OrderStateHistory(order, operator, order.getOrderState().serviceDesc(), ""));
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public Order confirmSuccessOrder(long orderId, String operator) throws OrderBaseException {
        Order order = checkOrder(orderId);
        confirmSuccessOrder(order, operator);
        return order;
    }

    @Override
    @Transactional
    public void updateOrderPayBank(long orderNo, PayBank bank) {
        orderRepository.updateOrderPayBank(orderNo, bank);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void approvalOrderOfPaySuccess(long orderId, String operator) throws OrderBaseException {
        operateOrderState(orderId, OrderState.Confirm, OrderState.Pay, operator, true);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void approvalOrderOfCashOnDelivery(long orderId) throws OrderBaseException {
        Order order = checkOrder(orderId);

        if (order.getPayType() == PayType.OnLine) {
            throw new OrderNoTransactionalException("订单[" + order.getOrderNo() + "]是非货到付款, 不能操作此步骤!");
        }

        OrderState oldState = order.getOrderState();
        // 从 创建 到 确认
        order.setMustPreviousState(OrderState.Create);
        order.setOrderState(OrderState.Confirm);
        if (orderRepository.updateOrderStateToConfirm(order.getId()) != 1) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("订单[" + order.getOrderNo() + "]不能从当前状态[" + oldState.serviceDesc() + "]更新为["
                        + order.getOrderState().serviceDesc() + "](只能从[" + order.getMustPreviousState().serviceDesc() + "]变更)");
            }

            throw new OrderTransactionalException("订单[" + order.getOrderNo() + "]不能从当前状态[" + oldState.serviceDesc() + "]更新为["
                    + order.getOrderState().serviceDesc() + "](只能从[" + order.getMustPreviousState().serviceDesc() + "]变更)");
        }

        // 更新订单
        order.updateOrderItemsState(this);
        orderRepository.createOrderStateHistory(new OrderStateHistory(order, order.getOrderState().serviceDesc()));
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void printOrderForSupplier(long orderId, String supplierName) throws OrderBaseException {
        operateOrderState(orderId, OrderState.Print, OrderState.Confirm, supplierName, true);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void turnBackOrderToConfirmForSupplier(long orderId, String supplierName) throws OrderBaseException {
        OrderState backState = OrderState.Print;
        operateOrderState(orderId, OrderState.Confirm, backState, supplierName, false);
        // 将 已打印的订单历史 置为 用户不可见
        updateOrderStateHistoryToVisibleWithUser(orderId, backState);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void validateOrderForSupplier(long orderId, String supplierName) throws OrderBaseException {
        operateOrderState(orderId, OrderState.Verify, OrderState.Print, supplierName, true);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void turnBackOrderToPrintForSupplier(long orderId, String supplierName) throws OrderBaseException {
        OrderState backState = OrderState.Verify;
        operateOrderState(orderId, OrderState.Print, backState, supplierName, false);
        // 将 已验货的订单历史 置为 用户不可见
        updateOrderStateHistoryToVisibleWithUser(orderId, backState);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void deliveryOrderForSupplier(long orderId, String supplierName) throws OrderBaseException {
        operateOrderState(orderId, OrderState.Send, OrderState.Verify, supplierName, true);
    }

    /**
     * 更新订单状态
     *
     * @param orderId 订单Id
     * @param orderState 订单更新后的状态
     * @param mustPreviousState 必须的前置状态
     * @param operator 操作人
     * @param createHistory 是否需要生成日志(商家中往回置时不需要生成)
     * @throws OrderBaseException
     */
    private void operateOrderState(long orderId, OrderState orderState, OrderState mustPreviousState,
                                   String operator, boolean createHistory) throws OrderBaseException {
        // 基本检查 和 退货检查
        Order order = checkOrder(orderId);
        checkBackWhenOperateOrder(order);

        OrderState oldState = order.getOrderState();
        order.setMustPreviousState(mustPreviousState);
        order.setOrderState(orderState);
        if (updateOrderStateByStrictState(order.getId(), order.getOrderState(), order.getMustPreviousState()) !=  1) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("订单[" + order.getOrderNo() + "]不能从当前状态[" + oldState.serviceDesc() + "]更新为["
                        + order.getOrderState().serviceDesc() + "](只能从[" + order.getMustPreviousState().serviceDesc() + "]变更)");
            }

            throw new OrderTransactionalException("订单[" + order.getOrderNo() + "]不能从当前状态[" + oldState.serviceDesc() + "]更新为["
                    + order.getOrderState().serviceDesc() + "](只能从[" + order.getMustPreviousState().serviceDesc() + "]变更)");
        }
        order.updateOrderItemsState(this);
        if (createHistory)
            createOrderStateHistory(new OrderStateHistory(order, operator, order.getOrderState().serviceDesc(), ""));
    }

    /**
     * 在客服及商家操作订单时, 检查是否有退货. 只在商品已经被全部退完时才抛出异常
     *
     * @param order 包含订单项数据
     * @throws OrderNoTransactionalException
     */
    private void checkBackWhenOperateOrder(Order order) throws OrderNoTransactionalException {
        int count = 0;
        // 一个订单可以退多次
        for (BackGoods backGoods : backGoodsQueryService.queryBackGoodsByOrderId(order.getId())) {
            // 一个退单可以有多个退单项
            List<BackGoodsItem> backGoodsItems = backGoodsQueryService.queryBackGoodsItemByBackGoodsId(backGoods.getId());
            for (BackGoodsItem backGoodsItem : backGoodsItems) {
                OrderItem orderItem = order.getOrderItem(backGoodsItem.getOrderItemId());
                // 退货单的退货数量跟下单时的数据比对, 相等则记录
                if (orderItem != null && orderItem.getNumber() == backGoodsItem.getNumber()) {
                    count++;
                }
            }
        }
        // 如果退单项已经全退了, 则不应该再操作状态. 如果只退了部分, 商家操作时需要注意数据
        if (count == order.getOrderItemList().size()) {
            throw new OrderNoTransactionalException("订单[" + order.getOrderNo() + "]已经全部被退货, 不需要再操作其状态!");
        }
    }

    private Order checkOrder(long orderId) throws OrderNoTransactionalException {
        Order order = orderQueryService.getDetailsOrder(orderId);
        if (order == null) {
            throw new OrderNoTransactionalException("没有此订单");
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void triggerOperaOrderWhenBackSuccess(BackGoods backGoods) throws OrderTransactionalException {
        // 成功后触发更新订单相关状态
        Order order = orderQueryService.getSimpleOrder(backGoods.getOrderId());
        if (order == null) {
            LOGGER.error("退货单[" + backGoods.getId() + "]对应的订单[" + backGoods.getOrderNo()
                    + "]不存在(退款成功后修改订单状态时)!");
            throw new OrderTransactionalException("退货单对应的订单不存在.");
        }

        if (order.getOrderState().checkCanNotBack() || Money.YuanToCent(order.getTotalPrice()) == 0) {
            throw new OrderTransactionalException("订单[" + order.getOrderNo() + "], 状态[" + order.getOrderState().serviceDesc() + "]不能退货!");
        }

        List<BackGoodsItem> backGoodsItemList = backGoodsQueryService.queryBackGoodsItemByBackGoodsId(backGoods.getId());
        for (BackGoodsItem backGoodsItem : backGoodsItemList) {
            OrderItem orderItem = orderQueryService.queryOrderItemsById(backGoodsItem.getOrderItemId());
            // 如果是全部退掉则订单项状态置为 已退货
            if (backGoodsItem.getNumber() == orderItem.getNumber()) {
                orderItem.setOrderState(OrderState.Back);
                orderRepository.updateOrderItem(orderItem);
            }
            // 若商品未发货, 回加库存
            if (backGoods.getBackType() == BackGoodsState.BackGoodsType.NoSend) {
                skuService.addSkuStock(orderItem.getSkuId(), orderItem.getStorageId(), backGoodsItem.getNumber());
            }

            try {
                // 统计退货量
                orderItem.recordBackNumber(backGoodsItem.getNumber(), this, orderQueryService);
            } catch (Exception e) {
                LOGGER.error("附加(skuId:" + orderItem.getSkuId() + ", productId:" + orderItem.getProductId() + ")退货数量(" + orderItem.getNumber() + ")时异常:", e);
            }

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("订单[" + order.getOrderNo() + "], 项[" + orderItem.getSkuName() + "], 退货数量["
                        + backGoodsItem.getNumber() + "], 购买数量[" + orderItem.getNumber() + "]");
            }
        }

        // 当订单项全部退完的时候, 将订单状态置为退货, 并记录历史.
        int count = orderRepository.queryOrderItemCountByOrderId(order.getId());
        int backCount = orderRepository.queryOrderItemCountByOrderIdAndState(order.getId(), OrderState.Back);
        if (count == backCount) {
            order.setOrderState(OrderState.Back);
            orderRepository.updateOrder(order);
            createOrderStateHistory(new OrderStateHistory(order, backGoods.getUserName(), "已退货", ""));
        }
    }

    private int updateOrderStateByStrictState(long orderId, OrderState orderState, OrderState previousState) {
        return orderRepository.updateOrderState(orderId, orderState, previousState);
    }

    private void createOrderStateHistory(OrderStateHistory history) {
        orderRepository.createOrderStateHistory(history);
    }

    private void updateOrderStateHistoryToVisibleWithUser(long orderId, OrderState orderState) {
        orderRepository.updateOrderStateHistoryToVisibleWithUser(orderId, orderState);
    }

    @Override
    @Transactional
    public void createOrderItem(OrderItem orderItem) {
        orderRepository.createOrderItem(orderItem);
    }

    @Override
    @Transactional
    public int updateOrderItemState(long orderItemId, OrderState orderState, OrderState previousState) {
        return orderRepository.updateOrderItemState(orderItemId, orderState, previousState);
    }

    @Override
    @Transactional
    public int updateOrderItemAppraise(long orderItemId) {
        return orderRepository.updateOrderItemAppraise(orderItemId);
    }

    @Override
    @Transactional
    public void updateOrderInvoiceInfoRedundancy(long orderId, InvoiceInfo invoiceInfo) {
        orderRepository.updateOrderInvoiceInfoRedundancy(orderId, invoiceInfo);
    }

    @Override
    @Transactional
    public void updateOrderDeliveryType(long orderId, DeliveryInfo.DeliveryType type) {
        // 更新订单的物流公司信息
        logisticsService.updateLogisticsDeliveryType(orderId, type);
        orderRepository.updateOrderDeliveryType(orderId, type);
    }

    @Override
    @Transactional
    public void updateOrderMessage(OrderMessage orderMessage) {
        orderRepository.createOrderMessage(orderMessage);
    }

    @Override
    @Transactional
    public void insertSkuTradeResult(SkuTradeResult skuTradeResult) {
        skuTradeResultRepository.insert(skuTradeResult);
    }

    @Override
    @Transactional
    public void updateSkuTradeResult(SkuTradeResult skuTradeResult) {
        skuTradeResultRepository.update(skuTradeResult);
    }

    @Override
    @Transactional
    public void brushOrder(long orderId) {
        orderRepository.brush(orderId);
    }

}
