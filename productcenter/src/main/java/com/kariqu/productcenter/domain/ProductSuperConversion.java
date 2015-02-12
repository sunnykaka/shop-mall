package com.kariqu.productcenter.domain;

import com.kariqu.usercenter.domain.Currency;

/**
 * User: Json.zhu
 * Date: 13-12-31
 * Time: 上午11:27
 * 商品超值兑换表
 */
public class ProductSuperConversion extends ProductConversionBase {
    /**积分数 * 100 */
    private int integralCount;
    /**购买金额 */
    private long money;
    /**用户购买次数 */
    private int userBuyCount;

    public int getIntegralCount() {
        return integralCount;
    }

    public void setIntegralCount(int integralCount) {
        this.integralCount = integralCount;
    }

    //转成人类可读的积分表示, 系统里是按 * 100 存储的, 是因为不想用浮点数存储
    public String humanIntegralCount() {
        return Currency.IntegralToCurrencyWithFormat(integralCount, "#");
    }

    public long getMoney() {
        return money;
    }

    /**
     * 返回可读的金钱表示. 也就是把系统里存的分转换一下
     * @return
     */
    public String fetchMoney4Human() {
        return Money.CentToYuan(money).toStringWithFormat("#.##");
    }

    public String getMoneyForPrice() {
        return Money.getMoneyString(money);
    }

    public String fetchMoneyForPriceMutiNum(int num) {
        return Money.CentToYuan(money * num).toStringWithFormat("#.##");
    }

    public void setMoneyForPrice(String moneyForPrice) {
        money = Money.YuanToCent(moneyForPrice);
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public int getUserBuyCount() {
        return userBuyCount;
    }

    public void setUserBuyCount(int userBuyCount) {
        this.userBuyCount = userBuyCount;
    }

    /**
     * 获取积分
     * @return
     */
    public String getCurrency(){
        return Currency.IntegralToCurrency(Long.valueOf(this.integralCount));
    }

    public String fetchCurrencyMutiNum(int num) {
        return Currency.IntegralToCurrencyWithFormat(Long.valueOf(this.integralCount * num), "#");
    }



}
