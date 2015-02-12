package com.kariqu.tradecenter.domain.payment;

/**
 * sku交易记录
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-6
 *        Time: 上午11:24
 */
public class SkuTradeResult {

    private int id;

    private long skuId;

    private int productId;

    /**
     * 成交数量
     */
    private long number;

    /**
     * 退货数量
     */
    private long backNumber;

    /**
     * 付款成功数量
     */
    private long payNumber;

    public SkuTradeResult() {
    }

    public SkuTradeResult(long skuId, int productId) {
        this.skuId = skuId;
        this.productId = productId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    /**
     * 退货数量
     */
    public long getBackNumber() {
        return backNumber;
    }

    /**
     * 退货数量
     */
    public void setBackNumber(long backNumber) {
        this.backNumber = backNumber;
    }

    /**
     * 付款成功数量
     */
    public long getPayNumber() {
        return payNumber;
    }

    /**
     * 付款成功数量
     */
    public void setPayNumber(long payNumber) {
        this.payNumber = payNumber;
    }

    /**
     * 附加销售量
     */
    public void appendedNumber(int saleNumber) {
        number += saleNumber;
    }

    /**
     * 附加退货量
     */
    public void appendedBackNumber(int backNumber) {
        this.backNumber += backNumber;
    }

    /**
     * 附加付款成功量
     */
    public void appendedPayNumber(int payNumber) {
        this.payNumber += payNumber;
    }

}
