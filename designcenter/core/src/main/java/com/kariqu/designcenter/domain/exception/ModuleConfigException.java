package com.kariqu.designcenter.domain.exception;


/**
 * 模块配置处理异常
 * @author Asion
 * @since 2011-5-2 下午03:57:14
 * @version 1.0.0
 */
public class ModuleConfigException extends DesignCenterCheckedException {

    private static final long serialVersionUID = 8141306373990850262L;

    /**
     * @param message
     * @param e
     */
    public ModuleConfigException(String message, Throwable e) {
        super(message,e);
    }


}
