package com.kariqu.tradecenter.payment;

import com.kariqu.tradecenter.domain.PayBank;
import com.kariqu.tradecenter.helper.TradeSequenceUtil;
import com.kariqu.tradecenter.service.impl.PayMethod;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * 支付请求包装对象
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-26
 *        Time: 上午11:02
 */
public class PayInfoWrapper {

    private String defaultbank;


    private String callBackClass;

    /**
     * 交易总金额，以分为单位
     */
    private long totalFee;


    /**
     * 交易流水号
     */
    private String tradeNo = TradeSequenceUtil.getTradeNo();

    /**
     *支付方式
     */
    private PayMethod payMethod;


    /**
     * 购买的方式：是order还是coupon
     * 修改人：Json。zhu
     * 修改时间：2013.12.09
     */
    private String bizType;

    public String getDefaultbank() {
        return defaultbank;
    }

    public void setDefaultbank(String defaultbank) {
        this.defaultbank = defaultbank;
    }

    public PayMethod getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(PayMethod payMethod) {
        if (payMethod == null) {
            payMethod = PayMethod.directPay;
        }
        this.payMethod = payMethod;
    }


    public boolean isAlipay() {
        return payMethod != null && payMethod == PayMethod.directPay;
    }

    public boolean isTenpay() {
        return payMethod != null && payMethod == PayMethod.Tenpay;
    }

    public boolean isBank() {
        return payMethod != null && payMethod == PayMethod.bankPay && StringUtils.isNotEmpty(defaultbank)
                && Arrays.asList(PayBank.values()).contains(Enum.valueOf(PayBank.class, defaultbank));
    }

    public String getTradeNo() {
        return tradeNo;
    }


    public long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(long totalFee) {
        this.totalFee = totalFee;
    }

    public String getCallBackClass() {
        return callBackClass;
    }

    public void setCallBackClass(Class<? extends PayCallback> callBackClass) {
        this.callBackClass = callBackClass.getName();
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }
}
