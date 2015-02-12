package com.kariqu.buyer.web.controller.trade;

import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.service.impl.PayMethod;

import java.util.Date;
import java.util.List;

/**
 * 优惠券套餐
 * User: Alec
 * Date: 13-10-31
 * Time: 下午2:08
 */
public class CouponSet {
    private int id;
    private int userId;
    /**
     * 套餐类目目前只有A，B，C三个套餐
     */
    private CouponSetType setType;
    /**
     * 交易号
     */
    private String tradeNo;
    /**
     * 支付方式
     */
    private PayMethod payMethod;
    /**
     * 购买优惠券的总金额
     */
    private long totalPrice;
    /**
     * 套餐里面分配的优惠券Id
     */
    private boolean allocated;
    private Date createDate;
    private Date updateDate;
    private boolean isDelete;

    public CouponSet() {
    }

    public CouponSet(int userId, CouponSetType setType, String tradeNo, PayMethod payMethod) {
        this.userId = userId;
        this.setType = setType;
        this.tradeNo = tradeNo;
        this.payMethod = payMethod;
    }

    private List<Coupon> coupons;

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

    public CouponSetType getSetType() {
        return setType;
    }

    public void setSetType(CouponSetType setType) {
        this.setType = setType;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public PayMethod getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(PayMethod payMethod) {
        this.payMethod = payMethod;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }
}