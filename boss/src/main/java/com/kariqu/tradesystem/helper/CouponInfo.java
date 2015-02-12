package com.kariqu.tradesystem.helper;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 13-5-7
 * Time: 下午12:06
 */
public class CouponInfo {
    private int id;

    /**
     * 现金券的编码
     */
    private String code;

    /**
     * 是否使用
     */
    private boolean used;


    /**
     * 是否已分发
     */
    private boolean publish;


    /**
     * 用户ID，可能为空
     */
    private String userName;

    /**
     * 订单号
     */
    private long orderNo;

    /**
     * 金额
     */
    private String price;

    /**
     * 能够使用的最少订单价格
     */
    private String miniApplyOrderPrice;


    /**
     * 现金券类型
     */
    private String couponType;


    private String createDate;

    private String updateDate;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 过期时间
     */
    private String expireDate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    public String getMiniApplyOrderPrice() {
        return miniApplyOrderPrice;
    }

    public void setMiniApplyOrderPrice(String miniApplyOrderPrice) {
        this.miniApplyOrderPrice = miniApplyOrderPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
