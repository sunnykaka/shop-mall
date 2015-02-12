package com.kariqu.common.lib;

/**
 * 转换异常
 * Created by Canal.wen on 2014/6/27 14:25.
 */
public class ConversionException extends RuntimeException {
    public ConversionException() {
        super();
    }

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
