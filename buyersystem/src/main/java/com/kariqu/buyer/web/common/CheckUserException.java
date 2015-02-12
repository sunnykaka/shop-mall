package com.kariqu.buyer.web.common;

/**
 * 检查用户
 *
 * @author Athens(刘杰)
 * @Time 13-6-7 上午11:39
 */
public class CheckUserException extends Exception {

    public CheckUserException() {
    }

    public CheckUserException(String message) {
        super(message);
    }

    public CheckUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckUserException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
