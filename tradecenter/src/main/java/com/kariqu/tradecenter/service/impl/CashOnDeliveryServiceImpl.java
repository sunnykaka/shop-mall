package com.kariqu.tradecenter.service.impl;

import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.tradecenter.service.CashOnDeliveryService;
import org.apache.log4j.Logger;

/**
 * @author Athens(刘杰)
 * @Time 2013-03-25 17:42
 * @since 1.0.0
 */
public class CashOnDeliveryServiceImpl implements CashOnDeliveryService {

    private static final Logger LOGGER = Logger.getLogger(CashOnDeliveryServiceImpl.class);

    @Override
    public void checkCashOnDelivery(Order order) throws OrderNoTransactionalException {
        // TODO 检查是否符合条件
    }

}
