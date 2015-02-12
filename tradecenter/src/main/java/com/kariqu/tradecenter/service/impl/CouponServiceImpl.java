package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.domain.SubmitOrderForPrice;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.tradecenter.excepiton.OrderTransactionalException;
import com.kariqu.tradecenter.excepiton.TradeFailException;
import com.kariqu.tradecenter.repository.TradeRepository;
import com.kariqu.tradecenter.service.CouponQuery;
import com.kariqu.tradecenter.service.CouponService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 现金券
 *
 * @author Athens(刘杰)
 * @Time 2013-03-26 13:26
 * @since 1.0.0
 */
public class CouponServiceImpl implements CouponService {

    private static final Logger LOGGER = Logger.getLogger(CouponServiceImpl.class);

    @Autowired
    private TradeRepository tradeRepository;

    @Override
    public long reducePriceForCoupon(Coupon coupon, long totalPrice) throws OrderNoTransactionalException {
        // 检查现金券
        if (coupon != null) {
            coupon.checkCoupon(totalPrice);

            // 计算扣除现金券价格后的总价
            totalPrice = coupon.calculatePrice(totalPrice);

            // 联动优势 的现金券使用后可能会出现 0 或负数, 因此不检查!
            /*
            if (totalPrice < 1) {
                throw new OrderNoTransactionalException("订单价格有误!");
            }
            */
        }
        return totalPrice;
    }

    @Override
    public long reducePriceForCoupon(SubmitOrderForPrice submitOrderForPrice, String couponCode,
                                     long totalPrice) throws OrderNoTransactionalException {
        // 现金券更新总价
        if (StringUtils.isNotBlank(couponCode)) {
            // 检查现金券
            Coupon coupon = getCouponByCode(couponCode);
            if (coupon == null) {
                throw new OrderNoTransactionalException("没有此现金券!");
            }
            submitOrderForPrice.setCoupon(coupon);

            totalPrice = reducePriceForCoupon(coupon, totalPrice);
        }
        return totalPrice;
    }

    @Override
    @Transactional
    public void useCoupon(Coupon coupon, List<Long> orderNoList, int userId) throws OrderTransactionalException {
        // 将优惠卷改为已使用.
        if (coupon != null && coupon.getCouponType() == Coupon.CouponType.Normal) {
            coupon.setUserId(userId);
            coupon.setUsed(true);
            if (updateCoupon(coupon) != 1) {
                throw new OrderTransactionalException("使用现金券[" + coupon.getCode() + "]时发生错误, 请确认现金券未被使用!");
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("使用现金券(订单号:" + orderNoList + ")");
            }
        }
    }


    @Override
    @Transactional
    public List<Coupon> fetchCouponForUser(int userId, long price, int number) {
        List<Coupon> coupons = tradeRepository.queryNotUsedAndNotAssignCoupon(price, number);
        for (Coupon coupon : coupons) {
            coupon.setUserId(userId);
            updateCoupon(coupon);
        }
        return coupons;
    }


    @Override
    public List<Coupon> generateCouponList(int number, long price, long miniApplyOrderPrice,
                                           Coupon.CouponType couponType, Date startDate, Date expireDate) {
        List<Coupon> coupons = CouponGenerateUtils.generateCoupon(number, price, miniApplyOrderPrice, true, couponType, startDate, expireDate);
        for (Coupon coupon : coupons) {
            try {
                createCoupon(coupon);
            } catch (Exception e) {
                LOGGER.error("创建现金券发生异常:code是" + coupon, e);
            }
        }
        return coupons;
    }

    @Override
    public Coupon generateCoupon(long price, long miniPrice, Date startDate, Date expireDate) {
        List<Coupon> coupons = CouponGenerateUtils.generateCoupon(1, price, miniPrice, true, Coupon.CouponType.Normal, startDate, expireDate);
        if (coupons == null || coupons.size() == 0) {
            throw new TradeFailException("没有生成现金券, 请联系客服~");
        }

        return coupons.get(0);
    }

    @Override
    public List<Coupon> getRemindCouponList(int interval) {
        return tradeRepository.getMsgRemindCoupon(interval);
    }

    @Override
    public List<Coupon> queryCanUseCouponByTotalPriceAndUserId(long totalPrice, int userId) {
        return tradeRepository.getCanUseCouponByTotalPriceAndUserId(totalPrice, userId);
    }

    @Override
    public Page<Coupon> queryNotUsedCoupon(Page<Coupon> page, String code, int userId) {
        return tradeRepository.queryNotUsedCoupon(page, code, userId);
    }

    public Page<Coupon> queryNotUsedCouponByUserId(int userId, Page<Coupon> page) {
        return tradeRepository.queryNotUsedCoupon(userId, page);
    }

    @Override
    public int queryNotUsedCouponCountByUserId(int userId) {
        return tradeRepository.queryCountNotUsedCouponByUserId(userId);
    }

    @Override
    public Page<Coupon> queryUsedCoupon(Page<Coupon> page, String code, int userId) {
        return tradeRepository.queryUsedCoupon(page, code, userId);
    }

    @Override
    public Page<Coupon> queryExpireCoupon(Page<Coupon> page, String code, int userId) {
        return tradeRepository.queryExpireCoupon(page, code, userId);
    }

    @Override
    public Page<Coupon> queryAllocationCoupon(Page<Coupon> page, String code) {
        return tradeRepository.queryAllocationCoupon(page, code);
    }

    @Override
    public Page<Coupon> queryAllUMPayCoupon(Page<Coupon> page, String code) {
        return tradeRepository.queryAllUMPayCoupon(page, code);
    }

    @Override
    public List<Coupon> queryCouponByUserId(int couponId) {
        return tradeRepository.queryCouponByUserId(couponId);
    }

    @Override
    public Page<Coupon> queryCoupon(CouponQuery couponQuery) {
        return tradeRepository.queryCoupon(couponQuery);
    }

    public int updateCoupon(Coupon coupon) {
        return tradeRepository.updateCoupon(coupon);
    }

    @Override
    public int makeCouponUsed(String code, long orderNo) {
        return tradeRepository.makeCouponUsed(code, orderNo);
    }

    @Override
    public Coupon getCouponByCode(String code) {
        return tradeRepository.queryCouponByCode(code);
    }

    @Override
    public Coupon getCouponById(int id) {
        return tradeRepository.getCouponById(id);
    }

    @Override
    public void createCoupon(Coupon coupon) {
        tradeRepository.createCoupon(coupon);
    }

    @Override
    public void deleteCoupon(int id) {
        tradeRepository.deleteCoupon(id);
    }

}
