package com.kariqu.designcenter.domain.exception;

/**
 * 模板渲染异常，这个异常致命
 *
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-15 下午09:22:02
 */
public class RenderException extends DesignCenterRuntimeException {

    private static final long serialVersionUID = 3533090940265107161L;

    /**
     * @param msg
     */
    public RenderException(String msg) {
        super(msg);
    }

    public RenderException(String msg, Throwable e) {
        super(msg, e);
    }

}
