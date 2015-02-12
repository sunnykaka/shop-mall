package com.kariqu.tradecenter.domain;


/**
 * User: Asion
 * Date: 12-7-20
 * Time: 下午3:11
 */
public class TradeItem {

    private long skuId;

    protected int number; //购买数量

    //是否有库存，这个字段不保存，是在显示购物车的时候动态从数据库中查询的，这样可以实时查看是否有货
    private boolean hasStock;


    protected TradePriceStrategy tradePriceStrategy;


    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public int getNumber() {
        return number;
    }

    public String getNumberByString(){
        return Long.toString(number);
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isHasStock() {
        return hasStock;
    }

    public void setHasStock(boolean hasStock) {
        this.hasStock = hasStock;
    }

    public TradePriceStrategy getTradePriceStrategy() {
        return tradePriceStrategy;
    }

    public void setTradePriceStrategy(TradePriceStrategy tradePriceStrategy) {
        this.tradePriceStrategy = tradePriceStrategy;
    }
}
