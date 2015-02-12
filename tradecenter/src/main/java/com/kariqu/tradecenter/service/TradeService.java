package com.kariqu.tradecenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.RefundTrade;
import com.kariqu.tradecenter.domain.RefundTradeOrder;
import com.kariqu.tradecenter.domain.payment.TradeInfo;

import java.util.List;

/**
 * 交易服务
 *
 * @author : Asion
 * @since 1.0.0
 *        Date: 12-11-15
 *        Time: 下午3:15
 */
public interface TradeService {

    /**
     * 交易成功触发
     *
     * @param tradeInfo
     */
    void triggerTradeOrderPaySuccessful(TradeInfo tradeInfo);

    /**
     * 退款成功触发
     * 返回退款成功的退单号
     *
     * @param tradeNo
     * @param successNum
     * @param result_details
     */
    List<Long> triggerRefundSuccess(String tradeNo, String successNum, String result_details);


    /**
     * 根据订单号查询到订单的交易号
     *
     * @param orderNo
     * @return
     */
    String queryOuterTradeNoByOrderNo(long orderNo);


    /**
     * 根据交易流水号创建订单的交易信息
     *
     * @param tradeNo
     * @param orderList
     */
    void createOrderTradeInfo(String tradeNo, Long[] orderList);


    /**
     * 根据交易号查询订单号列表
     *
     * @param tradeNo
     * @return
     */
    List<Long> queryOrderListByTradeNo(String tradeNo);

    /**
     * 根据交易号查询订单号列表,并且是交易成果的
     * 修改人：Json.zhu
     * 修改时间：2013.12.12
     * @param tradeNo
     * @return
     */
    List<Long> queryOrderListByTradeNoAndPayFlag(String tradeNo);


    void triggerTradeOrderPayRecharge(String tradeNo, long orderNo);

    /**
     * 算出一批订单的总价
     *
     * @param orderNo
     * @return
     */
    String getPayPriceForYuan(Long[] orderNo);

    boolean existTradeInfo(String tradeNo, String outerTradeNo);

    /**
     * 创建退款交易单
     *
     * @param refundTradeOrder
     */
    void createRefundTradeOrder(RefundTradeOrder refundTradeOrder);

    RefundTradeOrder queryRefundTradeOrderByBackGoodsId(long backGoodsId);


    long getPayPriceForCent(Long[] orderNoList);

    /**
     * 根据指定的(交易方式，支付方式，交易开始时间和交易结束时间)条件来查询
     * 带分分页的
     * 修改人：Json.zhu
     * 修改时间：2013.12.09，15：13
     * @param tradeQuery
     */
    Page<TradeInfo> getTradeByConditions(TradeQuery tradeQuery);

    /**
     * 根据指定的(交易方式，支付方式，交易开始时间和交易结束时间)条件来查询
     * 修改人：Json.zhu
     * 修改时间：2013.12.09，15：13
     * @param tradeQuery
     */
    List<TradeInfo> getTradeByListConditions(TradeQuery tradeQuery);

    /**
     * 查询出所有成功退款的项
     * 修改人：Json.zhu
     * 修改时间：2013.12.13，13：02
     * @param startDate
     * @param endDate
     * @return
     */
    List<RefundTrade> getRefundTradeAllInfo(String startDate, String endDate);

    /**
     * 根据batchNo查询出所有的退款中的订单项
     * 修改人：Json.zhu
     * 修改时间：2013.12.13，13：32
     * @param batchNo
     * @return
     */
    List<RefundTradeOrder> getBackIdByBatchNo(String batchNo);
}
