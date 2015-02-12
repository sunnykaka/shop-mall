package com.kariqu.suppliersystem.orderManager.vo;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-3-6
 * Time: 上午9:38
 */
public class OrderRemark {

    private String userRemark;
    private String customerServiceRemark;

    public String getCustomerServiceRemark() {
        return customerServiceRemark;
    }

    public void setCustomerServiceRemark(String customerServiceRemark) {
        this.customerServiceRemark = customerServiceRemark;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }
}
