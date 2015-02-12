package com.kariqu.tradecenter.payment.alipay;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-15
 *        Time: 下午5:42
 */
public class AlipayUtil {

    private static final Logger TRADE_LOGGER = Logger.getLogger("alipayTradeLog");

    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // 合作身份者ID，以2088开头由16位纯数字组成的字符串,ejs partner id
    public static String partner = "2088801555613602";

    // 交易安全检验码，由数字和字母组成的32位字符串
    public static String key = "cf92qjldwsnp9cxrsspzzuj01zjd2jq1";

    // 签约支付宝账号或卖家收款支付宝帐户
    public static String seller_email = "fd@yijushang.cn";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String input_charset = "UTF-8";

    // 签名方式 不需修改
    public static String sign_type = "MD5";

    public static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";

    private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";


    public static boolean verify(String reqType, Map<String, String> params) {
        Map<String, String> sParaNew = paraFilter(params);
        String mysign = buildMysign(sParaNew);
        String responseTxt = "true";
        if (params.get("notify_id") != null) {
            responseTxt = verifyResponse(params.get("notify_id"));
        }
        String sign = "";
        if (params.get("sign") != null) {
            sign = params.get("sign");
        }

        TRADE_LOGGER.warn(String.format("请求类型: %s\n 响应结果: %s\n notify_url_log:sign=%s&mysign=%s\n 返回参数：%s",
                reqType, responseTxt, sign, mysign, createLinkString(params)));

        //验证
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //mysign与sign不等，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        return mysign.equals(sign) && responseTxt.equals("true");
    }

    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }


    /**
     * 获取远程服务器ATN结果,验证返回URL
     *
     * @param notify_id 通知校验ID
     * @return 服务器ATN结果
     *         验证结果集：
     *         invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     *         true 返回正确信息
     *         false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static String verifyResponse(String notify_id) {
        //获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求
        return checkUrl(HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notify_id);
    }

    /**
     * 获取远程服务器ATN结果
     *
     * @param urlvalue 指定URL路径地址
     * @return 服务器ATN结果
     *         验证结果集：
     *         invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     *         true 返回正确信息
     *         false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static String checkUrl(String urlvalue) {
        String inputLine;
        try {
            URL url = new URL(urlvalue);
            TRADE_LOGGER.warn("向 ATN 验证发送的请求: " + urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection
                    .getInputStream()));
            inputLine = in.readLine();
        } catch (Exception e) {
            TRADE_LOGGER.error(String.format("向 ATN 验证时请求(%s)失败: %s", urlvalue, e.getMessage()));
            inputLine = "";
        }
        return inputLine;
    }


    public static String buildMysign(Map<String, String> sArray) {
        String prestr = createLinkString(sArray);
        prestr = prestr + key;
        return md5(prestr, input_charset);
    }


    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    /**
     * 对字符串进行MD5签名
     *
     * @param text 明文
     * @return 密文
     */
    public static String md5(String text, String charset) {
        return DigestUtils.md5Hex(getContentBytes(text, charset));
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }

        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

}
