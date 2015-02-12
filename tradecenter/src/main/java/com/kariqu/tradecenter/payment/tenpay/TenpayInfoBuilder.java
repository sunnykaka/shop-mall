package com.kariqu.tradecenter.payment.tenpay;

import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.payment.BackInfoBuilder;
import com.kariqu.tradecenter.payment.PayType;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: amos.zhou
 * Date: 13-10-24
 * Time: 上午10:51
 */
public class TenpayInfoBuilder implements BackInfoBuilder {

    @Override
    public TradeInfo buildFromRequest(HttpServletRequest request) {
        String trade_no = request.getParameter("out_trade_no");  //获取订单号
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setTradeNo(trade_no);
        tradeInfo.setPayTotalFee(Long.valueOf(request.getParameter("total_fee")));
        tradeInfo.setGmtCreateTime(new Date());
        tradeInfo.setTradeStatus(request.getParameter("trade_state"));
        tradeInfo.setOuterBuyerAccount(request.getParameter("buyer_alias"));
        tradeInfo.setOuterTradeNo(request.getParameter("transaction_id"));
        tradeInfo.setOuterPlatformType(PayType.TenPay.getValue());
        tradeInfo.setNotifyId(request.getParameter("notify_id"));
        tradeInfo.setNotifyType("redirect");
        return tradeInfo;
    }

    @Override
    public Map<String, String> buildParam(HttpServletRequest request) {
        Map<String, String> params = new TreeMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Object oName : requestParams.keySet()) {
            String name = (String) oName;
            String[] values = (String[]) requestParams.get(name);
            if (values != null && values.length > 0)
                params.put(name, values[0]);
        }
        return params;
    }
}
