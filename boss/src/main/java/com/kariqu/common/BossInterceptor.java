package com.kariqu.common;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.login.SessionUtils;
import com.kariqu.om.domain.SystemLog;
import com.kariqu.om.service.OMService;
import com.kariqu.securitysystem.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志拦截器
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-27
 *        Time: 下午4:56
 */
public class BossInterceptor implements HandlerInterceptor {

    @Autowired
    private OMService omService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Permission permission = handlerMethod.getMethodAnnotation(Permission.class);
            if (permission != null) {
                Account currentAccount = SessionUtils.getLoginAccount(request.getSession());
                Role loginRole = SessionUtils.getLoginRole(request.getSession());
                SystemLog systemLog = new SystemLog();
                systemLog.setIp(request.getRemoteAddr());
                systemLog.setTitle(permission.value());
                systemLog.setOperator(currentAccount != null ? currentAccount.getUserName() : "未登录");
                systemLog.setContent(RequestUtil.getRequestParams(request).toString());
                systemLog.setRoleName(loginRole == null ? "无角色" : loginRole.getRoleName());
                omService.createSystemLog(systemLog);
            }
        }
    }
}
