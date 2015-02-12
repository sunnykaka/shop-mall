package com.kariqu.tradecenter.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付类型
 * <p/>
 * 支付方式的不同决定订单的生命周期
 * 如果一个订单是在线付款，我们会设置一个付款期限，如果超过这个订单就要自动取消
 * User: Asion
 * Date: 12-6-7
 * Time: 下午4:20
 */
public enum PayType {

    /**
     * 货到付款，现金付款
     */
    OnDelivery_Cash,


    /**
     * POS机刷卡
     */
    OnDelivery_POS,


    /**
     * 在线付款
     */
    OnLine;

    private static Map<PayType, String> mapping = new HashMap<PayType, String>();

    static {
        mapping.put(OnDelivery_Cash, "现金付款");
        mapping.put(OnDelivery_POS, "POS机刷卡");
        mapping.put(OnLine, "在线付款");
    }

    public String toDesc() {
        return mapping.get(this);
    }
}
