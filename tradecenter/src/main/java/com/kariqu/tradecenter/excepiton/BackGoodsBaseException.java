package com.kariqu.tradecenter.excepiton;

/**
 * 退货单异常
 *
 */
public abstract class BackGoodsBaseException extends Exception {

    public BackGoodsBaseException() {
    }

    public BackGoodsBaseException(String message) {
        super(message);
    }

    public BackGoodsBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackGoodsBaseException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
