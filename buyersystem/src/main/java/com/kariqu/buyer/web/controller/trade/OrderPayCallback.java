package com.kariqu.buyer.web.controller.trade;

import com.kariqu.common.ApplicationContextUtils;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.payment.CallBackResult;
import com.kariqu.tradecenter.payment.PayCallback;
import com.kariqu.tradecenter.payment.ResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * User: amos.zhou
 * Date: 13-10-24
 * Time: 下午3:32
 */
public class OrderPayCallback implements PayCallback {

    private static Log logger = LogFactory.getLog(OrderPayCallback.class);

    public static final String PAY_SUCCESS = "/order/paySuccess";
    public static final String PAY_FAIL = "/order/payFail";
    private TradeCenterUserClient tradeCenterUserClient = ApplicationContextUtils.getBean(TradeCenterUserClient.class);

    @Override
    public CallBackResult  initResult(TradeInfo tradeInfo, ResponseType type){
        logger.info("「订单回调」开始初始化CallBackResult");
        CallBackResult result = new CallBackResult(PAY_SUCCESS, PAY_FAIL);
        long payTotalFee = tradeInfo.getPayTotalFee();
        Money money = new Money();
        money.setCent(payTotalFee);
        List<Order> orderList = tradeCenterUserClient.queryOrdersByTradeNo(tradeInfo.getTradeNo());
        result.addData("orders", orderList);
        result.addData("total_fee", money.toString());
        logger.info("「订单回调」CallBackResult初始化Success");
        return result;
    }


    @Override
    public boolean doAfterBack(TradeInfo tradeInfo, ResponseType type, CallBackResult result) {
        logger.info("「订单回调」开始更新订单状态");
        try {
            tradeCenterUserClient.changeOrderStateWhenPaySuccess(tradeInfo);

            List<Order> orderList = tradeCenterUserClient.queryOrdersByTradeNo(tradeInfo.getTradeNo());
            result.addData("orders", orderList);
        }catch (Exception e) {
            logger.error("「订单回调」订单状态更新失败.." + e.getMessage());
            //Todo  如果数据库操作出了问题，是非常严重的问题，应该及时马上进行紧急处理。 2013-11-04
            return false;
        }
        return true;
    }

}
