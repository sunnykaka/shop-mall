package com.kariqu.tradecenter.excepiton;

/**
 * 退货单异常 >> 影响到事务
 */
public class BackGoodsTransactionalException extends BackGoodsBaseException {

    public BackGoodsTransactionalException() {
    }

    public BackGoodsTransactionalException(String message) {
        super(message);
    }

    public BackGoodsTransactionalException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackGoodsTransactionalException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
