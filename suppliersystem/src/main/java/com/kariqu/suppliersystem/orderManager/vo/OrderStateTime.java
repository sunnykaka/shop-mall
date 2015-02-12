package com.kariqu.suppliersystem.orderManager.vo;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-12-19
 * Time: 下午2:46
 */
public class OrderStateTime {
    //订单创建日期
    private String createTime;

    //订单付款日期
    private String payTime;

    //审核日期
    private String confirmTime;
    private String confirmOperator;

    //打印日期
    private String printTime;
    private String printOperator;

    //验货日期
    private String verifyTime;
    private String verifyOperator;

    //发货日期
    private String sendTime;
    private String sendOperator;

    //签收时间
    private String SuccessTime;

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getPrintTime() {
        return printTime;
    }

    public void setPrintTime(String printTime) {
        this.printTime = printTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSuccessTime() {
        return SuccessTime;
    }

    public void setSuccessTime(String successTime) {
        SuccessTime = successTime;
    }

    public String getConfirmOperator() {
        return confirmOperator;
    }

    public void setConfirmOperator(String confirmOperator) {
        this.confirmOperator = confirmOperator;
    }

    public String getPrintOperator() {
        return printOperator;
    }

    public void setPrintOperator(String printOperator) {
        this.printOperator = printOperator;
    }

    public String getSendOperator() {
        return sendOperator;
    }

    public void setSendOperator(String sendOperator) {
        this.sendOperator = sendOperator;
    }

    public String getVerifyOperator() {
        return verifyOperator;
    }

    public void setVerifyOperator(String verifyOperator) {
        this.verifyOperator = verifyOperator;
    }
}
