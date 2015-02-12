package com.kariqu.tradecenter.domain;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2012-09-26 10:43
 * @since 1.0.0
 */
public class BackResult {

    private String message;
    private int state;
    private int status;
    private String com;
    private String nu;
    private List<LinkedHashMap<String, String>> data;
    private String condition;

    /**
     * 是否签收标记
     */
    private int ischeck;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * 是否签收标记
     */
    public int getIscheck() {
        return ischeck;
    }

    /**
     * 是否签收标记
     */
    public void setIscheck(int ischeck) {
        this.ischeck = ischeck;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getNu() {
        return nu;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public List<LinkedHashMap<String, String>> getData() {
        return data;
    }

    public void setData(List<LinkedHashMap<String, String>> data) {
        this.data = data;
    }

}
