package com.kariqu.tradesystem.web;

/**
 * Created with IntelliJ IDEA.
 * User: Json.zhu
 * Date: 13-12-11
 * Time: 下午2:54
 * 导出交易记录
 */
public class ExportTrade {

    /**交易号*/
    private String tradeNo;

    /** 流水号 */
    private String outerTradeNo;

    /** 支付方式  */
    private String payType;
    /** 业务类型，order/coupon*/
    private String bizType;
    /** 总金额*/
    private String tradeTotalFee;
    /**订单号 */
    private String orderNo;
    /** 付款金额*/
    private String orderTotalFee;
    /** 订单项 */
    private String skuName;
    /** 单价*/
    private String unitPrice;
    /** 数量*/
    private String skuNum;
    /** 应付金额 */
    private String skuTotalFee;
    /** 订单价格说明*/
    private String priceMessageDetail;
    /** 第一层的合并 */
    private int countFirst;
    /** 第二层的合并*/
    private int countSecond;

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public int getCountFirst() {
        return countFirst;
    }

    public void setCountFirst(int countFirst) {
        this.countFirst = countFirst;
    }

    public int getCountSecond() {
        return countSecond;
    }

    public void setCountSecond(int countSecond) {
        this.countSecond = countSecond;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOuterTradeNo() {
        return outerTradeNo;
    }

    public void setOuterTradeNo(String outerTradeNo) {
        this.outerTradeNo = outerTradeNo;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getTradeTotalFee() {
        return tradeTotalFee;
    }

    public void setTradeTotalFee(String tradeTotalFee) {
        this.tradeTotalFee = tradeTotalFee;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderTotalFee() {
        return orderTotalFee;
    }

    public void setOrderTotalFee(String orderTotalFee) {
        this.orderTotalFee = orderTotalFee;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(String skuNum) {
        this.skuNum = skuNum;
    }

    public String getSkuTotalFee() {
        return skuTotalFee;
    }

    public void setSkuTotalFee(String skuTotalFee) {
        this.skuTotalFee = skuTotalFee;
    }

    public String getPriceMessageDetail() {
        return priceMessageDetail;
    }

    public void setPriceMessageDetail(String priceMessageDetail) {
        this.priceMessageDetail = priceMessageDetail;
    }
}
