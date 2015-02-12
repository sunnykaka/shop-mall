package com.kariqu.tradecenter.payment;

import com.kariqu.common.prop.PropertiesUtil;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * 支付服务
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-15
 *        Time: 下午3:22
 *        <p/>
 *        修改人：amos.zhou
 *        修改时间：2013.10.22
 */
public abstract class PayRequestHandler {

    private static final String PAYMENT_PROPERTIES_LOCATION = "payment.properties";


    protected static Properties payProp = null;
    /**
     * 默认的reutrn url
     */
    protected static final String DEFAULT_RETURN_URL_KEY = "return.url";

    /**
     *  默认的notify url
     */
    protected static final String DEFAULT_NOTIFY_URL_KEY = "notify.url";

    protected static final String DEFAULT_REDFUND_URL_KEY = "refund.notify.url" ;

    /**
     * 初始化加载return.url和notify.url
     */
    static {
        try {
            payProp = PropertiesUtil.loadProperties(PAYMENT_PROPERTIES_LOCATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public final String forwardToPay(PayInfoWrapper payInfoWrapper) {
        Map<String, String> params = buildPayParam(payInfoWrapper);
        //返回处理的Url
        String returnUrl =  payProp.getProperty(DEFAULT_RETURN_URL_KEY);
        //notify处理的Url
        String notifyUrl = payProp.getProperty(DEFAULT_NOTIFY_URL_KEY);
        Assert.notNull(returnUrl,"必须要在payment.properties中配置return url");
        Assert.notNull(notifyUrl,"必须要在payment.properties中配置notify url");
        params.put("return_url",returnUrl);
        params.put("notify_url",notifyUrl);
        //签名，并追加签名参数
        doSign(params);
        return buildPaymentString(params);

    }

    protected String buildPaymentString(Map<String, String> params) {
        StringBuilder sbHtml = new StringBuilder();
        sbHtml.append("<form id=\"payForm\" name=\"payForm\" action=\"" + getPaymentURL() + "\" "
                + "method=\"get\">");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sbHtml.append("<input type=\"hidden\" name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\"/>");
        }
        sbHtml.append("<input type=\"submit\" value=\"" + "确认" + "\" style=\"display:none;\"></form>");
        sbHtml.append("<script>document.forms['payForm'].submit();</script>");
        if (getLog().isInfoEnabled())
            getLog().info("向 (" + getPaymentURL() + ") 发送请求 : " + params);
        return sbHtml.toString();
    }


    protected abstract Map<String, String> buildPayParam(PayInfoWrapper payInfoWrapper);

    protected abstract String getPaymentURL();


    protected abstract void doSign(Map<String, String> params);


    protected abstract Logger getLog();

}
