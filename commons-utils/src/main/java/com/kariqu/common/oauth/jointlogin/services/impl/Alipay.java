package com.kariqu.common.oauth.jointlogin.services.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;


/**
 * 支付宝登录
 * User: Alec
 * Date: 13-6-4
 * Time: 下午1:12
 */
public class Alipay {

    private static String partner = "2088801555613602";

    private static String key = "cf92qjldwsnp9cxrsspzzuj01zjd2jq1";

    private static String input_charset = "utf-8";

    private static String sign_type = "MD5";

    private static String return_url = "http://www.yijushang.com/oauth/alipay";

    private static final String AUTHORIZE_URL = "https://mapi.alipay.com/gateway.do?";

    /**
     * 获取支付宝登录访问地址
     *
     * @return
     */
    public static String getAuthorizationUrl() {
        return AUTHORIZE_URL + getParametersWithSign();
    }

    /**
     * 获取代签名的参数
     *
     * @return
     */
    private static String getParametersWithSign() {
        String parameters_without_sign = "_input_charset=" + input_charset + "&partner=" + partner + "&return_url=" + return_url + "&service=alipay.auth.authorize&target_service=user.auth.quick.login";
        String sign = DigestUtils.md5Hex(getContentBytes(parameters_without_sign + key, input_charset));
        return parameters_without_sign + "&sign=" + sign + "&sign_type=" + sign_type;
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (StringUtils.isBlank(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }
}
