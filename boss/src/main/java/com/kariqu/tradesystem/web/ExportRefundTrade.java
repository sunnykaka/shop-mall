package com.kariqu.tradesystem.web;

/**
 * Created with IntelliJ IDEA.
 * User: Json.zhu
 * Date: 13-12-13
 * Time: 上午11:29
 */
public class ExportRefundTrade {
    //退款批次号
    private String batchNo;
    //退款的流水号
    private String outerTradeNo;
    //本次退款实际的退款
    private String realRefundTotalFee;
    //退款的id
    private String backGoodsId;
    //订单号
    private String orderNo;
    //订单项
    private String skuName;
    //单价
    private String unitPrice;
    //退货数量
    private String refundSkuNum;
    //订单退款金额
    private String backPrice;
    //购买数量
    private String skuNum;
    //订单价格说明
    private String priceMessageDetail;
    //退款原因
    private String backReason;
    //第一层的合并
    private int countFirst;
    //第二层的合并
    private int countSecond;


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


    public String getBackPrice() {
        return backPrice;
    }

    public void setBackPrice(String backPrice) {
        this.backPrice = backPrice;
    }

    public String getRefundSkuNum() {
        return refundSkuNum;
    }

    public void setRefundSkuNum(String refundSkuNum) {
        this.refundSkuNum = refundSkuNum;
    }

    public String getBackGoodsId() {
        return backGoodsId;
    }

    public void setBackGoodsId(String backGoodsId) {
        this.backGoodsId = backGoodsId;
    }

    public String getRealRefundTotalFee() {
        return realRefundTotalFee;
    }

    public void setRealRefundTotalFee(String realRefundTotalFee) {
        this.realRefundTotalFee = realRefundTotalFee;
    }

    public String getOuterTradeNo() {
        return outerTradeNo;
    }

    public void setOuterTradeNo(String outerTradeNo) {
        this.outerTradeNo = outerTradeNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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


    public String getPriceMessageDetail() {
        return priceMessageDetail;
    }

    public void setPriceMessageDetail(String priceMessageDetail) {
        this.priceMessageDetail = priceMessageDetail;
    }

    public String getBackReason() {
        return backReason;
    }

    public void setBackReason(String backReason) {
        this.backReason = backReason;
    }
}
