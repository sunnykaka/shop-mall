package com.kariqu.productcenter.domain;

/**
 * 套餐项
 * User: Asion
 * Date: 13-5-5
 * Time: 上午10:46
 */
public class MealItem {

    private int id;

    private long skuId;

    private int mealSetId;

    private int productId;

    private long skuPrice;

    private int number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public long getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(long skuPrice) {
        this.skuPrice = skuPrice;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getMealSetId() {
        return mealSetId;
    }

    public void setMealSetId(int mealSetId) {
        this.mealSetId = mealSetId;
    }
}
