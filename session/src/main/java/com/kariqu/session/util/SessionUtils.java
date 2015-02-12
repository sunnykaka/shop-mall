package com.kariqu.session.util;

import com.kariqu.session.KariquSessionLoginCallback;

import javax.servlet.http.HttpSession;

/**
 * @Author: Tiger
 * @Since: 11-12-8 下午9:01
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class SessionUtils {

    public static boolean isLogin(HttpSession kariquSession) {
        return null != kariquSession && "true".equals(kariquSession.getAttribute(SessionConstants.LOGIN));
    }

    public static void doLogin(HttpSession kariquSession, Object userId) {
        kariquSession.setAttribute(SessionConstants.LOGIN, "true");
        kariquSession.setAttribute(SessionConstants.USERID, userId);
    }

    public static void doLogin(KariquSessionLoginCallback callback) {
        callback.beforeLogin();
        callback.getHttpSession().setAttribute(SessionConstants.LOGIN, "true");
        callback.getHttpSession().setAttribute(SessionConstants.USERID, callback.getUserId());
        callback.afterLogin();
    }

    public static Object getUserId(HttpSession kariquSession) {
        return kariquSession.getAttribute(SessionConstants.USERID);
    }

}
