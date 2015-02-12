package com.kariqu.tradecenter.domain;

/**
 * 用户以单位名义开发票时常用的公司对象
 * User: Asion
 * Date: 12-6-4
 * Time: 下午4:08
 */
public class InvoiceCompany {

    private int id;

    private int userId;

    private String companyName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
