package com.kariqu.productmanager.helper;

/**
 * User: Asion
 * Date: 13-5-5
 * Time: 下午2:31
 */
public class MealItemRecord extends StockPriceRecord {

    private int id;

    //套餐价
    private String mealPrice;

    //套餐数量
    private int mealNumber;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(String mealPrice) {
        this.mealPrice = mealPrice;
    }

    public int getMealNumber() {
        return mealNumber;
    }

    public void setMealNumber(int mealNumber) {
        this.mealNumber = mealNumber;
    }
}
