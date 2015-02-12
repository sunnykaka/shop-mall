package com.kariqu.tradesystem.helper;

import com.kariqu.usercenter.domain.AccountType;

/**
 * User: wendy
 * Date: 12-6-28
 * Time: 下午5:05
 */
public class OrderDetail {

    //订单编号
    private long orderId;

    private String orderState;

    /**
     * 订单状态描述
     */
    private String orderStateDesc;

    //下单时间
    private String startDate;

    //结束时间
    private String endDate;

    private int userId;

    //下单用户
    private String userName;

    //收获人
    private String goodToUserName;


    //是否开发票
    private boolean invoice;

    //账户类型
    private AccountType accountType;

    private long orderNo;

    /**
     * 使用现金券或积分时记录其订单价格相关详细说明
     */
    private String priceMessageDetail;


    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }


    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public boolean isInvoice() {
        return invoice;
    }

    public void setInvoice(boolean invoice) {
        this.invoice = invoice;
    }

    public String getOrderStateDesc() {
        return orderStateDesc;
    }

    public void setOrderStateDesc(String orderStateDesc) {
        this.orderStateDesc = orderStateDesc;
    }

    public String getPriceMessageDetail() {
        return priceMessageDetail;
    }

    public void setPriceMessageDetail(String priceMessageDetail) {
        this.priceMessageDetail = priceMessageDetail;
    }

    public String getGoodToUserName() {
        return goodToUserName;
    }

    public void setGoodToUserName(String goodToUserName) {
        this.goodToUserName = goodToUserName;
    }
}
