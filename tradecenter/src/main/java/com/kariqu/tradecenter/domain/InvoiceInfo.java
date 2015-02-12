package com.kariqu.tradecenter.domain;

import com.kariqu.common.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 发票信息
 * User: Asion
 * Date: 11-10-11
 * Time: 下午1:03
 */
public class InvoiceInfo {

    //是否开发票
    private boolean invoice;

    /**
     * 发票类型，比如普通发票，增值税
     */
    private String invoiceType;

    /**
     * 抬头，个人或者单位
     */
    private InvoiceTitle invoiceTitle;

    /**
     * 单位名字
     */
    private String companyName;


    /**
     * 发票内容，明细，办公用品等
     */
    private String invoiceContent;

    /**
     * 客服修改发票信息冗余
     */
    private String invoiceTypeRewrite;
    private InvoiceTitle invoiceTitleRewrite;
    private String invoiceContentRewrite;
    private String companyNameRewrite;
    private String editor;
    private Date timeRewrite;

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public InvoiceTitle getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(InvoiceTitle invoiceTitle) {
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

    public boolean isInvoice() {
        return invoice;
    }

    public void setInvoice(boolean invoice) {
        this.invoice = invoice;
    }

    public static enum InvoiceTitle {

        individual,

        company;

        private static Map<InvoiceTitle, String> mapping = new HashMap<InvoiceTitle, String>();

        static {
            mapping.put(individual, "个人");
            mapping.put(company, "公司");

        }

        public String toDesc() {
            return mapping.get(this);
        }

    }

    public String getInvoiceTypeRewrite() {
        return invoiceTypeRewrite;
    }

    public void setInvoiceTypeRewrite(String invoiceTypeRewrite) {
        this.invoiceTypeRewrite = invoiceTypeRewrite;
    }

    public InvoiceTitle getInvoiceTitleRewrite() {
        return invoiceTitleRewrite;
    }

    public void setInvoiceTitleRewrite(InvoiceTitle invoiceTitleRewrite) {
        this.invoiceTitleRewrite = invoiceTitleRewrite;
    }

    public String getInvoiceContentRewrite() {
        return invoiceContentRewrite;
    }

    public void setInvoiceContentRewrite(String invoiceContentRewrite) {
        this.invoiceContentRewrite = invoiceContentRewrite;
    }

    public String getCompanyNameRewrite() {
        return companyNameRewrite;
    }

    public void setCompanyNameRewrite(String companyNameRewrite) {
        this.companyNameRewrite = companyNameRewrite;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Date getTimeRewrite() {
        return timeRewrite;
    }

    public void setTimeRewrite(Date timeRewrite) {
        this.timeRewrite = timeRewrite;
    }

}
