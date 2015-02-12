package com.kariqu.productcenter.domain;

import java.util.Arrays;
import java.util.List;

/**
 * @author: enoch
 * @since 1.0.0
 *        Date: 12-8-28
 *        Time: 上午10:24
 */
public enum ConsultationCategory {

    /** 全部咨询 */
    all("全部咨询"),

    /** 商品咨询 */
    product("商品咨询"),

    /** 支付咨询 */
    pay("支付问题"),

    /** 发票咨询 */
    invoice("发票及保修"),

    /** 促销赠品 */
    sales("促销及赠品"),

    /** 库存和配送咨询 */
    stock("库存及配送");

    private String value;
    ConsultationCategory(String value) {
        this.value = value;
    }

    public String toDesc() {
        return value;
    }

    public boolean queryAll() {
        return this == all;
    }

    public static List<ConsultationCategory> iter() {
        return Arrays.asList(product, pay, invoice, sales, stock);
    }

}
