package com.kariqu.designcenter.service;

/**
 * 客户端容器没有找模块的异常
 * User: Asion
 * Date: 12-7-25
 * Time: 下午6:14
 */
public class ModuleNotFoundException extends RuntimeException {

    public ModuleNotFoundException() {
        super();
    }

    public ModuleNotFoundException(String message) {
        super(message);
    }

    public ModuleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleNotFoundException(Throwable cause) {
        super(cause);
    }
}
