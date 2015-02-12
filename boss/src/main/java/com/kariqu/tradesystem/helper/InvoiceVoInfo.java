package com.kariqu.tradesystem.helper;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-28
 *        Time: 下午4:38
 */
public class InvoiceVoInfo {

    //订单编号
    private long orderId;

    //发票类型，比如普通发票，增值税
    private String invoiceType;

    // 抬头，个人或者单位
    private String invoiceTitle;

    // 单位名字
    private String companyName;

    // 发票内容，明细，办公用品等
    private String invoiceContent;

    private String orderState;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(String invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }
}
