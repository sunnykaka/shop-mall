package com.kariqu.tradecenter.excepiton;

/**
 * 订单异常基类. 客户端只需要 catch 这个 check 异常就可以, 具体是否需要回滚事务由其内部决定
 */
public abstract class OrderBaseException extends Exception {

    public OrderBaseException() {}

    public OrderBaseException(String message) {
        super(message);
    }

    public OrderBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderBaseException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
