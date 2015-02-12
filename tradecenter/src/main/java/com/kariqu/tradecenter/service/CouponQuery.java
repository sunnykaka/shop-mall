package com.kariqu.tradecenter.service;

import com.kariqu.common.pagenavigator.BaseQuery;
import com.kariqu.tradecenter.domain.Coupon;

/**
 * Created with IntelliJ IDEA.
 * User: Try.Liu
 * Date: 14-6-16
 * Time: 下午5:06
 */
public class CouponQuery  extends BaseQuery {

    private int couponId;

    private Coupon.CouponUsed couponUsed;

    /**
     * 按
     * @param couponId
     * @param couponUsed
     * @return
     */
    public static CouponQuery where(int couponId, Coupon.CouponUsed couponUsed) {
        CouponQuery couponQuery = new CouponQuery();
        couponQuery.setCouponId(couponId);
        couponQuery.setCouponUsed(couponUsed);
        return couponQuery;
    }
    public Coupon.CouponUsed getCouponUsed() {
        return couponUsed;
    }

    public void setCouponUsed(Coupon.CouponUsed couponUsed) {
        this.couponUsed = couponUsed;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }
}
