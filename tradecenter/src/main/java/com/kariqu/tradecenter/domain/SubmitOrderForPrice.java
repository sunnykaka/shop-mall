package com.kariqu.tradecenter.domain;

/**
 * 提交订单时(基于整个订单List, 而非单个订单)与订单价格相差的数据.
 *
 * @author Athens(刘杰)
 * @Time 2013-03-20 10:23
 */
public class SubmitOrderForPrice {

    /**
     * 扣除积分和现金券之后的新价(基于整个订单List, 而非单个订单)
     */
    private long newTotalPrice;

    /**
     * 积分对应的钱
     */
    private String integral;

    /**
     * 现金券
     */
    private Coupon coupon;

    /**
     * 扣除积分和现金券之后的新价(基于整个订单List, 而非单个订单)
     */
    public long getNewTotalPrice() {
        return newTotalPrice;
    }

    /**
     * 扣除积分和现金券之后的新价(基于整个订单List, 而非单个订单)
     */
    public void setNewTotalPrice(long newTotalPrice) {
        this.newTotalPrice = newTotalPrice;
    }

    /**
     * 积分对应的钱
     */
    public String getIntegral() {
        return integral;
    }

    /**
     * 积分对应的钱
     */
    public void setIntegral(String integral) {
        this.integral = integral;
    }

    /**
     * 现金券
     */
    public Coupon getCoupon() {
        return coupon;
    }

    /**
     * 现金券
     */
    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

}
