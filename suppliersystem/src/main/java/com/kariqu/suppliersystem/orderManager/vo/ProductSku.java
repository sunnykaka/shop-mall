package com.kariqu.suppliersystem.orderManager.vo;

/**
 * User: Wendy
 * Date: 12-6-21
 * Time: 下午6:21
 */
public class ProductSku {

    //商品信息
    private long skuId;
    private int orderId;
    private String productName;
    private String skuPrice;

    /**
     * 购买数量
     */
    private int number;

    /**
     * 实际发货数量
     */
    private int shipmentNum;

    /**
     * 条形码
     */
    private String barCode;

    /**
     * 商品编号
     */
    private String itemNo;

    //sku属性
    private String attribute;

    //库存
    private int stockQuantity;

    //库房名称
    private String storageName;

    private String createTime;

    /**
     * 购买时的单价
     */
    private String unitPrice;

    private String brandName;

    private String categoryName;

    //销售金额
    private String salePrice;

    //商品金额
    private String commodityPrice;

    public String getCommodityPrice() {
        return commodityPrice;
    }

    public void setCommodityPrice(String commodityPrice) {
        this.commodityPrice = commodityPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public int getShipmentNum() {
        return shipmentNum;
    }

    public void setShipmentNum(int shipmentNum) {
        this.shipmentNum = shipmentNum;
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }
}
