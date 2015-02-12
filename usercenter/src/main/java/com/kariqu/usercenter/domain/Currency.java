package com.kariqu.usercenter.domain;

import com.kariqu.common.lib.Money;
import org.apache.commons.lang.math.NumberUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Maven
 * Date: 14-7-25
 * Time: 下午12:10
 * To change this template use File | Settings | File Templates.
 */
public class Currency {
    /**
     * 将积分转换为积分分 0.01积分分=1积分
     *
     * @param currency 积分
     * @return
     */
    public static long CurrencyToIntegral(String currency) {
        return Money.YuanToCent(currency);
    }

    /**
     * 积分分转为积分 1积分 = 0.01积分分
     *
     * @param integral 积分分
     * @return
     */
    public static String IntegralToCurrency(long integral) {
        return Money.getMoneyString(integral);
    }

    public static String IntegralToCurrencyWithFormat(long integral, String format) {
        return Money.CentToYuan(integral).toStringWithFormat(format);
    }
}
