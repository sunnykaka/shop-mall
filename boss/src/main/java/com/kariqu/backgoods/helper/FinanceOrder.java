package com.kariqu.backgoods.helper;

/**
 * 财务操作的订单在表现层的对象
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-28
 *        Time: 上午11:51
 */
public class FinanceOrder {

    /**
     * 退单号
     */
    private long id;

    /**
     * 订单号
     */
    private long orderNo;

    /**
     * 订单ID号
     */
    private long orderId;

    /**
     * 订单状态
     */
    private String orderState;

    /**
     * 订单状态描述
     */
    private String orderStateDesc;


    /**
     * 退单状态
     */
    private String backGoodsState;

    /**
     * 退单状态描述
     */
    private String backGoodsStateDesc;

    /** 退货人 */
    private String userName;

    /** 退货人号码 */
    private String backPhone;

    /**
     * 退款金额
     */
    private String price;

    /**
     * 退款银行
     */
    private String paybank;

    /**
     * 物流编号
     */
    private String waybillNumber;

    /**
     * 理由
     */
    private String backReason;

    /**
     * 质量问题  非质量问题
     */
    private String backReasonReal;

    /**
     * 退款日期
     */
    private String backDate;


    /**
     * 第三方交易号，可通过这个退款
     */
    private String outerTradeNo;

    /**
     * 是否有附件
     */
    private boolean hasAttach;

    private String uploadFiles;

    private boolean invoice;
    /**
     * 标志是否可以退款
     */
    private boolean canRefund;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBackPhone() {
        return backPhone;
    }

    public void setBackPhone(String backPhone) {
        this.backPhone = backPhone;
    }

    public String getPrice() {
        return price;
    }

    public String getUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(String uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPaybank() {
        return paybank;
    }

    public void setPaybank(String paybank) {
        this.paybank = paybank;
    }

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public String getBackReasonReal() {
        return backReasonReal;
    }

    public void setBackReasonReal(String backReasonReal) {
        this.backReasonReal = backReasonReal;
    }

    public String getOuterTradeNo() {
        return outerTradeNo;
    }

    public void setOuterTradeNo(String outerTradeNo) {
        this.outerTradeNo = outerTradeNo;
    }

    public String getBackReason() {
        return backReason;
    }

    public void setBackReason(String backReason) {
        this.backReason = backReason;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getBackGoodsState() {
        return backGoodsState;
    }

    public void setBackGoodsState(String backGoodsState) {
        this.backGoodsState = backGoodsState;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setInvoice(boolean invoice) {
        this.invoice = invoice;
    }

    public boolean isInvoice() {
        return invoice;
    }

    public String getBackDate() {
        return backDate;
    }

    public void setBackDate(String backDate) {
        this.backDate = backDate;
    }

    public boolean isHasAttach() {
        return hasAttach;
    }

    public void setHasAttach(boolean hasAttach) {
        this.hasAttach = hasAttach;
    }

    public String getOrderStateDesc() {
        return orderStateDesc;
    }

    public void setOrderStateDesc(String orderStateDesc) {
        this.orderStateDesc = orderStateDesc;
    }

    public String getBackGoodsStateDesc() {
        return backGoodsStateDesc;
    }

    public boolean isCanRefund() {
        return canRefund;
    }

    public void setCanRefund(boolean canRefund) {
        this.canRefund = canRefund;
    }

    public void setBackGoodsStateDesc(String backGoodsStateDesc) {
        this.backGoodsStateDesc = backGoodsStateDesc;
    }


}
