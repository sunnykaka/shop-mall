package com.kariqu.suppliersystem.supplierManager.vo;

import com.kariqu.securitysystem.domain.Role;
import com.kariqu.suppliercenter.domain.SupplierAccount;

import javax.servlet.http.HttpSession;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-10-8
 *        Time: 下午4:23
 */
public class SessionUtils {

    public static final String LOGIN_SupplierAccount = "supplierAccount";

    public static final String LOGIN_ROLE = "loginRole";

    public static void doLogin(HttpSession session, SupplierAccount supplierAccount) {
        session.setAttribute(LOGIN_SupplierAccount, supplierAccount);
    }

    public static void withRole(HttpSession session, Role role) {
        session.setAttribute(LOGIN_ROLE, role);
    }


    public static SupplierAccount getLoginAccount(HttpSession session) {
        Object object = session.getAttribute(LOGIN_SupplierAccount);
        if (object != null) {
            return (SupplierAccount) object;
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
