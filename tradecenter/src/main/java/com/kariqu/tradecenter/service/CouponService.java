package com.kariqu.tradecenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.domain.SubmitOrderForPrice;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.tradecenter.excepiton.OrderTransactionalException;
import com.kariqu.usercenter.domain.User;

import java.util.Date;
import java.util.List;

/**
 * 现金券
 *
 * @author Athens(刘杰)
 * @Time 2013-03-26 13:26
 * @since 1.0.0
 */
public interface CouponService {

    /**
     * 检查并计算现金券价格.
     *
     * @param coupon     现金券
     * @param totalPrice 使用前的总价
     * @return 使用现金券后的总价
     * @throws OrderNoTransactionalException
     */
    long reducePriceForCoupon(Coupon coupon, long totalPrice) throws OrderNoTransactionalException;

    /**
     * 检查并计算现金券价格.
     *
     * @param couponCode 现金券
     * @param totalPrice 使用前的总价
     * @return 使用现金券后的总价
     * @throws OrderNoTransactionalException
     */
    long reducePriceForCoupon(SubmitOrderForPrice submitOrderForPrice, String couponCode,
                              long totalPrice) throws OrderNoTransactionalException;

    /**
     * 使用现金券.
     *
     * @param orderNoList 积分或现金券 使用在的 订单列表 上
     * @param userId      用户Id
     * @param coupon      现金券
     * @throws OrderTransactionalException
     */
    void useCoupon(Coupon coupon, List<Long> orderNoList, int userId) throws OrderTransactionalException;

    /**
     * 给用户分发现金券
     *
     * @param userId
     * @param price
     * @param number
     * @return
     */
    List<Coupon> fetchCouponForUser(int userId, long price, int number);

    /**
     * 自动生成现金券，参数是个数
     *
     * @param number
     * @param couponType
     * @param startDate
     */
    List<Coupon> generateCouponList(int number, long price, long miniApplyOrderPrice, Coupon.CouponType couponType, Date startDate, Date expireDate);

    /**
     * 为抽奖用户生成现金券
     *
     * @param price 面值
     * @param miniPrice 最小订单价格
     * @param startDate 开始时间
     * @param expireDate 过期时间
     */
    Coupon generateCoupon(long price, long miniPrice, Date startDate, Date expireDate);

    /**
     * 获取所有需要短信提醒的现金券数据
     *
     * @param interval 时间(比如今天是 1 号, 此值是 5, 则提醒 5 号开始过期的现金券)
     * @return 符合条件的数据
     */
    List<Coupon> getRemindCouponList(int interval);

    /**
     * 查询用户所有能用的现金券信息.
     *
     * @param totalPrice 订单总价
     * @param userId 用户Id
     * @return
     */
    List<Coupon> queryCanUseCouponByTotalPriceAndUserId(long totalPrice, int userId);

    /**
     * 未使用的现金券列表
     *
     * @return
     */
    Page<Coupon> queryNotUsedCoupon(Page<Coupon> page, String code, int userId);

    /**
     * 查询用户未使用的现金券.
     *
     * @param userId
     * @param page
     * @return
     */
    Page<Coupon> queryNotUsedCouponByUserId(int userId, Page<Coupon> page);

    /**
     * 查询用户未使用的现金券数量.
     *
     * @param userId
     * @return
     */
    int queryNotUsedCouponCountByUserId(int userId);

    /**
     * 已使用的现金券列表
     *
     * @return
     */
    Page<Coupon> queryUsedCoupon(Page<Coupon> page, String code, int userId);

    /**
     * 已过期的现金券列表
     *
     * @return
     */
    Page<Coupon> queryExpireCoupon(Page<Coupon> page, String code, int userId);

    /**
     * 可分配的现金券列表
     *
     * @return
     */
    Page<Coupon> queryAllocationCoupon(Page<Coupon> page, String code);

    /**
     * 可分配的联动优势现金券列表
     *
     * @return
     */
    Page<Coupon> queryAllUMPayCoupon(Page<Coupon> page, String code);

    /**
     * @param userId
     * @return
     */
    List<Coupon> queryCouponByUserId(int userId);

    Page<Coupon> queryCoupon(CouponQuery couponQuery);


    /**
     * 更新现金券.
     *
     * @param coupon
     * @return
     */
    int updateCoupon(Coupon coupon);

    /**
     * 将某个现金券置为已使用
     *
     * @param code
     */
    int makeCouponUsed(String code, long orderNo);

    Coupon getCouponByCode(String code);

    Coupon getCouponById(int id);

    /**
     * 创建现金券
     *
     * @param coupon
     */
    void createCoupon(Coupon coupon);

    void deleteCoupon(int id);

}
