package com.kariqu.tradecenter.payment;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-10-31
 * Time: 下午5:49
 * To change this template use File | Settings | File Templates.
 */
public enum PayType {

    /**
     * 支付宝
     */
    Alipay("alipay"),
    /**
     * 财富通
     */
    TenPay("tenpay");

    private String value;

    PayType(String value) {
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
