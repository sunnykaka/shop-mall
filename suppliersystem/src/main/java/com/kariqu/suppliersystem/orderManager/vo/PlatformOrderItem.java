package com.kariqu.suppliersystem.orderManager.vo;

/**
 * 抽象订单
 * User: wendy
 * Date: 13-8-27
 * Time: 下午5:34
 * To change this template use File | Settings | File Templates.
 */
public class PlatformOrderItem {

    private long id;

    private long orderId;

    private long orderNo;

    private long skuId;
    /**
     * 下单时的商品单价(若有活动则使用活动价格)
     */
    private String unitPrice;

    /**
     * sku价格
     */
    private String skuPrice;

    /**
     * 实际要发货数量
     */
    private int shipmentNum;

    /**
     * 已退款数量
     */
    private int backNum;

    /**
     * 购买数量
     */
    private int number;

    /**
     * 订单项总额(理论上=单价*数量, 若有使用积分或现金券, 则分摊至单个订单项)
     */
    private long itemTotalPrice;

    private String barCode;//条形码

    private String itemNo;//编号

    private String storageName;//库存位置

    private String productName;//商品名称

    private String categoryName;//类目名称

    private String brandName;//品牌名

    private int stockQuantity;  //库存

    private String skuAttribute;  //商品属性


    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getShipmentNum() {
        return shipmentNum;
    }

    public void setShipmentNum(int shipmentNum) {
        this.shipmentNum = shipmentNum;
    }

    public int getBackNum() {
        return backNum;
    }

    public void setBackNum(int backNum) {
        this.backNum = backNum;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getItemTotalPrice() {
        return itemTotalPrice;
    }

    public void setItemTotalPrice(long itemTotalPrice) {
        this.itemTotalPrice = itemTotalPrice;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSkuAttribute() {
        return skuAttribute;
    }

    public void setSkuAttribute(String skuAttribute) {
        this.skuAttribute = skuAttribute;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public String getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(String skuPrice) {
        this.skuPrice = skuPrice;
    }
}
