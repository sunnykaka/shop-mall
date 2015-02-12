package com.kariqu.productcenter.domain;

import java.util.Date;
import java.util.List;

/**
 * SKU来自于线下零售行业，表示可以存储的最小的单元
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-9-19 下午12:45
 */
public class StockKeepingUnit {


    /**
     * 状态，如果REMOVED表示这个sku失效
     */
    public static enum SKUState {
        NORMAL,
        REMOVED
    }

    /**
     * SKU  ID
     */
    private long id;

    /**
     * 商品ID，一个商品可以有多个sku
     */
    private int productId;

    /**
     * 库存数量，查询sku的时候如果提供仓库则可读出库存数量
     */
    private int stockQuantity;

    /**
     * 价格
     */
    private long price;

    /**
     * 市场价格
     */
    private long marketPrice;

    /**
     * SKU 属性列表
     */
    private List<SkuProperty> skuProperties;


    /**
     * SKU属性在数据库中的字符串表示 pid:vid,pid:vid，值和上面的skuProperties对应
     */
    private String skuPropertiesInDb;

    /**
     * SKU 状态，默认是无效的，需要操作人员手的有效，有效的时候会检查约束
     */
    private SKUState skuState = SKUState.REMOVED;

    /**
     * 本sku的顾客购买上限，防止恶意下单
     */
    private int tradeMaxNumber;

    /**
     * 创建日期
     */
    private Date createTime;


    /**
     * 修改日期
     */
    private Date updateTime;

    /**
     * 条形码
     */
    private String barCode;

    /**
     * sku编码
     */
    private String skuCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public SKUState getSkuState() {
        return skuState;
    }

    public void setSkuState(SKUState skuState) {
        this.skuState = skuState;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<SkuProperty> getSkuProperties() {
        return skuProperties;
    }

    public void setSkuProperties(List<SkuProperty> skuProperties) {
        this.skuProperties = skuProperties;
    }

    public String getSkuPropertiesInDb() {
        return skuPropertiesInDb;
    }

    public void setSkuPropertiesInDb(String skuPropertiesInDb) {
        this.skuPropertiesInDb = skuPropertiesInDb;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
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

    public boolean canBuy() {
        return this.skuState == SKUState.NORMAL;
    }

    public long getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(long marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
}
