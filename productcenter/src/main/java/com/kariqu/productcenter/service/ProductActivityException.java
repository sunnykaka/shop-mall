package com.kariqu.productcenter.service;

/**
 * 商品活动异常，用来表达商品活动中需要受检查的一些业务行为
 * User: Asion
 * Date: 13-4-1
 * Time: 下午4:05
 */
public class ProductActivityException extends Exception {


    public ProductActivityException() {
        super();
    }

    public ProductActivityException(String message) {
        super(message);
    }

    public ProductActivityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductActivityException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
