package com.kariqu.tradecenter.excepiton;

/**
 * 交易致命异常，直接失败
 * User: Asion
 * Date: 13-5-31
 * Time: 上午10:18
 */
public class TradeFailException extends RuntimeException {

    public TradeFailException() {
        super();
    }

    public TradeFailException(String message) {
        super(message);
    }

    public TradeFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public TradeFailException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
