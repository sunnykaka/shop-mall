package com.kariqu.tradecenter.service.impl;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Tiger
 * @version 1.0
 * @since 13-1-22 上午10:49
 */
public enum PayMethod {

    directPay("支付宝"),
    creditPay("信用卡"),
    bankPay("银行卡"),
    Tenpay("财付通");

    private String value;
    PayMethod(String value) {
        this.value = value;
    }
    public String toDesc() {
        return value;
    }
}
