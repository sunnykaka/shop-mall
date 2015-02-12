package com.kariqu.tradecenter.domain;

import java.util.Map;

/**
 * 向 第三方物流查询平台(快递100) 发送的对象.
 *
 * @author Athens(刘杰)
 * @Time 2012-09-25 10:21
 * @since 1.0.0
 */
public class PutMsg {
    private String company;
    private String number;
    private String from;
    private String to;
    private String key;
    private Map<String, String> parameters;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}