package com.kariqu.tradecenter.service;

import com.kariqu.tradecenter.domain.SubmitOrderForPrice;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.usercenter.domain.User;

import java.util.List;

/**
 * 积分.
 *
 * @author Athens(刘杰)
 * @Time 2013-03-26 16:43
 * @since 1.0.0
 */
public interface IntegralService {

    /**
     * 检查并计算积分对应的钱.
     *
     * @param submitOrderForPrice 存储积分对应的钱数, 外面会用到使用.
     * @param integral            积分分
     * @param user                用户信息(检查用户的积分数是否够)
     * @param totalPrice          使用积分前的总价
     * @return 使用积分后的总价
     */
    long reducePriceForIntegral(SubmitOrderForPrice submitOrderForPrice,
                                long integral, User user, long totalPrice) throws OrderNoTransactionalException;

    /**
     * 检查积分, 并将去掉积分后的总价返回
     *
     * @param integral   积分分数
     * @param user       用户
     * @param totalPrice 原价
     * @return 去掉积分后的总价
     * @throws OrderNoTransactionalException
     */
    long reducePriceForIntegral(long integral, User user, long totalPrice) throws OrderNoTransactionalException;

    /**
     * 获取兑换 1 块钱需要的积分数, 默认是 1 积分.
     */
    double integralCount();

    /**
     * 计算钱对应的积分分
     *
     * @param price 钱数(单位: 分)
     */
    long calculateMoney(long price);

    /**
     * 计算积分对应的钱(转换后的钱为分)
     *
     * @param integral 积分分数
     */
    long calculateIntegral(long integral);

    /**
     * 使用积分.
     *
     * @param integral    积分分数
     * @param orderNoList 用在的订单列表
     * @param userId      使用积分的人
     */
    void useIntegral(long integral, List<Long> orderNoList, int userId);

    /**
     * 交易成功后返回给用户的积分百分比, 100 元钱返 1 积分. 则此值就是 1
     */
    double getTradeIntegralPercent();

    /**
     * 交易返还的积分数量
     *
     * @param price 钱数(单位: 分)
     * @return
     */
    String getTradeCurrency(long price);

    /**
     * 将积分转换为现金元
     *
     * @param integral 积分分
     * @return
     */
    String integralToYuan(long integral);

    /**
     * 获取金钱对应的积分数
     *
     * @param cent 分
     * @return
     */
    String moneyToCurrency(long cent);

    /**
     * 根据积分和钱的比例算出钱对应的积分分,金钱乘以比例
     *
     * @param price
     * @param integralPercent 积分比例
     * @return
     */
    long moneyToIntegralByIntegralPercent(String price, double integralPercent);

    // 评价后增加积分

    /**
     * 获取注册账号获取的积分数
     *
     * @return
     */
    long getRegisterCurrencyCount();
}
