package com.kariqu.suppliercenter.service;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-4-28
 * Time: 下午1:54
 */
public class SupplierLogQuery {
    private String content;

    private int supplierId;

    private int start;

    private int limit;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
