package com.kariqu.tradecenter.domain;

/**
 * 银行
 * User: Alec
 * Date: 12-10-30
 * Time: 下午1:49
 * To change this template use File | Settings | File Templates.
 */
public enum PayBank {

    /**
     * 如果是货到付款，则没有支付银行
     */
    NOBank("不在线支付"),
    /**
     * 工商银行
     */
    ICBCB2C("中国工商银行"),
    /**
     * 交通银行
     */
    COMM("交通银行"),
    /**
     * 建设银行
     */
    CCB("中国建设银行"),
    /**
     * 农业银行
     */
    ABC("中国农业银行"),
    /**
     * 邮政储蓄
     */
    POSTGC("中国邮政储蓄银行"),
    /**
     * 招商银行
     */
    CMB("招商银行"),
    /**
     * 中国银行
     */
    BOCB2C("中国银行"),
    /**
     * 光大银行
     */
    CEBBANK("中国光大银行"),
    /**
     * 中信银行
     */
    CITIC("中信银行"),
    /**
     * 深发展银行
     */
    SDB("深圳发展银行"),
    /**
     * 浦发银行
     */
    SPDB("浦发银行"),
    /**
     * 民生银行
     */
    CMBC("民生银行"),
    /**
     * 兴业银行
     */
    CIB("兴业银行"),
    /**
     * 平安银行
     */
    SPABANK("平安银行"),
    /**
     * 广发银行
     */
    GDB("广发银行"),
    /**
     * 上海银行
     */
    SHBANK("上海银行"),
    /**
     * 宁波银行
     */
    NBBANK("宁波银行"),
    /**
     * 杭州银行
     */
    HZCBB2C("杭州银行"),
    /**
     * 北京银行
     */
    BJBANK("北京银行"),
    /**
     * 北京农商银行
     */
    BJRCB("北京农商银行"),

    /**
     * 支付宝
     */
    Alipay("支付宝"),

    /**
     * 财付通
     */
    Tenpay("财付通");

    private String value;

    PayBank(String value) {
        this.value = value;
    }

    public String toDesc() {
        return value;
    }


}
