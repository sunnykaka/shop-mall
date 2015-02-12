package com.kariqu.tradecenter.service;

import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;

/**
 * 货到付款.
 *
 * @author Athens(刘杰)
 * @Time 2013-03-25 17:43
 * @since 1.0.0
 */
public interface CashOnDeliveryService {

    /**
     * 检查订单是否支持货到付款.
     *
     * @param order
     * @throws OrderNoTransactionalException
     */
    void checkCashOnDelivery(Order order) throws OrderNoTransactionalException;

}
