package com.kariqu.tradecenter.domain.payment;

/**
 * 订单交易记录
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-6
 *        Time: 上午11:30
 */
public class OrderResult {

    private int id;

    private long orderNo;

    private long tradePrice;

    private int customerId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    public long getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(long tradePrice) {
        this.tradePrice = tradePrice;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
