package com.kariqu.usercenter.domain;

/**
 * User: kyle
 * Date: 13-1-6
 * Time: 下午3:29
 */
public class SmsSenderException extends RuntimeException {

    public SmsSenderException(String msg) {
        super(msg);
    }

    public SmsSenderException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
