package com.kariqu.tradesystem.helper;

import java.util.List;

/**
 * User: wendy
 * Date: 12-6-28
 * Time: 下午5:05
 */
public class ProductSku {
    //商品信息
    private long skuId;

    private int orderId;

    private int productId;

    private String productName;

    private String skuPrice;

    private int number;

    /**
     * 这个sku的配送状态
     */
    private String skuState;

    /**
     * 实际发货数量
     */
    private int shipmentNum;

    /**
     * 退货数量
     */
    private int backNumber;


    private float skuPreferentialPrice;

    //sku属性
    private String attribute;

    //库存
    private int stockQuantity;


    private String productCode;

    private String barCode;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    private List<SkuPropertyList> skuPropertyList;

    public List<SkuPropertyList> getSkuPropertyList() {
        return skuPropertyList;
    }

    public void setSkuPropertyList(List<SkuPropertyList> skuPropertyList) {
        this.skuPropertyList = skuPropertyList;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getSkuPreferentialPrice() {
        return skuPreferentialPrice;
    }

    public void setSkuPreferentialPrice(float skuPreferentialPrice) {
        this.skuPreferentialPrice = skuPreferentialPrice;
    }

    public String getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(String skuPrice) {
        this.skuPrice = skuPrice;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSkuState() {
        return skuState;
    }

    public void setSkuState(String skuState) {
        this.skuState = skuState;
    }

    public int getShipmentNum() {
        return shipmentNum;
    }

    public void setShipmentNum(int shipmentNum) {
        this.shipmentNum = shipmentNum;
    }

    public int getBackNumber() {
        return backNumber;
    }

    public void setBackNumber(int backNumber) {
        this.backNumber = backNumber;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
