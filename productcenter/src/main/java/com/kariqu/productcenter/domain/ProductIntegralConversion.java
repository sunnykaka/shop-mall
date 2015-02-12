package com.kariqu.productcenter.domain;

import com.kariqu.usercenter.domain.Currency;

/**
 * User: Json.zhu
 * Date: 13-12-31
 * Time: 上午11:19
 *  商品积分兑换表
 */
public class ProductIntegralConversion extends ProductConversionBase {

    /**积分数 * 100, 方便系统存储 */
    private int integralCount;
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


    /**
     * 积分乘以多个数量
     * @param num 个数
     * @return
     */
    public String fetchCurrencyMutiNum(int num) {
       return Currency.IntegralToCurrencyWithFormat(Long.valueOf(this.integralCount * num), "#");
    }
}
