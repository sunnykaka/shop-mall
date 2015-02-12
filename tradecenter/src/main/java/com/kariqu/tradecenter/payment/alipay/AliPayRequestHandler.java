package com.kariqu.tradecenter.payment.alipay;

import com.kariqu.common.DateUtils;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.payment.PayInfoWrapper;
import com.kariqu.tradecenter.payment.PayRequestHandler;
import com.kariqu.tradecenter.service.impl.PayMethod;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-15
 *        Time: 下午3:25
 */
public class AliPayRequestHandler extends PayRequestHandler {

    private static final Logger TRADE_LOGGER = Logger.getLogger("alipayTradeLog");//阿里支付

    public static final int PAYMENT_TYPE_BUY_PRODUCT = 1;    //商品购买


    @Override
    protected String getPaymentURL() {
        return  AlipayUtil.ALIPAY_GATEWAY_NEW;
    }


    @Override
    protected void doSign(Map<String, String> params) {
        //生成签名结果
        String mysign = AlipayUtil.buildMysign(params);
        //签名结果与签名方式加入请求提交参数组中
        params.put("sign", mysign);
        params.put("sign_type", AlipayUtil.sign_type);
    }

    @Override
    protected Logger getLog() {
        return TRADE_LOGGER;
    }

    @Override
    protected Map<String, String> buildPayParam(PayInfoWrapper payInfoWrapper) {

        //************订单参数***************//
        //请与贵网站订单系统中的唯一订单号匹配
        String out_trade_no = payInfoWrapper.getTradeNo();


        //订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
        String subject = "易居尚-购物编号-" + out_trade_no;


        //************/订单参数***************//

        //扩展功能参数——默认支付方式//

        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("payment_type", String.valueOf(PAYMENT_TYPE_BUY_PRODUCT));
        sParaTemp.put("out_trade_no", out_trade_no);
        sParaTemp.put("subject", subject);
        sParaTemp.put("body", subject);
        //完成分到圆的转换
        Money money = new Money();
        money.setCent(payInfoWrapper.getTotalFee());
        sParaTemp.put("total_fee", money.toString());
        sParaTemp.put("partner", AlipayUtil.partner);
        sParaTemp.put("seller_email", AlipayUtil.seller_email);
        sParaTemp.put("service", "create_direct_pay_by_user");
        sParaTemp.put("_input_charset", AlipayUtil.input_charset);
        //回调class
        sParaTemp.put("extra_common_param",payInfoWrapper.getCallBackClass()+"|"+payInfoWrapper.getBizType()+"|"+payInfoWrapper.getDefaultbank());

        if (payInfoWrapper.isAlipay()) {
            sParaTemp.put("paymethod", PayMethod.directPay.toString());
        } else if (payInfoWrapper.isBank()) {
            sParaTemp.put("defaultbank", payInfoWrapper.getDefaultbank());
            sParaTemp.put("paymethod", PayMethod.bankPay.toString());
        } else {
            throw new RuntimeException("不支持的支付类型");
        }

        return sParaTemp;
    }


    public static Map<String, String> buildRefundParam(String batchNo, String outerTradeNo, String price, String reason) {
        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("seller_email", AlipayUtil.seller_email);
        sParaTemp.put("partner", AlipayUtil.partner);
        sParaTemp.put("seller_user_id", AlipayUtil.partner);
        //批次号是日期加上退单号,退单号要大于2为数，小于25位数
        sParaTemp.put("batch_no", batchNo);
        sParaTemp.put("refund_date", DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
        sParaTemp.put("batch_num", "1");
        sParaTemp.put("detail_data", outerTradeNo + "^" + price + "^" + reason);
        sParaTemp.put("notify_url", payProp.getProperty(DEFAULT_REDFUND_URL_KEY));
        sParaTemp.put("service", "refund_fastpay_by_platform_pwd");
        sParaTemp.put("_input_charset", AlipayUtil.input_charset);
        //生成签名结果
        String mysign = AlipayUtil.buildMysign(sParaTemp);

        //签名结果与签名方式加入请求提交参数组中
        sParaTemp.put("sign", mysign);
        sParaTemp.put("sign_type", AlipayUtil.sign_type);
        //构造函数，生成请求URL
        return sParaTemp;
    }
}
