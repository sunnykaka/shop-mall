package com.kariqu.buyer.web.helper;

import com.kariqu.productcenter.domain.Money;

/**
 * 对购物车中的商品进行显示适配的对象
 * User: Asion
 * Date: 11-10-13
 * Time: 下午1:17
 */
public class TradeItemView {

    private int productId;

    private long skuId;

    private String productName;

    // 促销前价格，如果没有促销那么和price价格一样
    private String originalPrice;

    private String price;

    private String customer;

    private boolean hasStock;

    private int number;

    private String totalPrice;

    //购买限制
    private int limit;

    private String imageUrl;

    private String skuPv;


    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    /**
     * 商品名加上了sku销售属性
     * @return
     */
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isHasStock() {
        return hasStock;
    }

    public void setHasStock(boolean hasStock) {
        this.hasStock = hasStock;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }


    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSkuPv() {
        return skuPv;
    }

    public void setSkuPv(String skuPv) {
        this.skuPv = skuPv;
    }

    public boolean hasActive() {
        return Money.YuanToCent(this.originalPrice) > Money.YuanToCent(this.price);
    }

}
