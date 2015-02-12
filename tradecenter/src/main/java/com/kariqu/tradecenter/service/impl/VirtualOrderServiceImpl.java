package com.kariqu.tradecenter.service.impl;

import com.kariqu.tradecenter.domain.PayBank;
import com.kariqu.tradecenter.domain.VirtualOrder;
import com.kariqu.tradecenter.repository.OrderRepository;
import com.kariqu.tradecenter.service.VirtualOrderService;
import com.kariqu.usercenter.domain.AccountType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-4
 *        Time: 下午2:04
 */
public class VirtualOrderServiceImpl implements VirtualOrderService {

    @Autowired
    private OrderRepository orderRepository;


    @Override
    public VirtualOrder createVirtualOrder(int userId, AccountType accountType, PayBank payBank) {
        VirtualOrder virtualOrder = new VirtualOrder();
        virtualOrder.setUserId(userId);
        virtualOrder.setAccountType(accountType);
        virtualOrder.setCreateDate(new Date());
        virtualOrder.setVirtualState(VirtualOrder.VirtualState.WaitingForPay);
        virtualOrder.setPayBank(payBank);
        orderRepository.createVirtualOrder(virtualOrder);
        return virtualOrder;
    }


}
