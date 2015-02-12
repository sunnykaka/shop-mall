package com.kariqu.productmanager.helper;

import com.kariqu.productcenter.domain.RecommendType;

/**
 * User: Asion
 * Date: 11-11-8
 * Time: 下午7:35
 */
public class RecommendProduct {

    private int productId;

    private int recommendProductId;  //关联产品

    private String productName;

    private String pictureUrl;

    private RecommendType recommendType;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getRecommendProductId() {
        return recommendProductId;
    }

    public void setRecommendProductId(int recommendProductId) {
        this.recommendProductId = recommendProductId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public RecommendType getRecommendType() {
        return recommendType;
    }

    public void setRecommendType(RecommendType recommendType) {
        this.recommendType = recommendType;
    }
}
