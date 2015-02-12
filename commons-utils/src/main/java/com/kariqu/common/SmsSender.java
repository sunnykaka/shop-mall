package com.kariqu.common;

import com.kariqu.common.http.Request;
import com.kariqu.common.http.Response;
import com.kariqu.common.http.Verb;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 短信发送
 * User: Asion
 * Date: 13-7-31
 * Time: 下午2:58
 */
public class SmsSender {

    private static final Log LOG = LogFactory.getLog("SmsLog");

    private static final String key = "3SDK-EMY-0130-PHTLM";
    private static final String pwd = "308084";

    /**
     * 短信发送
     *
     * @param phone 电话号码
     * @param content 短信内容
     * @return 短信发送后的结果
     */
    public static String sendSms(String phone, String content) {
        return sendTimeSms(phone, content, StringUtils.EMPTY);
    }

    /**
     * 发送定时短信
     *
     * @param phone 电话号码
     * @param content 短信内容
     * @param sendTime 发送时间
     * @return 短信发送后的结果
     */
    public static String sendTimeSms(String phone, String content, String sendTime) {
        Request request = new Request(Verb.POST, "http://sdkhttp.eucp.b2m.cn/sdkproxy/sendtimesms.action");

        request.addQuerystringParameter("cdkey", key);
        request.addQuerystringParameter("password", pwd);
        request.addQuerystringParameter("phone", phone);
        request.addQuerystringParameter("message", content);
        if (StringUtils.isNotBlank(sendTime))
            request.addQuerystringParameter("sendtime", sendTime);

        LOG.warn("短信发送:" + request.toString());
        Response send = request.send();

        String resultXml = send.getBody().trim();
        LOG.warn("短信发送结果:" + resultXml);

        return resultXml;
    }

}
