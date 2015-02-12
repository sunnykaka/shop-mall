package com.kariqu.tradecenter.domain.payment;

/**
 * 虚拟订单交易结果
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-6
 *        Time: 上午11:29
 */
public class VirtualOrderResult {

    private int id;

    private int virtualOrderId;

    private long tradePrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVirtualOrderId() {
        return virtualOrderId;
    }

    public void setVirtualOrderId(int virtualOrderId) {
        this.virtualOrderId = virtualOrderId;
    }

    public long getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(long tradePrice) {
        this.tradePrice = tradePrice;
    }
}
