package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.domain.PayBank;
import com.kariqu.tradecenter.domain.RefundTradeOrder;
import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.repository.TradeRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-15
 * Time: 下午3:33
 */
@SpringApplicationContext({"classpath:tradeContext.xml"})
public class TradeInfoRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("tradeRepository")
    private TradeRepository tradeRepository;

    @Test
    public void testTradeRepository() {
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setTradeNo("34234");
        tradeInfo.setOuterTradeNo("343423");
        tradeInfo.setOuterBuyerId("2");
        tradeInfo.setOuterBuyerAccount("22");
        tradeInfo.setOuterPlatformType("222");
        tradeInfo.setPayMethod("22");
        tradeInfo.setPayTotalFee(3223L);
        tradeInfo.setTradeStatus("2");
        tradeInfo.setNotifyId("notity");
        tradeInfo.setNotifyType("type");
        tradeInfo.setGmtCreateTime(new Date());
        tradeInfo.setGmtModifyTime(new Date());
        tradeInfo.setTradeGmtCreateTime(new Date());
        tradeInfo.setTradeGmtModifyTime(new Date());
        tradeInfo.setBizType("order");
        tradeInfo.setDefaultbank(PayBank.ABC.toString());

        tradeRepository.createTradeInfo(tradeInfo);

        assertEquals("2", tradeRepository.selectTradeInfoById(tradeInfo.getId()).getTradeStatus());

        tradeInfo.setTradeStatus("333");

        tradeRepository.updateTradeInfo(tradeInfo);
        assertEquals("333", tradeRepository.selectTradeInfoById(tradeInfo.getId()).getTradeStatus());

        assertEquals(1, tradeRepository.queryTradeInfo("34234", "343423"));
    }

    @Test
    public void testTradeOrder() {
        tradeRepository.createOrderTradeInfo("1", new Long[]{1l});
        List<Long> longs = tradeRepository.queryOrderListByTradeNo("1");
        assertEquals(1, longs.size());
        assertEquals(new Long(1), longs.get(0));
        tradeRepository.triggerTradeOrderPaySuccessful("1");
        tradeRepository.triggerTradeOrderPayRecharge("1", 1l);
    }

    @Test
    public void testRefund() {
        Map trade = new HashMap();
        trade.put("batchNo", "1111");
        trade.put("realRefund", 1000l);
        trade.put("successNum", 1);
        tradeRepository.createRefundTrade(trade);

        RefundTradeOrder refundTradeOrder = new RefundTradeOrder();
        refundTradeOrder.setBatchNo("1111");
        refundTradeOrder.setBackGoodsId(33l);
        refundTradeOrder.setOuterTradeNo("201224242252525");
        refundTradeOrder.setRefund(3221);

        tradeRepository.createRefundTradeOrder(refundTradeOrder);
        tradeRepository.updateRefundTradeOrderSuccess("1111", "201224242252525", 1);
        assertEquals(33l, tradeRepository.getBackIdByBatchNoAndTradeNo("1111", "201224242252525"));
    }


    @Test
    public void testCoupon() {
        Coupon coupon = new Coupon();
        coupon.setCode("333");
        coupon.setPrice(3);
        coupon.setPublish(true);
        coupon.setStartDate(new Date());
        // 3 天后过期
        coupon.setExpireDate(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 3)));
        coupon.setCouponType(Coupon.CouponType.Normal);
        coupon.setMiniApplyOrderPrice(33);
        tradeRepository.createCoupon(coupon);
        assertFalse(coupon.isExpire());
        assertEquals(1, tradeRepository.queryNotUsedCoupon(new Page<Coupon>(1, 15), "", 0).getResult().size());
        assertEquals(1, tradeRepository.queryNotUsedAndNotAssignCoupon(3, 1).size());
        tradeRepository.makeCouponUsed("333", 32532);
        assertEquals(1, tradeRepository.queryUsedCoupon(new Page<Coupon>(1, 15), "333", 0).getResult().size());
        assertEquals(32532, tradeRepository.queryCouponByCode("333").getOrderNo());
        assertEquals(3, tradeRepository.queryCouponByCode("333").getPrice());
        assertEquals(1, tradeRepository.queryCouponByUserId(0).size());
        coupon.setUserId(1);
        tradeRepository.updateCoupon(coupon);
        assertEquals(1, tradeRepository.queryCouponByUserId(1).size());

        // 还有 3 天过期的优惠券 >> 有 1 张, 还有 4 天过期的优惠券 >> 没有
        assertEquals(1, tradeRepository.getMsgRemindCoupon(3).size());
        assertEquals(0, tradeRepository.getMsgRemindCoupon(4).size());
    }
}
