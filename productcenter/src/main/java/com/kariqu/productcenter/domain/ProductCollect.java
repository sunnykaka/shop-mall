package com.kariqu.productcenter.domain;

import com.kariqu.common.DateUtils;

import java.util.Date;

/**
 * 商品收藏
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-28
 *        Time: 下午2:55
 */
public class ProductCollect {

    private int id;

    private String userId;

    private int productId;

    private String productName;

    private String unitPrice;

    private Date collectTime;

    private String productMainPicture;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductMainPicture() {
        return productMainPicture;
    }

    public void setProductMainPicture(String productMainPicture) {
        this.productMainPicture = productMainPicture;
    }

}
