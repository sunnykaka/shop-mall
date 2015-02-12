package com.kariqu.buyer.web.helper;

/**
 * @author Athens(刘杰)
 * @Time 13-5-6 下午5:17
 */
public class MealTradeItemView {

    /** 套餐价 */
    private String mealPrice;

    /** 商品相关的数据 */
    private TradeItemView tradeItem;

    public String getMealPrice() {
        return mealPrice;
    }

    /** 套餐价 */
    public void setMealPrice(String mealPrice) {
        this.mealPrice = mealPrice;
    }

    /** 商品相关的数据 */
    public TradeItemView getTradeItem() {
        return tradeItem;
    }

    /** 商品相关的数据 */
    public void setTradeItem(TradeItemView tradeItem) {
        this.tradeItem = tradeItem;
    }
}
