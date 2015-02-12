package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.domain.RefundTrade;
import com.kariqu.tradecenter.domain.RefundTradeOrder;
import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.excepiton.OrderTransactionalException;
import com.kariqu.tradecenter.repository.OrderRepository;
import com.kariqu.tradecenter.repository.TradeRepository;
import com.kariqu.tradecenter.service.TradeQuery;
import com.kariqu.tradecenter.service.TradeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-15
 *        Time: 下午3:16
 */
public class TradeServiceImpl implements TradeService {

    private static Log logger = LogFactory.getLog(TradeServiceImpl.class);

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private OrderRepository orderRepository;


    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void triggerTradeOrderPaySuccessful(TradeInfo tradeInfo) {
        tradeRepository.triggerTradeOrderPaySuccessful(tradeInfo.getTradeNo());
    }

    @Override
    @Transactional
    public List<Long> triggerRefundSuccess(String batchNo, String successNum, String result_details) {
        List<Long> idList = new LinkedList<Long>();

        //得到批量退款信息
        String[] backs = result_details.split("#");

        Money realTotalRefund = new Money();

        int totalSuccess = 0;

        //循环每一批退款
        for (String back : backs) {
            String[] infos = back.split("\\^");
            if ("SUCCESS".equalsIgnoreCase(infos[2])) {
                Money money = new Money(infos[1]);
                realTotalRefund = realTotalRefund.add(money);
                int updateSuccess = tradeRepository.updateRefundTradeOrderSuccess(batchNo, infos[0], money.getCent());
                if (updateSuccess == 1) {
                    long backId = tradeRepository.getBackIdByBatchNoAndTradeNo(batchNo, infos[0]);
                    idList.add(backId);
                    totalSuccess++;
                }
            } else {
                logger.error("发现退款失败,result_details=" + result_details + "批次号是:" + batchNo);
            }

        }
        if (totalSuccess > 0) {
            Map trade = new HashMap();
            trade.put("batchNo", batchNo);
            trade.put("successNum", successNum);
            trade.put("realRefund", realTotalRefund.getCent());
            tradeRepository.createRefundTrade(trade);
        }
        return idList;
    }

    @Override
    public String queryOuterTradeNoByOrderNo(long orderNo) {
        return tradeRepository.queryOuterTradeNoByOrderNo(orderNo);
    }

    @Override
    public void createOrderTradeInfo(String tradeNo, Long[] orderList) {
        tradeRepository.createOrderTradeInfo(tradeNo, orderList);
    }

    @Override
    public List<Long> queryOrderListByTradeNo(String tradeNo) {
        return tradeRepository.queryOrderListByTradeNo(tradeNo);
    }

    /**
     * 根据交易号查询订单号列表,并且是交易成果的
     *
     * 修改人：Json.zhu
     * 修改时间：2013.12.12
     * @param tradeNo
     * @return
     */
    @Override
    public List<Long> queryOrderListByTradeNoAndPayFlag(String tradeNo){
        return tradeRepository.queryOrderListByTradeNoAndPayFlag(tradeNo);
    }

    @Override
    public void triggerTradeOrderPayRecharge(String tradeNo, long orderNo) {
        tradeRepository.triggerTradeOrderPayRecharge(tradeNo, orderNo);
    }

    @Override
    public String getPayPriceForYuan(Long[] orderNoList) {
        Money money = new Money();
        for (long orderNo : orderNoList) {
            String orderPrice = orderRepository.getOrderPriceByOrderNo(orderNo);
            Money toAdd = new Money(orderPrice);
            money = money.add(toAdd);
        }
        return money.toString();
    }

    @Override
    public long getPayPriceForCent(Long[] orderNoList) {
        Money money = new Money();
        for (long orderNo : orderNoList) {
            String orderPrice = orderRepository.getOrderPriceByOrderNo(orderNo);
            Money toAdd = new Money(orderPrice);
            money = money.add(toAdd);
        }
        return money.getCent();
    }



    @Override
    public boolean existTradeInfo(String tradeNo, String outerTradeNo) {
        return tradeRepository.queryTradeInfo(tradeNo, outerTradeNo) == 1;
    }

    @Override
    @Transactional
    public void createRefundTradeOrder(RefundTradeOrder refundTradeOrder) {
        tradeRepository.createRefundTradeOrder(refundTradeOrder);
    }

    @Override
    public RefundTradeOrder queryRefundTradeOrderByBackGoodsId(long backGoodsId) {
        return tradeRepository.queryRefundTradeOrderByBackGoodsId(backGoodsId);
    }

    /**
     * 根据指定的交易方式，支付方式，交易开始时间和交易结束时间来查询
     * 带分页的
     * 修改人：Json.zhu
     * 修改时间：2013.12.09，15：13
     * @param tradeQuery
     */
    @Override
    public Page<TradeInfo> getTradeByConditions(TradeQuery tradeQuery){
        return tradeRepository.getTradeByConditions(tradeQuery);
    }

    /**
     * 根据指定的交易方式，支付方式，交易开始时间和交易结束时间来查询
     * 修改人：Json.zhu
     * 修改时间：2013.12.09，15：13
     * @param tradeQuery
     */
    @Override
    public  List<TradeInfo> getTradeByListConditions(TradeQuery tradeQuery){
        return tradeRepository.getTradeListByListConditions(tradeQuery);
    }

    /**
     * 查询出所有成功退款的项
     * 修改人：Json.zhu
     * 修改时间：2013.12.13，13：02
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public  List<RefundTrade> getRefundTradeAllInfo(String startDate, String endDate){
        return tradeRepository.getRefundTradeAllInfo(startDate,endDate);
    }

    /**
     * 根据batchNo查询出所有的退款中的订单项
     * 修改人：Json.zhu
     * 修改时间：2013.12.13，13：32
     * @param batchNo
     * @return
     */
    @Override
    public List<RefundTradeOrder> getBackIdByBatchNo(String batchNo){
        return tradeRepository.getBackIdByBatchNo(batchNo);
    }

}

