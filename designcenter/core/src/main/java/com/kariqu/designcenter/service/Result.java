package com.kariqu.designcenter.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务结果
 *
 * @author Tiger
 * @version 1.0
 * @since 12-4-20 上午11:55
 */
public class Result {

    private boolean success;

    private String message;

    private Exception exception;

    private Map<String, String> data = new HashMap<String, String>();


    public Result(boolean success, String message, Exception exception) {
        this.success = success;
        this.message = message;
        this.exception = exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Exception getException() {
        return exception;
    }

    public void addDataEntry(String name, String value) {
        data.put(name, value);
    }

    public String getDataEntry(String name) {
        return data.get(name);
    }



}
