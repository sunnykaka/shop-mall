package com.kariqu.tradecenter.payment.tenpay;

import com.kariqu.common.DateUtils;
import com.kariqu.tradecenter.payment.PayInfoWrapper;
import com.kariqu.tradecenter.payment.PayRequestHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Tiger
 * @version 1.0
 * @since 13-1-21 下午3:33
 */
public class TenPayRequestHandler extends PayRequestHandler {

    private static final Logger TRADE_LOGGER  = Logger.getLogger("tenpayTradeLog"); //腾讯支付

    @Override
    protected Logger getLog() {
        return TRADE_LOGGER;
    }

    @Override
    protected String getPaymentURL() {
        return TenpayUtils.PAY_GATEWAY;
    }


    @Override
    protected void doSign(Map<String, String> params) {
        //签名结果与签名方式加入请求提交参数组中
        params.put("sign", TenpayUtils.buildMysign(params));
    }


    @Override
    protected Map<String, String> buildPayParam(PayInfoWrapper payInfoWrapper) {
        //必填参数//

        //************订单参数***************//
        //请与贵网站订单系统中的唯一订单号匹配
        String out_trade_no = payInfoWrapper.getTradeNo();


        //订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
        String subject = "易居尚-购物编号-" + out_trade_no;




        //订单总金额，显示在支付宝收银台里的“应付总额”里 ,以分为单位
        long total_fee = payInfoWrapper.getTotalFee();
        //************/订单参数***************//

        //扩展功能参数——默认支付方式//

        //把请求参数打包成数组
        Map<String, String> sParaTemp = new TreeMap<String, String>();
        sParaTemp.put("partner", TenpayUtils.PARTNER);
        sParaTemp.put("out_trade_no", out_trade_no);
        sParaTemp.put("total_fee", String.valueOf(total_fee));

        sParaTemp.put("body", subject);
        sParaTemp.put("bank_type", "DEFAULT");
        sParaTemp.put("spbill_create_ip", "119.137.111.192");
        sParaTemp.put("fee_type", "1");
        sParaTemp.put("subject", subject);
        sParaTemp.put("sign_type", TenpayUtils.SIGN_TYPE);
        sParaTemp.put("service_version", TenpayUtils.SERVICE_VERSION);
        sParaTemp.put("input_charset", TenpayUtils.INPUT_CHARSET);
        sParaTemp.put("sign_key_index", "1");

        sParaTemp.put("attach", payInfoWrapper.getCallBackClass());
        sParaTemp.put("product_fee", String.valueOf(total_fee));
        sParaTemp.put("transport_fee", "0");
        sParaTemp.put("time_start", DateUtils.formatDate(new Date(), DateUtils.DateFormatType.SIMPLE_DATE_TIME_FORMAT_STR));
        sParaTemp.put("time_expire", StringUtils.EMPTY);
        sParaTemp.put("buyer_id", StringUtils.EMPTY);
        sParaTemp.put("goods_tag", StringUtils.EMPTY);
        sParaTemp.put("trade_mode", "1");
        sParaTemp.put("transport_desc", StringUtils.EMPTY);
        sParaTemp.put("trans_type", "1");
        sParaTemp.put("agentid", StringUtils.EMPTY);
        sParaTemp.put("agent_type", "0");
        sParaTemp.put("seller_id", StringUtils.EMPTY);

        return sParaTemp;
    }


}
