package com.kariqu.tradecenter.domain;

/**
 * 响应的数据
 *
 * @author Athens(刘杰)
 * @Time 2012-09-25 10:22
 * @since 1.0.0
 */
public class ResponseMsg {
    private boolean result;
    private int returnCode;
    private String message;

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

    public String toString() {
        return "[result:" + result + ", returnCode:" + returnCode + ", message:" + message + "]";
    }

}