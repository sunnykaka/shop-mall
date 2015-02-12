package com.kariqu.tradecenter.domain;

/**
 * @author Athens(刘杰)
 * @Time 2012-09-25 17:43
 * @since 1.0.0
 */
public class LogisticsInfo {

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 物流信息. {text 的值为 65535[(2 << 15) - 1] 个字符}
     */
    private String expressValue;

    /**
     * 状态(1表示配送完成, 0表示未完成). 配送完成后则不需要再去走第三方通道进行查询.
     */
    private int status;

    public LogisticsInfo() {
    }

    public LogisticsInfo(String expressNo, String expressValue, int status) {
        this.expressNo = expressNo;
        this.expressValue = expressValue;
        this.status = status;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getExpressValue() {
        return expressValue;
    }

    public void setExpressValue(String expressValue) {
        this.expressValue = expressValue;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toString() {
        return "[单号:" + expressNo + ", 信息:" + expressValue + ", 状态:" + ((status == 1) ? "成功" : "失败") + "]";
    }

}
