package com.kariqu.tradecenter.domain;

/**
 * 封装订单提交时的现金券信息
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 13-1-14
 *        Time: 下午2:27
 */
public class CouponInfo {

    private boolean useCoupon;

    private String code;

    public boolean isUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(boolean useCoupon) {
        this.useCoupon = useCoupon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
