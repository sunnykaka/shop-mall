package com.kariqu.buyer.web.controller.trade;

import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券套餐类型
 * User: Alec
 * Date: 13-10-31
 * Time: 下午2:11
 */
public enum CouponSetType {
    A {
        @Override
        public String toDesc() {
            return "一张50元现金券(无限制使用)";
        }

        @Override
        public long price() {
            return 2000L;
        }

        @Override
        public List<PN> couponPrice() {
            return priceForA;
        }
    },
    B {
        @Override
        public String toDesc() {
            return "一张100元现金券(满500使用)";
        }

        @Override
        public long price() {
            return 5000L;
        }

        @Override
        public List<PN> couponPrice() {
            return priceForB;
        }
    },
    C {
        @Override
        public String toDesc() {
            return "一张200元现金券(满1000使用)";
        }

        @Override
        public long price() {
            return 10000L;
        }

        @Override
        public List<PN> couponPrice() {
            return priceForC;
        }
    };

    public abstract String toDesc();

    public abstract long price();

    public abstract List<PN> couponPrice();

    private static List<PN> priceForA = new ArrayList<PN>();
    private static List<PN> priceForB = new ArrayList<PN>();
    private static List<PN> priceForC = new ArrayList<PN>();

    static {
        priceForA.add(new PN(5000L, 1));

        priceForB.add(new PN(10000L, 1));

        priceForC.add(new PN(20000L, 1));
    }

    public static class PN {

        public PN(long price, int number) {
            this.price = price;
            this.number = number;
        }

        public long price;

        public int number;
    }
}
