package com.kariqu.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 重新实现HttpServletRequest
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-17 下午6:45
 */
public class KariquSessionServletRequest extends HttpServletRequestWrapper {

    private KariquSession kariquSession;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public KariquSessionServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void setSession(KariquSession kariquSession) {
        this.kariquSession = kariquSession;
    }

    @Override
    public KariquSession getSession() {
        return kariquSession;
    }

    @Override
    public KariquSession getSession(boolean create) {
        return getSession();
    }
}
