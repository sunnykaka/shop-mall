package com.kariqu.tradecenter.payment;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 回调处理值的返回值
 * User: amos.zhou
 * Date: 13-10-24
 * Time: 下午6:02
 */
public class CallBackResult {

    private boolean result;
    private Map<String, Object> data = new HashMap<String, Object>();
    private String successUrl;
    private String failureUrl;


    /**
     * 跳转至下一个处理流程
     * @return
     */
    public String skipToNextProcess() {
        if(StringUtils.isEmpty(this.successUrl) || StringUtils.isEmpty(this.failureUrl)){
            throw new IllegalArgumentException("没有设置待跳转的Url，不能进行跳转。请确保successUrl与failureUrl已被正确初始化");
        }
        if (success()) {
            return this.successUrl;
        }
        return this.failureUrl;
    }


    /**
     * 成功时跳转的URL 与 失败时跳转的URL不同时采用此构造函数
     * @param successUrl    成功跳转的URL
     * @param failureUrl    失败时跳转的URL
     */
    public CallBackResult(String successUrl, String failureUrl) {
        this.successUrl = successUrl;
        this.failureUrl = failureUrl;
    }


    /**
     * 成功 与失败时采用同一个URL
     * @param successUrl
     */
    public CallBackResult(String successUrl) {
        this.successUrl = successUrl;
    }

    /**
     * 不管成功与失败，不需要跳转页面，也就不需要successUrl与failureUrl。 比如与前端交互用Ajax
     */
    public CallBackResult() {
    }

    public boolean success() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object getData(String key){
        if(data == null){
            return null;
        }
        return data.get(key);
    }


    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getFailureUrl() {
        return failureUrl;
    }

    public void setFailureUrl(String failureUrl) {
        this.failureUrl = failureUrl;
    }
}
