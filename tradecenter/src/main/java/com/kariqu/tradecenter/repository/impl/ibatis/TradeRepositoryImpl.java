package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.domain.RefundTrade;
import com.kariqu.tradecenter.domain.RefundTradeOrder;
import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.repository.TradeRepository;
import com.kariqu.tradecenter.service.CouponQuery;
import com.kariqu.tradecenter.service.TradeQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-15
 * Time: 下午3:33
 */
public class TradeRepositoryImpl extends SqlMapClientDaoSupport implements TradeRepository {
    @Override
    public void createTradeInfo(TradeInfo tradeInfo) {
        getSqlMapClientTemplate().insert("insertTradeInfo", tradeInfo);
    }

    @Override
    public void updateTradeInfo(TradeInfo tradeInfo) {
        getSqlMapClientTemplate().update("updateTradeInfo", tradeInfo);
    }

    @Override
    public TradeInfo selectTradeInfoById(int id) {
        TradeInfo tradeInfo = (TradeInfo) getSqlMapClientTemplate().queryForObject("selectTradeInfo", id);
        return tradeInfo;
    }

    @Override
    public int triggerTradeOrderPaySuccessful(String tradeNo) {
        int rowsEffected = getSqlMapClientTemplate().update("updateTradeOrderPaySuccessful", tradeNo);
        return rowsEffected;
    }

    @Override
    public int queryTradeInfo(String tradeNo, String outerTradeNo) {
        Map map = new HashMap();
        map.put("tradeNo", tradeNo);
        map.put("outerTradeNo", outerTradeNo);
        return (Integer) getSqlMapClientTemplate().queryForObject("queryTradeInfo", map);
    }

    @Override
    public List<Long> queryOrderListByTradeNo(String tradeNo) {
        return getSqlMapClientTemplate().queryForList("queryOrderListByTradeNo", tradeNo);
    }

    /**
     * 根据交易号查询订单号列表,并且是交易成果的
     * <p/>
     * 修改人：Json.zhu
     * 修改时间：2013.12.12
     *
     * @param tradeNo
     * @return
     */
    @Override
    public List<Long> queryOrderListByTradeNoAndPayFlag(String tradeNo) {
        return getSqlMapClientTemplate().queryForList("queryOrderListByTradeNoAndPayFlag", tradeNo);
    }

    @Override
    public void createOrderTradeInfo(String tradeNo, Long[] orderList) {
        Map map = new HashMap();
        map.put("tradeNo", tradeNo);
        for (Long orderNo : orderList) {
            map.put("orderNo", orderNo);
            getSqlMapClientTemplate().insert("createTradeOrder", map);
        }
    }

    @Override
    public void triggerTradeOrderPayRecharge(String tradeNo, Long orderNo) {
        Map map = new HashMap();
        map.put("tradeNo", tradeNo);
        map.put("orderNo", orderNo);
        getSqlMapClientTemplate().update("updateTradeOrderPayRecharge", map);
    }

    @Override
    public String queryOuterTradeNoByOrderNo(long orderNo) {
        String tradeNo = queryTradeNoByOrderNo(orderNo);
        String outerTradeNo = (String) getSqlMapClientTemplate().queryForObject("queryOuterTradeNoByTradeNo", tradeNo);
        return outerTradeNo;
    }

    @Override
    public String queryTradeNoByOrderNo(long orderNo) {
        return (String) getSqlMapClientTemplate().queryForObject("queryTradeNoByOrderNo", orderNo);
    }

    @Override
    public void createRefundTradeOrder(RefundTradeOrder refundTradeOrder) {
        getSqlMapClientTemplate().insert("insertRefundTradeOrder", refundTradeOrder);
    }

    @Override
    public void createRefundTrade(Map map) {
        getSqlMapClientTemplate().insert("insertRefundTrade", map);
    }

    @Override
    public int updateRefundTradeOrderSuccess(String batchNo, String outerTradeNo, long cent) {
        Map map = new HashMap();
        map.put("batchNo", batchNo);
        map.put("outerTradeNo", outerTradeNo);
        map.put("realRefund", cent);
        return getSqlMapClientTemplate().update("updateRefundTradeOrderSuccess", map);

    }

    @Override
    public long getBackIdByBatchNoAndTradeNo(String batchNo, String outerTradeNo) {
        Map map = new HashMap();
        map.put("batchNo", batchNo);
        map.put("outerTradeNo", outerTradeNo);
        return (Long) getSqlMapClientTemplate().queryForObject("getBackIdByBatchNoAndTradeNo", map);
    }

    @Override
    public void createCoupon(Coupon coupon) {
        getSqlMapClientTemplate().insert("insertCoupon", coupon);
    }

