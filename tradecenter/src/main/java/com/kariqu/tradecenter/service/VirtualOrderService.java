package com.kariqu.tradecenter.service;

import com.kariqu.tradecenter.domain.PayBank;
import com.kariqu.tradecenter.domain.VirtualOrder;
import com.kariqu.usercenter.domain.AccountType;

/**
 * 虚拟订单服务
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-4
 *        Time: 下午2:02
 */

public interface VirtualOrderService {

    /**
     * 创建虚拟订单
     */
    VirtualOrder createVirtualOrder(int userId, AccountType accountType, PayBank payBank);

}
