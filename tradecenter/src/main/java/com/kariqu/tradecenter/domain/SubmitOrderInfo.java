package com.kariqu.tradecenter.domain;

import com.kariqu.usercenter.domain.AccountType;

import java.util.List;

/**
 * 提交订单时所需要的所有数据.
 *
 * @author Athens(刘杰)
 * @Time 2013-01-29 17:28
 * @since 1.0.0
 */
public class SubmitOrderInfo {

    /**
     * 用户Id
     */
    private int userId;

    /**
     * 用户类型
     */
    private AccountType accountType;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 订单项数据(只有 skuId 和 number)
     */
    private List<TradeItem> tradeItemList;

    /**
     * 物流信息
     */
    private Logistics logistics;

    /**
     * 发票信息(写进 Order 对象), 可能为空
     */
    private InvoiceInfo invoiceInfo;

    /**
     * 现金券
     */
    private Coupon coupon;

    /**
     * 付款方式(写进 Order 对象)
     */
    private PayBank payBank;

    /**
     * 付款类型(写进 Order 对象), 默认是在线支付
     */
    private PayType payType = PayType.OnLine;

    /**
     * 用户留言信息
     */
    private String messageInfo;

    /**
     * 积分数 最小积分单位
     */
    private long integral = 0;

    /**
     * 用户Id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * 用户Id
     */
    public void setUser(int userId) {
        this.userId = userId;
    }

    /**
     * 用户类型
     */
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * 用户类型
     */
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 物流信息
     */
    public Logistics getLogistics() {
        return logistics;
    }

    /**
     * 物流信息
     */
    public void setLogistics(Logistics logistics) {
        this.logistics = logistics;
    }

    /**
     * 发票信息(写进 Order 对象), 可能为空
     */
    public InvoiceInfo getInvoiceInfo() {
        return invoiceInfo;
    }

    /**
     * 发票信息(写进 Order 对象), 可能为空
     */
    public void setInvoiceInfo(InvoiceInfo invoiceInfo) {
        this.invoiceInfo = invoiceInfo;
    }

    /**
     * 现金券
     */
    public Coupon getCoupon() {
        return coupon;
    }

    /**
     * 现金券
     */
    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }


    /**
     * 付款方式(写进 Order 对象)
     */
    public PayBank getPayBank() {
        return payBank;
    }

    /**
     * 付款方式(写进 Order 对象)
     */
    public void setPayBank(PayBank payBank) {
        this.payBank = payBank;
    }

    /**
     * 付款类型(写进 Order 对象), 默认是在线支付
     */
    public PayType getPayType() {
        return payType;
    }

    /**
     * 付款类型(写进 Order 对象), 默认是在线支付
     */
    public void setPayType(PayType payType) {
        this.payType = payType;
    }

    /**
     * 用户留言信息
     */
    public String getMessageInfo() {
        return messageInfo;
    }

    /**
     * 用户留言信息
     */
    public void setMessageInfo(String messageInfo) {
        this.messageInfo = messageInfo;
    }

    /**
     * 积分数 最小积分单位
     */
    public long getIntegral() {
        return integral;
    }

    /**
     * 积分数 最小积分单位
     */
    public void setIntegral(long integral) {
        this.integral = integral;
    }

    public List<TradeItem> getTradeItemList() {
        return tradeItemList;
    }

    public void setTradeItemList(List<TradeItem> tradeItemList) {
        this.tradeItemList = tradeItemList;
    }
}
