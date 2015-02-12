package com.kariqu.suppliersystem.orderManager.vo;

import com.kariqu.tradecenter.domain.InvoiceInfo;

/**
 * 发票信息
 *
 * @author:Wendy
 * @since:1.0.0 Date: 13-3-14
 * Time: 下午3:13
 */
public class InvoiceInformation {

    //是否开发票
    private boolean invoice;
    //发票类型，比如普通发票，增值税
    private String invoiceType;

    // 抬头，个人或者单位
    private InvoiceInfo.InvoiceTitle invoiceTitle;

    // 单位名字
    private String companyName;

    // 发票内容，明细，办公用品等
    private String invoiceContent;

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

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public InvoiceInfo.InvoiceTitle getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(InvoiceInfo.InvoiceTitle invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public boolean isInvoice() {
        return invoice;
    }

    public void setInvoice(boolean invoice) {
        this.invoice = invoice;
    }
}
