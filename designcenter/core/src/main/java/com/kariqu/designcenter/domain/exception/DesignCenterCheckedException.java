package com.kariqu.designcenter.domain.exception;

/**
 * 检查异常，如果出现这样的异常程序必须保证后果
 * DesignCenter业务上的可恢复异常的父类
 * @author Asion
 * @since 2011-5-5 下午03:40:31
 * @version 1.0.0
 */
public class DesignCenterCheckedException extends Exception {
    
    private static final long serialVersionUID = -774055845995191714L;
    
    public DesignCenterCheckedException(Throwable e) {
        super(e);
    }

    public DesignCenterCheckedException(String msg) {
        super(msg);
    }

    public DesignCenterCheckedException(String msg, Throwable e) {
        super(msg, e);
    }

}
