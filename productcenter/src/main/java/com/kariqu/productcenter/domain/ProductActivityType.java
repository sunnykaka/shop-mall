package com.kariqu.productcenter.domain;

/**
 * 商品活动类型
 * User: Asion
 * Date: 13-4-1
 * Time: 下午2:59
 */
public enum ProductActivityType {

    /** 不做任何活动 */
    Normal(""),

    /** 限时折扣 */
    LimitTime("杰迷专享"),

    /** 积分兑换 */
    IntegralConversion("积分兑换"),

    /** 积分 + 钱 */
    SuperConversion("积分优惠购");

    private String value;
    ProductActivityType(String value) {
        this.value = value;
    }

    public String toDesc() {
        return value;
    }

}