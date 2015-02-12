package com.kariqu.buyer.web.filter;

import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.common.log4j.Log4jMDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class Log4jMDCServletFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Log4jMDC.put(Log4jMDC.LOG4J_MDC_REQUEST_URI, httpServletRequest.getRequestURI());

        SessionUserInfo userInfo = LoginInfo.getLoginUser(httpServletRequest);
        Log4jMDC.put(Log4jMDC.USER_INFO, (userInfo == null ? "未登陆用户" : userInfo.toString()) + ", ip:" + IpTools.getIpAddress(httpServletRequest));
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Log4jMDC.remove(Log4jMDC.LOG4J_MDC_REQUEST_URI);
        Log4jMDC.remove(Log4jMDC.USER_INFO);
    }

}
