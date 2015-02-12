package com.kariqu.tradecenter.domain;

import com.kariqu.common.json.JsonUtil;
import com.kariqu.common.uri.URLBrokerFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 组装物流信息<br/>
 * Time 2012-09-24 16:38
 *
 * @author Athens(刘杰)
 * @since 1.0.0
 */
public class PackageLogistics {

    private static final Log LOGGER = LogFactory.getLog("LogisticsLog");

    private URLBrokerFactory urlBrokerFactory;

    /**
     * 第三方物流查询的请求地址
     */
    private String thirdUrl;

    /**
     * 与第三方物流查询平台协商的用于校验用户的 key
     */
    private String key;

    /**
     * 请求及响应的数据模式, json 或是 xml, 默认是 json
     */
    private String schema = "json";

    /**
     * 请求的超时时间(单位, 毫秒. 1 秒则 1 * 1000)
     */
    private static final int TIME_OUT = 10 * 1000;

    private static final String ENCODE = "UTF-8";

    private boolean online = false;

    /**
     * 向 第三方物流查询平台(快递100) 发送请求. 响应异步返回数据
     *
     * @param company 快递公司
     * @param number  物流单号
     * @param from    从哪来
     * @param to      到哪去
     * @return 收到成功信息则返回 true.
     */
    public boolean requestThirdLogistics(String company, String number, String from, String to) {
        if (!online) return false;

        PutMsg put = new PutMsg();
        put.setCompany(company);
        put.setNumber(number);
        put.setFrom(StringUtils.isBlank(from) ? "" : from);
        put.setTo(StringUtils.isBlank(to) ? "" : to);
        put.setKey(key);

        Map<String, String> map = new HashMap<String, String>();
        map.put("callbackurl", getUrlBrokerFactory().getUrl("LogisticsCallBackUrl").toString());
        put.setParameters(map);

        // post 数据.
        String putMsg;
        try {
            putMsg = JsonUtil.objectToJson(put);
        } catch (Exception e) {
            LOGGER.error("转换请求数据时异常", e);
            return false;
        }

        ResponseMsg res = null;
        try {
            String responseMsg = post(putMsg);
            if (StringUtils.isNotBlank(responseMsg))
                res = JsonUtil.json2Object(responseMsg, ResponseMsg.class);
        } catch (Exception e) {
            LOGGER.error("接收三方物流查询平台时异常", e);
            return false;
        }

        if (res == null) {
            LOGGER.warn("第三方物流查询平台未返回数据!");
            return false;
        }
        if (res.getReturnCode() != 200) {
            LOGGER.warn("第三方物流查询平台未返回<成功>数据");
            return false;
        }
        return true;
    }

    /**
     * 请求数据.
     *
     * @param msg 请求的 json 数据
     * @return 返回的 json 数据, 若异常则返回空
     */
    private String post(String msg) throws Exception {
        StringBuilder sbd = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(thirdUrl).openConnection();
            // 超时时间
            conn.setConnectTimeout(TIME_OUT);
            // method
            conn.setRequestMethod("POST");
            // 忽略缓存
            conn.setUseCaches(false);
            // 可以输出
            conn.setDoOutput(true);

            conn.connect();

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            // 传递数据.
            String content = "schema=" + getSchema() + "&param=" + msg;
            out.write(content.getBytes(ENCODE));
            out.flush();
            out.close();

            // 读取返回数据, 在 getInputStream 时才会真正进行数据的请求
            LOGGER.warn("向第三方查询发送请求: [" + thirdUrl + "], 数据是: [" + content + "].");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), ENCODE));
            String line;
            while ((line = br.readLine()) != null) {
                sbd.append(line);
            }
            LOGGER.warn("第三方查询即时返回的数据是: " + sbd.toString());
        } finally {
            if (conn != null)
                conn.disconnect();
        }
        return sbd.toString();
    }

    public String getThirdUrl() {
        return thirdUrl;
    }

    public void setThirdUrl(String thirdUrl) {
        this.thirdUrl = thirdUrl;
    }

    public URLBrokerFactory getUrlBrokerFactory() {
        return urlBrokerFactory;
    }

    public void setUrlBrokerFactory(URLBrokerFactory urlBrokerFactory) {
        this.urlBrokerFactory = urlBrokerFactory;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