    @Override
    public Page<Coupon> queryNotUsedCoupon(Page<Coupon> page, String code, int userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        map.put("code", code);
        map.put("userId", userId);
        List<Coupon> couponList = getSqlMapClientTemplate().queryForList("queryNotUsedCoupon", map);
        page.setResult(couponList);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("queryNotUsedCouponCount", map));
        return page;
    }

    @Override
    public int queryCountNotUsedCouponByUserId(int userId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("queryNotUsedCouponCountByUserId", userId);
    }

    @Override
    public Page<Coupon> queryNotUsedCoupon(int userId, Page<Coupon> page) {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("userId", userId);
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        List<Coupon> couponList = getSqlMapClientTemplate().queryForList("queryNotUsedCouponByUserId", map);
        page.setResult(couponList);
        page.setTotalCount(queryCountNotUsedCouponByUserId(userId));
        return page;
    }

    @Override
    public Page<Coupon> queryUsedCoupon(Page<Coupon> page, String code, int userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        map.put("code", code);
        map.put("userId", userId);
        List<Coupon> couponList = getSqlMapClientTemplate().queryForList("queryUsedCoupon", map);
        page.setResult(couponList);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("queryUsedCouponCount", map));
        return page;
    }

    @Override
    public Page<Coupon> queryExpireCoupon(Page<Coupon> page, String code, int userId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        map.put("code", code);
        map.put("userId", userId);
        List<Coupon> couponList = getSqlMapClientTemplate().queryForList("queryExpireCoupon", map);
        page.setResult(couponList);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("queryExpireCouponCount", map));
        return page;
    }

    @Override
    public Page<Coupon> queryAllUMPayCoupon(Page<Coupon> page, String code) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        map.put("code", code);
        List<Coupon> couponList = getSqlMapClientTemplate().queryForList("queryAllUMPayCoupon", map);
        page.setResult(couponList);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("queryAllUMPayCouponCount", map));
        return page;
    }

    @Override
    public Page<Coupon> queryAllocationCoupon(Page<Coupon> page, String code) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        map.put("code", code);
        List<Coupon> couponList = getSqlMapClientTemplate().queryForList("queryAllocationCoupon", map);
        page.setResult(couponList);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("queryAllocationCouponCount", map));
        return page;
    }

    @Override
    public Coupon queryCouponByCode(String code) {
        return (Coupon) getSqlMapClientTemplate().queryForObject("queryCouponByCode", code);
    }

    @Override
    public Coupon getCouponById(int id) {
        return (Coupon) getSqlMapClientTemplate().queryForObject("queryCouponById", id);
    }

    @Override
    public List<Coupon> getMsgRemindCoupon(int interval) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("intervalBegin", 0 - interval);
        map.put("intervalEnd", 0 - interval - 1);
        return getSqlMapClientTemplate().queryForList("queryMsgRemindCoupon", map);
    }

    @Override
    public List<Coupon> getCanUseCouponByTotalPriceAndUserId(long totalPrice, int userId) {
        Map map = new HashMap();
        map.put("totalPrice", totalPrice);
        map.put("userId", userId);
        return getSqlMapClientTemplate().queryForList("queryCanUseCouponByTotalPriceAndUserId", map);
    }

    @Override
    public int makeCouponUsed(String code, long orderNo) {
        Map param = new HashMap();
        param.put("code", code);
        param.put("orderNo", orderNo);
        return getSqlMapClientTemplate().update("makeCouponUsed", param);
    }

    @Override
    public int updateCoupon(Coupon coupon) {
        return getSqlMapClientTemplate().update("updateCoupon", coupon);
    }

    @Override
    public void deleteCoupon(int id) {
        getSqlMapClientTemplate().delete("deleteCoupon", id);
    }

    @Override
    public List<Coupon> queryCouponByUserId(int couponId) {
        return getSqlMapClientTemplate().queryForList("queryCouponById", couponId);
    }

    /**
     * 分页查询用户的现金券
     *
     * @param couponQuery
     * @return
     */
    @Override
    public Page<Coupon> queryCoupon(CouponQuery couponQuery) {
        Page<Coupon> page = new Page<Coupon>(couponQuery.getPageNo(), couponQuery.getPageSize());
        Map param = new HashMap();
        param.put("userId", couponQuery.getCouponId());
        param.put("couponUsed", couponQuery.getCouponUsed());
        int totalCount = (Integer) getSqlMapClientTemplate().queryForObject("selectCountCoupon", param);
        //如果输入的页数超过总页数，则页数位置为第一页
        if (page.getStart() >= totalCount) {
            page.setPageNo(1);
        }
        page.setTotalCount(totalCount);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        page.setResult(getSqlMapClientTemplate().queryForList("queryCoupon", param));
        return page;
    }

    @Override
    public List<Coupon> queryNotUsedAndNotAssignCoupon(long price, int number) {
        Map param = new HashMap();
        param.put("price", price);
        param.put("number", number);
        return getSqlMapClientTemplate().queryForList("queryNotUsedAndNotAssignCouponGivingPriceAndNumber", param);
    }


    @Override
    public RefundTradeOrder queryRefundTradeOrderByBackGoodsId(long backGoodsId) {
        return (RefundTradeOrder) getSqlMapClientTemplate().queryForObject("queryRefundTradeOrderByBackGoodsId", backGoodsId);
    }

    /**
     * 根据指定的交易方式，支付方式，交易开始时间和交易结束时间来查询
     * 带分页
     * 修改人：Json.zhu
     * 修改时间：2013.12.09，15：13
     *
     * @param tradeQuery
     */
    @Override
    public Page<TradeInfo> getTradeByConditions(TradeQuery tradeQuery) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("bizType", tradeQuery.getBizType());
        param.put("payMethod", tradeQuery.getPayType());

        if (StringUtils.isNotBlank(tradeQuery.getStartDate()))
            param.put("startDate", DateUtils.parseDate(tradeQuery.getStartDate().replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR));
        if (StringUtils.isNotBlank(tradeQuery.getEndDate()))
            param.put("endDate", DateUtils.parseDate(tradeQuery.getEndDate().replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR));
        if (StringUtils.isNotBlank(tradeQuery.getTradeNo())) {
            param.put("tradeNo", tradeQuery.getTradeNo());
        }
        if (StringUtils.isNotBlank(tradeQuery.getOrderNo())) {
            param.put("tradeNo", queryTradeNoByOrderNo(NumberUtils.toLong(tradeQuery.getOrderNo())));
        }

        Page<TradeInfo> page = Page.createFromStart(tradeQuery.getStart(), tradeQuery.getLimit());
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());

        List<TradeInfo> tradeInfoList = getSqlMapClientTemplate().queryForList("queryTradeByConditions", param);
        page.setResult(tradeInfoList);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("queryTradeCountByConditions", param));
        return page;
    }

    /**
     * 根据指定的交易方式，支付方式，交易开始时间和交易结束时间来查询
     * 修改人：Json.zhu
     * 修改时间：2013.12.09，15：13
     *
     * @param tradeQuery
     */
    @Override
    public List<TradeInfo> getTradeListByListConditions(TradeQuery tradeQuery) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("bizType", tradeQuery.getBizType());
        param.put("payMethod", tradeQuery.getPayType());

        if (StringUtils.isNotBlank(tradeQuery.getStartDate()))
            param.put("startDate", DateUtils.parseDate(tradeQuery.getStartDate().replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR));
        if (StringUtils.isNotBlank(tradeQuery.getEndDate()))
            param.put("endDate", DateUtils.parseDate(tradeQuery.getEndDate().replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR));

        if (StringUtils.isNotBlank(tradeQuery.getTradeNo()))
            param.put("tradeNo", tradeQuery.getTradeNo());
        if (StringUtils.isNotBlank(tradeQuery.getOrderNo()))
            param.put("tradeNo", queryTradeNoByOrderNo(NumberUtils.toLong(tradeQuery.getOrderNo())));

        List<TradeInfo> tradeInfoList = getSqlMapClientTemplate().queryForList("queryTradeListByConditions", param);
        return tradeInfoList;
    }

    /**
     * 查询出所有成功退款的项
     * 修改人：Json.zhu
     * 修改时间：2013.12.13，13：02
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<RefundTrade> getRefundTradeAllInfo(String startDate, String endDate) {
        Map<String, Object> param = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(startDate))
            param.put("startDate", DateUtils.parseDate(startDate.replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR));
        if (StringUtils.isNotBlank(endDate))
            param.put("endDate", DateUtils.parseDate(endDate.replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR));

        return getSqlMapClientTemplate().queryForList("queryRefundTradeAllInfo", param);
    }

    /**
     * 根据batchNo查询出所有的退款中的订单项
     * 修改人：Json.zhu
     * 修改时间：2013.12.13，13：32
     *
     * @param batchNo
     * @return
     */
    @Override
    public List<RefundTradeOrder> getBackIdByBatchNo(String batchNo) {
        return getSqlMapClientTemplate().queryForList("queryRefundTradeOrderByBatchNo", batchNo);
    }
}
