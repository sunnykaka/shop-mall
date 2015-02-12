package com.kariqu.tradecenter.domain;

import com.kariqu.usercenter.domain.AccountType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 虚拟订单
 * 一个和用户关联的虚拟订单，一个虚拟订单对应多个具体商家和仓库的真实订单
 * User: Asion
 * Date: 12-6-25
 * Time: 上午11:49
 */
public class VirtualOrder {

    private long id;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 取消时间
     */
    private Date cancelDate;

    /**
     * 完成时间
     */
    private Date endDate;

    /**
     * 修改时间
     */
    private Date modifyDate;

    /**
     * 虚拟订单的状态
     */
    private VirtualState virtualState;


    //谁下的单
    private int userId;


    //订单总价
    private long totalPrice;


    //真正的支付价格，因为一次虚拟订单下的订单可能被部分取消
    private long payPrice;


    private PayBank payBank;


    //账户类型，可区别出来自什么网站，比如QQ，sina，KRQ代表我们自己
    private AccountType accountType;

    public Date getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public VirtualState getVirtualState() {
        return virtualState;
    }

    public void setVirtualState(VirtualState virtualState) {
        this.virtualState = virtualState;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public static enum VirtualState {

        /**
         * 等待付款
         */
        WaitingForPay,

        /**
         * 被取消
         */
        Cancel,

        /**
         * 付款完成
         */
        PayCompleted,


        /**
         * 商品出库
         */
        OnDelivery,


        /**
         * 订单完成
         */
        Complete;

        private static Map<VirtualState, String> mapping = new HashMap<VirtualState, String>();

        static {
            mapping.put(PayCompleted, "付款成功等待确认");
            mapping.put(Cancel, "被取消");
            mapping.put(WaitingForPay, "等待付款");
            mapping.put(OnDelivery, "商品出库");
            mapping.put(Complete, "完成");
        }

        public String toDesc() {
            return mapping.get(this);
        }

    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PayBank getPayBank() {
        return payBank;
    }

    public void setPayBank(PayBank payBank) {
        this.payBank = payBank;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public long getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(long payPrice) {
        this.payPrice = payPrice;
    }
}
