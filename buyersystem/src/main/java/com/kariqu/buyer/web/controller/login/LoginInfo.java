package com.kariqu.buyer.web.controller.login;

import com.kariqu.buyer.web.helper.SessionUserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * User: Asion
 * Date: 11-12-6
 * Time: 下午1:08
 */
public class LoginInfo {

    public static final String USER_SESSION_KEY = "loginUser";

    public static final String BACK_URL = "sessionBackUrl";

    public static final String AUTO_LOGIN_NAME_KEY = "autologinName";

    public static final String AUTO_LOGIN_CODE_KEY = "autologinCode";

    /**
     * 得到登陆用户
     *
     * @param request
     * @return
     */
    public static SessionUserInfo getLoginUser(HttpServletRequest request) {
        return (SessionUserInfo) request.getSession().getAttribute(LoginInfo.USER_SESSION_KEY);
    }

}
