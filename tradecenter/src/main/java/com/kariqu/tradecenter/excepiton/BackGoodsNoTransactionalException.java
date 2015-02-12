package com.kariqu.tradecenter.excepiton;

/**
 * 退货单异常 >> 不影响到事务
 */
public class BackGoodsNoTransactionalException extends BackGoodsBaseException {

    public BackGoodsNoTransactionalException() {
    }

    public BackGoodsNoTransactionalException(String message) {
        super(message);
    }

    public BackGoodsNoTransactionalException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackGoodsNoTransactionalException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
