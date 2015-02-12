package com.kariqu.designcenter.domain.exception;

/**
 * 运行时的严重异常，会直接抛向容器，程序可以配置错误映射页面
 * 这类异常容器会向浏览器发送错误代码，所以Ext类的富客户端会得到错误代码然后调用失败函数。
 * @author Asion
 * @since 2011-5-5 下午03:39:03
 * @version 1.0.0
 */
public class DesignCenterRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -3250161006387814367L;
    
    public DesignCenterRuntimeException(Throwable e) {
        super(e);
    }

    public DesignCenterRuntimeException(String msg) {
        super(msg);
    }

    public DesignCenterRuntimeException(String msg, Throwable e) {
        super(msg, e);
    }

}
