package com.kariqu.tradecenter.service.impl;

import com.kariqu.tradecenter.domain.Coupon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * User: Asion
 * Date: 13-1-22
 * Time: 下午8:08
 */
public class CouponGenerateUtils {

    /**
     * 提取出独立生成现金券的方法   -by kyle
     *
     *
     * @param number
     * @param price
     * @param miniApplyOrderPrice
     * @param isPublish
     * @param couponType
     * @return
     */
    public static List<Coupon> generateCoupon(int number, long price, long miniApplyOrderPrice,
                                              boolean isPublish, Coupon.CouponType couponType, Date startDate, Date expireDate) {
        String base = "abcdefghijklmnopqrstuvwxy";

        List<String> codeList = new ArrayList<String>(number);
        Random random = new Random();

        //1000次循环找给定number的现金券码
        for (int i = 0; i < 100000; i++) {
            if (codeList.size() == number) {
                break;
            }
            int code = Math.abs(random.nextInt()) % 1000000;
            if (code > 100000) {
                codeList.add(code + "");
            }
        }

        List<Coupon> coupons = new ArrayList<Coupon>(codeList.size());

        for (String code : codeList) {
            Coupon coupon = new Coupon();
            char c1 = base.charAt(random.nextInt(base.length()));
            char c2 = base.charAt(random.nextInt(base.length()));
            coupon.setCode("" + c1 + c2 + code);
            coupon.setCouponType(couponType);
            coupon.setPrice(price);
            coupon.setPublish(isPublish);
            coupon.setStartDate(startDate);
            coupon.setExpireDate(expireDate);
            coupon.setMiniApplyOrderPrice(miniApplyOrderPrice);

            coupons.add(coupon);
        }
        return coupons;

    }

}

