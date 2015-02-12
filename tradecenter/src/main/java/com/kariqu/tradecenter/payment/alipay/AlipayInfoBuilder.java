package com.kariqu.tradecenter.payment.alipay;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.domain.payment.TradeInfo;
import com.kariqu.tradecenter.payment.BackInfoBuilder;
import com.kariqu.tradecenter.payment.PayType;
import com.kariqu.tradecenter.service.impl.PayMethod;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: amos.zhou
 * Date: 13-10-24
 * Time: 上午10:03
 */
public class AlipayInfoBuilder implements BackInfoBuilder {

    private static final Logger ALIPAY_LOGGER = Logger.getLogger("alipayTradeLog");


    @Override
    /**
     * 修改人：Json
     * 修改时间：2013.12.06，18.20
     * why：添加了业务方式和银行
     */
    public TradeInfo buildFromRequest(HttpServletRequest request) {
        String order_no = request.getParameter("out_trade_no");  //获取订单号
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setTradeNo(order_no);
        Money money = new Money(request.getParameter("total_fee"));
        tradeInfo.setPayTotalFee(money.getCent());
        tradeInfo.setGmtCreateTime(new Date());
        tradeInfo.setTradeStatus(request.getParameter("trade_status"));
        tradeInfo.setOuterBuyerAccount(request.getParameter("buyer_email"));
        tradeInfo.setOuterTradeNo(request.getParameter("trade_no"));
        tradeInfo.setOuterPlatformType(PayType.Alipay.getValue());
        String extra_common_param = request.getParameter("extra_common_param");//阿里的支付回传参数
        String[] split = extra_common_param.split("\\|");

        ALIPAY_LOGGER.warn("支付宝回传的 extra_common_param 参数是: " + extra_common_param);

        /**
         * 修改原因：添加一个业务类型（是订单还是优惠券）
         * 修改人：Json.zhu
         * 修改时间：2013.12.09
         */
        if (split.length > 1)
            tradeInfo.setBizType(split[1]);  //设置业务方式，是order还是coupon
        /**
         * 修改原因：request.getParameter("defaultbank")无法正确设置值，
         * 修改人：Json.zhu
         * 修改时间：2013.12.09
         */
        if (split.length > 2)
            tradeInfo.setDefaultbank(split[2]);
        //tradeInfo.setDefaultbank(request.getParameter("defaultbank"));//设置银行

        String bank_seq_no = request.getParameter("bank_seq_no");
        tradeInfo.setPayMethod(bank_seq_no == null ? PayMethod.directPay.toString() : PayMethod.bankPay.toString());
        tradeInfo.setOuterBuyerId(request.getParameter("buyer_id"));
        tradeInfo.setNotifyId(request.getParameter("notify_id"));
        tradeInfo.setNotifyType(request.getParameter("notify_type"));
        return tradeInfo;
    }

    @Override
    public Map<String, String> buildParam(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Object oName : requestParams.keySet()) {
            String name = (String) oName;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }
}
