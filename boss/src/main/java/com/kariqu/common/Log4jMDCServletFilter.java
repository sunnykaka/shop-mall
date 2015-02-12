package com.kariqu.common;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.common.log4j.Log4jMDC;
import com.kariqu.login.SessionUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * log4j MDC Filter
 *
 * @author Tiger
 * @version 1.0
 * @since 12-4-27 下午4:56
 */
public class Log4jMDCServletFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        Log4jMDC.put(Log4jMDC.LOG4J_MDC_REQUEST_URI, httpServletRequest.getRequestURI());

        Account currentAccount = SessionUtils.getLoginAccount(httpServletRequest.getSession());
        Log4jMDC.put(Log4jMDC.USER_INFO, (currentAccount == null ? "未登陆用户" : currentAccount.toString()) + ", ip:" + IpTools.getIpAddress(httpServletRequest));
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Log4jMDC.remove(Log4jMDC.LOG4J_MDC_REQUEST_URI);
        Log4jMDC.remove(Log4jMDC.USER_INFO);
    }
}
