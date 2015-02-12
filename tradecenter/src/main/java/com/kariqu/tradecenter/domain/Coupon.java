package com.kariqu.tradecenter.domain;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;

import java.util.Date;

/**
 * 现金券
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 13-1-14
 *        Time: 上午11:29
 */
public class Coupon {

    private int id;

    /**
     * 编码
     */
    private String code;

    /**
     * 是否使用
     */
    private boolean used;

    /**
     * 是否已分发
     */
    private boolean publish;

    /**
     * 是否已发送短信提醒
     */
    private boolean msgRemind;

    /**
     * 用户ID, 可以为空
     */
    private int userId;

    /**
     * 订单号
     */
    private long orderNo;

    /**
     * 优惠数值(若是现金券, 则此值代表了优惠价格; 若是折扣券)
     */
    private long price;

    /**
     * 能够使用的最少订单价格
     */
    private long miniApplyOrderPrice;

    /**
     * 现金券类型
     */
    private CouponType couponType;

    private Date createDate;

    private Date updateDate;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 过期时间
     */
    private Date expireDate;

    /**
     * 还没有生效吗? 未生效则返回 true
     */
    public boolean isNotBegin() {
        return startDate.getTime() > new Date().getTime();
    }

    /**
     * 是否已过期? 已过期则返回 true
     */
    public boolean isExpire() {
        return new Date().getTime() > expireDate.getTime();
    }

    public String getMoney() {
        return Money.getMoneyString(price);
    }

    /**
     * 根据页面需求将优惠额后的小数点去除
     * 如果是比例优惠劵，则如果是90打9折将0去除(除以10)，85折类的不变
     * @return
     */
    public String getYuanMoney(){
        String money = Money.getMoneyString(price);
        return this.getCouponType() == Coupon.CouponType.Normal ? money.substring(0,money.indexOf(".")) :
                this.getPrice() %10 == 0 ? this.getPrice()/10 + "" : this.getPrice() + "";

    }

    public String getMiniApplyMoney() {
        return Money.getMoneyString(miniApplyOrderPrice);
    }

    /**
     * 根据页面需求将优惠额后的小数点去除
     * @return
     */
    public String getYuantMiniApplyMoney(){
        String money = Money.getMoneyString(miniApplyOrderPrice);
        return money.substring(0,money.indexOf("."));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public long getMiniApplyOrderPrice() {
        return miniApplyOrderPrice;
    }

    public void setMiniApplyOrderPrice(long miniApplyOrderPrice) {
        this.miniApplyOrderPrice = miniApplyOrderPrice;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public boolean isMsgRemind() {
        return msgRemind;
    }

    public void setMsgRemind(boolean msgRemind) {
        this.msgRemind = msgRemind;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public CouponType getCouponType() {
        return couponType;
    }

    public void setCouponType(CouponType couponType) {
        this.couponType = couponType;
    }

    public static enum CouponType {

        /**
         * 普通
         */
        Normal {
            @Override
            public long calculate(long total, long couponPrice) {
                total -= couponPrice;
                return total;
            }

            @Override
            public String getPriceValue(long total, long couponPrice) {
                return Money.getMoneyString(couponPrice);
            }
        },

        /**
         * 比例
         */
        Ratio {
            @Override
            public long calculate(long total, long couponPrice) {
                // 先除以 100 再乘以 100, 就能去掉小数位后的数了
                return total * couponPrice / 100 / 100 * 100;
            }

            @Override
            public String getPriceValue(long total, long couponPrice) {
                return Money.getMoneyString(total - calculate(total, couponPrice));
            }
        };

        abstract long calculate(long total, long couponPrice);

        abstract String getPriceValue(long total, long couponPrice);
    }

    /**
     * 计算使用优惠券之后的订单价格.
     *
     * @param totalPrice 原订单价格
     * @return 使用后的价格
     */
    public long calculatePrice(long totalPrice) {
        return couponType.calculate(totalPrice, price);
    }

    /**
     * 获取此优惠券对应的钱
     *
     * @param totalPrice 原订单价, 若使用的是折扣券需要用来计算.
     * @return
     */
    public String getPriceValue(long totalPrice) {
        return couponType.getPriceValue(totalPrice, price);
    }

    /**
     * 检查现金券基本信息.
     *
     * @param total 原订单总价
     * @throws OrderNoTransactionalException
     */
    public void checkCoupon(long total) throws OrderNoTransactionalException {
        if (!publish) {
            throw new OrderNoTransactionalException("此现金券不能使用!");
        }
        if (used && couponType == CouponType.Normal) {
            throw new OrderNoTransactionalException("此现金券已被使用!");
        }
        if (isNotBegin()) {
            throw new OrderNoTransactionalException("此现金券还未生效");
        }
        if (isExpire()) {
            throw new OrderNoTransactionalException("此现金券已过期!");
        }
        if (miniApplyOrderPrice > total) {
            throw new OrderNoTransactionalException("此现金券只能用于金额在 " + getMiniApplyMoney() + "元 以上的订单(当前订单总额: " + Money.getMoneyString(total) + "元)");
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", used=" + used +
                ", publish=" + publish +
                ", msgRemind=" + msgRemind +
                ", userId=" + userId +
                ", orderNo=" + orderNo +
                ", price=" + price +
                ", miniApplyOrderPrice=" + miniApplyOrderPrice +
                ", couponType=" + couponType +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", startDate=" + startDate +
                ", expireDate=" + expireDate +
                '}';
    }

    public static enum CouponUsed {

        /** 已使用 */
        Used("已使用"),

        /** 可用券 */
        Unused("可用券"),

        /** 已过期 */
        Overdue("已过期");

        private String value;

        CouponUsed(String value) {
            this.value = value;
        }

        public String toDesc() {
            return value;
        }

        public static boolean equals(String status) {
            for(Coupon.CouponUsed couponUsed : Coupon.CouponUsed.values()){
                 if(couponUsed.name().equals(status)){
                     return true;
                 }
            }
            return false;
        }
    }


}
