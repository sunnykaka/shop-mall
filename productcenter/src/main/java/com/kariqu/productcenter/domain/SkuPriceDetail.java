package com.kariqu.productcenter.domain;

import java.util.Date;

/**
 * SKU 价格详情
 *
 * @author Athens(刘杰)
 * @Time 2013-04-02 14:29
 * @since 1.0.0
 */
public class SkuPriceDetail {

    private long skuId;

    /** 原始价格 */
    private String originalPrice;

    /** 市场价格 */
    private String marketPrice;

    // ============== 活动相关 ==============
    /** 是否参加活动(true 表示有参加, 默认是 false) */
    private boolean activity = false;

    /** 活动Id */
    private long marketingId;

    /** 参加的活动类型 */
    private ProductActivityType activityType = ProductActivityType.Normal;

    /** 参加的活动价格 */
    private String activityPrice;

    /**参加活动所需要的积分*/
    private Long activityIntegral;

    /**此字段专用积分兑换、积分优惠购活动*/
    private int userBuyCount;

    /**此字段专用积分兑换、积分优惠购活动，是否参加活动(true 表示有参加, 默认是 false)，用来表示是否有活动价格*/
    private Boolean isActivity = false;

    /** 活动开始时间 */
    private Date activityStartDate;

    /** 活动结束时间 */
    private Date activityEndDate;

    /** 字符串显示结束时间(若大于1天则显示天, 否则显示到分) */
    private String activityDateStr;
    // ============== 活动相关 ==============

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    /** 原始价格 */
    public String getOriginalPrice() {
        return originalPrice;
    }

    /** 原始价格 */
    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    /** 市场价格 */
    public String getMarketPrice() {
        return marketPrice;
    }

    /** 市场价格 */
    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    /** 是否参加活动(true 表示有参加, 默认是 false) */
    public boolean isActivity() {
        return activity;
    }

    /** 是否参加活动(true 表示有参加, 默认是 false) */
    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    /** 活动Id */
    public long getMarketingId() {
        return marketingId;
    }

    /** 活动Id */
    public void setMarketingId(long marketingId) {
        this.marketingId = marketingId;
    }

    /** 参加的活动类型 */
    public ProductActivityType getActivityType() {
        return activityType;
    }

    /** 参加的活动类型 */
    public void setActivityType(ProductActivityType activityType) {
        this.activityType = activityType;
    }

    /** 参加的活动价格 */
    public String getActivityPrice() {
        return activityPrice;
    }

    /** 参加的活动价格 */
    public void setActivityPrice(String activityPrice) {
        this.activityPrice = activityPrice;
    }

    public Long getActivityIntegral() {
        return activityIntegral;
    }

    public void setActivityIntegral(Long activityIntegral) {
        this.activityIntegral = activityIntegral;
    }

    public int getUserBuyCount() {
        return userBuyCount;
    }

    public void setUserBuyCount(int userBuyCount) {
        this.userBuyCount = userBuyCount;
    }

    public Boolean getIsActivity() {
        return isActivity;
    }

    public void setIsActivity(Boolean isActivity) {
        this.isActivity = isActivity;
    }

    /** 活动开始时间 */
    public Date getActivityStartDate() {
        return activityStartDate;
    }

    /** 活动开始时间 */
    public void setActivityStartDate(Date activityStartDate) {
        this.activityStartDate = activityStartDate;
    }

    /** 活动结束时间 */
    public Date getActivityEndDate() {
        return activityEndDate;
    }

    /** 活动结束时间 */
    public void setActivityEndDate(Date activityEndDate) {
        this.activityEndDate = activityEndDate;
    }

    /** 字符串显示结束时间(若大于1天则显示天, 否则显示到分) */
    public String getActivityDateStr() {
        return activityDateStr;
    }

    /** 字符串显示结束时间(若大于1天则显示天, 否则显示到分) */
    public void setActivityDateStr(String activityDateStr) {
        this.activityDateStr = activityDateStr;
    }

    /** 获取实际价格, 有参加活动则使用活动价, 否则返回原价 */
    public String getSellPrice() {
        return activity ? activityPrice : originalPrice;
    }

    /** 获取实际价格, 有参加活动则使用活动价, 否则返回原价 */
    public String getPrice() {
        return isActivity ? activityPrice : originalPrice;
    }

    public String getPriceDifference(){
        return activity ? Money.getMoneyString(Money.YuanToCent(originalPrice) - Money.YuanToCent(activityPrice)) : "0";
    }

}
