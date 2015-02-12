package com.kariqu.tradecenter.service.impl;

import com.kariqu.usercenter.domain.Currency;
import com.kariqu.common.lib.Money;
import com.kariqu.om.domain.Const;
import com.kariqu.om.service.ConstService;
import com.kariqu.tradecenter.domain.SubmitOrderForPrice;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.tradecenter.service.IntegralService;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.service.UserPointService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-03-26 16:45
 * @since 1.0.0
 */
public class IntegralServiceImpl implements IntegralService {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private ConstService constService;

    /**
     * 1 块钱兑换的积分积分, 默认是 1 元
     */
    private static final int INTEGRAL_OF_MONEY = 1;

    /**
     * 交易成功后返回给用户的积分百分比, 默认是 1
     */
    private static final double DEFAULT_TRADE_INTEGRAL_PERCENT = 1;

    /**
     * 注册用户后，赠送用户的积分数，默认是 5，送 5 积分
     */
    private static final String DEFAULT_REGISTER_INTEGRAL = "5";

    @Override
    public long reducePriceForIntegral(SubmitOrderForPrice submitOrderForPrice,
                                       long integral, User user, long totalPrice) throws OrderNoTransactionalException {
        // 积分更新总价
        if (integral > 0) {
            if (integral > user.getPointTotal()) {
                throw new OrderNoTransactionalException("本次最多可用 " + user.getCurrency() + " 积分.");
            }
            long backupTotalPrice = totalPrice;

            long integralMoney = calculateIntegral(integral);
            // 使用积分
            totalPrice -= integralMoney;

            if (totalPrice < 0) {
                String aboutIntegral = moneyToCurrency(backupTotalPrice);
                throw new OrderNoTransactionalException("超过当前订单最大积分使用数(只能使用 " + aboutIntegral + " 积分)!");
            }

            if (submitOrderForPrice != null)
                submitOrderForPrice.setIntegral(integralToYuan(integral));
        }
        return totalPrice;
    }

    @Override
    public long reducePriceForIntegral(long integral, User user, long totalPrice) throws OrderNoTransactionalException {
        return reducePriceForIntegral(null, integral, user, totalPrice);
    }

    @Override
    public double integralCount() {
        Const constInfo = constService.getConstByKey("integral");
        double integral = NumberUtils.toDouble(constInfo == null ? "" : constInfo.getConstValue(), INTEGRAL_OF_MONEY);
        if (integral <= 0) integral = INTEGRAL_OF_MONEY;
        return integral;
    }

    @Override
    public long calculateMoney(long price) {
        return Double.valueOf(price * integralCount()).longValue();
    }

    @Override
    public long calculateIntegral(long integral) {
        return Double.valueOf(integral / integralCount()).longValue();
    }

    @Override
    @Transactional
    public void useIntegral(long integral, List<Long> orderNoList, int userId) {
        // 减少用户的积分数
        if (integral > 0) {
            UserPoint userPoint = new UserPoint();
            userPoint.setUserId(userId);
            userPoint.setPoint(integral);
            userPoint.setDescription("使用积分(订单号:" + orderNoList.toString().replace("[", "").replace("]", "") + ")");
            userPoint.setInOutComingType(UserPoint.InOutComingType.Order);
            userPoint.setType(UserPoint.PointType.OutComing);
            userPointService.createUsePoint(userPoint);
        }
    }

    public double getTradeIntegralPercent() {
        Const constInfo = constService.getConstByKey("integralBack");
        double integralBack = NumberUtils.toDouble(constInfo == null ? "" : constInfo.getConstValue(), DEFAULT_TRADE_INTEGRAL_PERCENT);
        if (integralBack <= 0) integralBack = DEFAULT_TRADE_INTEGRAL_PERCENT;
        return integralBack;
    }

    @Override
    public String getTradeCurrency(long price) {
        return Currency.IntegralToCurrency(Double.valueOf(this.getTradeIntegralPercent() * price / 100).longValue());
    }

    @Override
    public String integralToYuan(long integral) {
        return Money.getMoneyString(calculateIntegral(integral));
    }

    @Override
    public String moneyToCurrency(long cent) {
        return Currency.IntegralToCurrency(Double.valueOf(cent * integralCount()).longValue());
    }

    @Override
    public long moneyToIntegralByIntegralPercent(String price, double integralPercent) {
        return Double.valueOf(new Double(price) * integralPercent).longValue();
    }

    public long getRegisterCurrencyCount() {
        Const constInfo = constService.getConstByKey("registerIntegral");
        String currency = constInfo == null || StringUtils.isBlank(constInfo.getConstValue()) ? DEFAULT_REGISTER_INTEGRAL : constInfo.getConstValue();
        return Currency.CurrencyToIntegral(currency);
    }
}
