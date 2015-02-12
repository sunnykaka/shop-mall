package com.kariqu.login;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.securitysystem.domain.Role;

import javax.servlet.http.HttpSession;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-10-8
 *        Time: 下午4:23
 */
public class SessionUtils {

    public static final String LOGIN_ACCOUNT = "loginAccount";

    public static final String LOGIN_ROLE = "loginRole";

    public static void doLogin(HttpSession session, Account account) {
        session.setAttribute(LOGIN_ACCOUNT, account);
    }

    public static void withRole(HttpSession session, Role role) {
        session.setAttribute(LOGIN_ROLE, role);
    }


    public static Account getLoginAccount(HttpSession session) {
        Object object = session.getAttribute(LOGIN_ACCOUNT);
        if (object != null) {
            return (Account) object;
        } else {
            return null;
        }
    }

    public static Role getLoginRole(HttpSession session) {
        Object object = session.getAttribute(LOGIN_ROLE);
        if (object != null) {
            return (Role) object;
        } else {
            return null;
        }
    }

    public static boolean isLogin(HttpSession session) {
        return getLoginAccount(session) != null;
    }
}
