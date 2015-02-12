package com.kariqu.productmanager.helper;

import org.apache.commons.lang.StringUtils;

/**
 * 库存价格在Ext中的编辑对象
 * User: Asion
 * Date: 11-11-7
 * Time: 下午4:03
 */
public class StockPriceRecord {

    private long skuId;

    /**
     * 多值pv的组合字符串，比如：颜色[红色]尺寸[XL]
     * 如果没有多值销售属性的筛选，这里就是一个商品名，这个时候一个商品对应一个SKU
     */
    private String sku;

    private String productName;

    private String price;

    private String marketPrice;

    /**
     * sku所在的位置，比如美亚的华北仓 TODO 下面三个值以后都可能是多个，以后会重构
     */
    private String skuLocation = "";

    private int stockQuantity;

    private int storeId;
    
    private int tradeMaxNumber;

    private String validStatus;

    /**
     * 条形码
     */
    private String barCode;

    /**
     * sku编码
     */
    private String skuCode;

    public String getSku() {
        return StringUtils.isBlank(sku) ? "无 sku 描述" : sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public String getSkuLocation() {
        return skuLocation;
    }

    public void setSkuLocation(String skuLocation) {
        this.skuLocation = skuLocation;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public int getTradeMaxNumber() {
        return tradeMaxNumber;
    }

    public void setTradeMaxNumber(int tradeMaxNumber) {
        this.tradeMaxNumber = tradeMaxNumber;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
}
