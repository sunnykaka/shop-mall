package com.kariqu.buyer.web.controller.trade;

import com.kariqu.common.ApplicationContextUtils;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.payment.CallBackResult;
import com.kariqu.tradecenter.payment.PayCallback;
import com.kariqu.tradecenter.payment.ResponseType;
import com.kariqu.tradecenter.service.CouponService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * 购买优惠券
 * User: Alec
 * Date: 13-10-31
 * Time: 下午1:50
 */
public class CouponSetPayCallback implements PayCallback {

    private JdbcTemplate jdbcTemplate = ApplicationContextUtils.getBean(JdbcTemplate.class);
    private CouponService couponService = ApplicationContextUtils.getBean(CouponService.class);
    private static Log logger = LogFactory.getLog(CouponSetPayCallback.class);





    @Override
    public CallBackResult initResult(TradeInfo tradeInfo, ResponseType type) {
        logger.info("「现金券回调」开始初始化CallBackResult");
        CallBackResult result = new CallBackResult("payCouponSuccess", "payFail");
        long payTotalFee = tradeInfo.getPayTotalFee();
        Money money = new Money();
        money.setCent(payTotalFee);
        CouponSet couponSet = (CouponSet) jdbcTemplate.queryForObject("select id,userId,setType,tradeNo,payMethod,totalPrice,allocated,createDate from CouponSet where isDelete=0 and tradeNo=" + tradeInfo.getTradeNo(), new BeanPropertyRowMapper(CouponSet.class));
        result.addData("setType", couponSet.getSetType());
        result.addData("total_fee", money.toString());
        result.addData("couponSet",couponSet);
        logger.info("「现金券回调」CallBackResult初始化成功");
        return result;
    }

    @Override
    public boolean doAfterBack(TradeInfo tradeInfo, ResponseType type, CallBackResult result) {
        logger.info("「现金券回调」开始分配现金券");
        CouponSet couponSet = (CouponSet)result.getData("couponSet");
        /**
         * 为用户分配优惠券
         */
        if (null != couponSet) {
            int userId = couponSet.getUserId();
            CouponSetType setType = couponSet.getSetType();
            for (CouponSetType.PN pn : setType.couponPrice()) {
                try {
                    List<Coupon> coupons = couponService.fetchCouponForUser(couponSet.getUserId(), pn.price, pn.number);
                    if (coupons.size() == 0) {
                        logger.error("为用户" + userId + "分配优惠券时数量不够了");
                        return false;
                    }
                    jdbcTemplate.update("update CouponSet set allocated=1 where id=" + couponSet.getId());
                } catch (Exception e) {
                    logger.error("为用户" + userId + "分配优惠券出错，手动分配", e);
                    return false;
                }
            }
        }
        logger.info("「现金券回调」现金券分配成功");
        return true;
    }
}
