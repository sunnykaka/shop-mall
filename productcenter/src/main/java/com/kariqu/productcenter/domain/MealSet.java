package com.kariqu.productcenter.domain;

/**
 * 套餐
 * User: Asion
 * Date: 13-5-4
 * Time: 下午1:30
 */
public class MealSet {

    private int id;

    private String name;

    /** 推荐理由 */
    private String recommendReason;

    //用字符串，以后用来冗余json价格
    private String price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** 推荐理由 */
    public String getRecommendReason() {
        return recommendReason;
    }

    /** 推荐理由 */
    public void setRecommendReason(String recommendReason) {
        this.recommendReason = recommendReason;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
