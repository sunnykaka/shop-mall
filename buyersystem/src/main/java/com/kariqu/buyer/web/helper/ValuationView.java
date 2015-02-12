package com.kariqu.buyer.web.helper;

import java.util.Date;

/**
 * @author Athens(刘杰)
 * @Time 13-7-11 下午1:36
 */
public class ValuationView {

    private int productId;

    private long skuId;

    private String skuName;

    private String skuExplain;

    private String skuMainPicture;

    private long orderItemId;

    private Date createDate;

    private int valuationCount;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuExplain() {
        return skuExplain;
    }

    public void setSkuExplain(String skuExplain) {
        this.skuExplain = skuExplain;
    }

    public String getSkuMainPicture() {
        return skuMainPicture;
    }

    public void setSkuMainPicture(String skuMainPicture) {
        this.skuMainPicture = skuMainPicture;
    }

    public long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getValuationCount() {
        return valuationCount;
    }

    public void setValuationCount(int valuationCount) {
        this.valuationCount = valuationCount;
    }
}
