package com.kariqu.productcenter.domain;

/**
 * sku 对应的库位
 *
 * @author Alec
 * @version 1.0.0
 * @since 2012-06-25 09:27:02
 */
public class SkuStorage {

    /**
     * sku
     */
    private long skuId;
    /**
     * 库位编号
     */
    private int productStorageId;
    /**
     * 库存数量
     */
    private int stockQuantity;

    private int tradeMaxNumber;

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public int getProductStorageId() {
        return productStorageId;
    }

    public void setProductStorageId(int productStorageId) {
        this.productStorageId = productStorageId;
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
}
