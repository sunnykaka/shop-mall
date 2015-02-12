package com.kariqu.designcenter.domain.exception;

/**
 * DomID 重复异常，致命异常
 * @author Asion
 * @since 2011-5-5 下午03:54:00
 * @version 1.0.0
 */
public class DomIdRepeatException extends RenderException {
    
    private static final long serialVersionUID = -6546542545687483554L;

    public DomIdRepeatException(String message) {
        super(message);
    }

}
