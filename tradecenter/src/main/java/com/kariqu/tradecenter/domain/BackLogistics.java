package com.kariqu.tradecenter.domain;

/**
 * 返回给第三方物流查询平台的接收成功的报文.
 *
 * create by Athens(刘杰) on 2012-11-09
 */
public class BackLogistics {

    /**
     * true 表示回调成功, false 表示失败
     */
    private boolean result = false;

    /**
     * 成功则使用 200
     */
    private int returnCode = 0;

    /**
     * "成功" 或 "失败"
     */
    private String message = "失败";

    public BackLogistics() {}
    public BackLogistics(boolean success) {
        if (success) {
            result = true;
            returnCode = 200;
            message = "成功";
        }
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
