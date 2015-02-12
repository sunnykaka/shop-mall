package com.kariqu.productcenter.domain;

import java.util.Date;

/**
 * 商品活动，记录商品所参加的活动
 * User: Asion
 * Date: 13-4-1
 * Time: 下午3:06
 */
public class ProductActivity {

    private int id;

    private int productId;

    private Date startDate;

    private Date endDate;

    /**
     * 活动ID ,要联合activityType才能具体定位哪个活动
     */
    private int activityId;

    /**
     * 活动类型
     */
    private ProductActivityType activityType;


    /**
     * 活动价格
     */
    private String activityPrice;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ProductActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ProductActivityType activityType) {
        this.activityType = activityType;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(String activityPrice) {
        this.activityPrice = activityPrice;
    }
}
