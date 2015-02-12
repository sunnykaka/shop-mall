package com.kariqu.tradecenter.client.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.client.TradeCenterSupplierClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.excepiton.OrderTransactionalException;
import com.kariqu.tradecenter.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Tiger
 * @version 1.0
 * @since 13-1-25 上午10:10
 */
public class TradeCenterClientSupplierImpl implements TradeCenterSupplierClient {

    @Autowired
    private OrderWriteService orderWriteService;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private BackGoodsQueryService backGoodsQueryService;

    @Autowired
    private OperateLogisticsService operateLogisticsService;

    @Override
    @Transactional
    public void updateLogistics(Logistics logistics) {
        logisticsService.updateLogistics(logistics);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void printOrderForSupplier(long orderId, String supplierName) throws OrderBaseException {
        orderWriteService.printOrderForSupplier(orderId, supplierName);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void turnBackOrderToConfirmForSupplier(long orderId, String supplierName) throws OrderBaseException {
        orderWriteService.turnBackOrderToConfirmForSupplier(orderId, supplierName);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void validateOrderForSupplier(long orderId, String supplierName) throws OrderBaseException {
        orderWriteService.validateOrderForSupplier(orderId, supplierName);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void turnBackOrderToPrintForSupplier(long orderId, String supplierName) throws OrderBaseException {
        orderWriteService.turnBackOrderToPrintForSupplier(orderId, supplierName);
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void deliveryOrderForSupplier(long orderId, String supplierName) throws OrderBaseException {
        orderWriteService.deliveryOrderForSupplier(orderId, supplierName);
    }


    @Override
    public LogisticsRedundancy queryLogisticsRedundancy(long logisticsId) {
        return logisticsService.queryLogisticsRedundancy(logisticsId);
    }

    @Override
    public OrderMessage queryUserMessage(long orderId) {
        return orderQueryService.queryUserOrderMessage(orderId);
    }

    @Override
    public OrderMessage queryServerMessage(long orderId) {
        return orderQueryService.queryServerOrderMessage(orderId);
    }

    @Override
    public Logistics getLogisticsByOrderId(long orderId) {
        return logisticsService.getLogisticsByOrderId(orderId);
    }

    @Override
    public OrderStateHistory queryHistoryByState(long orderId, OrderState orderState) {
        return orderQueryService.queryHistoryByState(orderId, orderState);
    }

    @Override
    public long getOrderIdByExpressNo(String expressNo) {
        return logisticsService.getOrderIdByLogisticsId(expressNo);
    }

    @Override
    public List<Long> getOrderIdsByExpressNo(String expressNo) {
        return logisticsService.getOrderIdsByLogisticsId(expressNo);
    }

    @Override
    public Order getSimpleOrder(long orderId) {
        return orderQueryService.getSimpleOrder(orderId);
    }

    @Override
    public BackGoodsLog queryBackGoodsLogByState(long orderId) {
        return backGoodsQueryService.queryRecentBackGoodsLogByOrderId(orderId);
    }

    @Override
    public Page<Order> searchQuery(Query query, int customerId, Page<Order> orderPage) {
        return orderQueryService.searchQuery(query, customerId, orderPage);
    }

    @Override
    public List<Order> searchQueryList(Query query, int customerId) {
        return orderQueryService.searchQueryList(query, customerId);
    }

    @Override
    public void queryThirdLogisticsInfo(String company, String number, String from, String to) {
        operateLogisticsService.handleThirdLogisticsInfo(company, number, from, to);
    }


    // ============================ common ============================
    @Override
    public List<OrderItem> queryOrderItemWithoutBackingNumberByOrderId(long orderId) {
        return orderQueryService.queryRealOrderItemNumberByOrderId(orderId);
    }

    @Override
    public InvoiceInfo queryOrderInvoiceInfoRedundancy(long orderId) {
        return orderQueryService.queryOrderInvoiceInfoRedundancy(orderId);
    }

}
