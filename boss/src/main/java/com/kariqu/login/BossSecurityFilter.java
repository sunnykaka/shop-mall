package com.kariqu.login;

import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.common.uri.UrlHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 安全过滤器，检查登陆
 * User: Asion
 * Date: 11-11-16
 * Time: 下午5:11
 */
public class BossSecurityFilter implements Filter {

    /**
     * 在spring上下文中urlBroker的key
     */
    private String urlBroker = "urlBrokerFactory";

    /**
     * 放过url的管理bean
     */
    private String passResourceBean = "passResourceBean";

    private ServletContext servletContext;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        String configUrlBeanKey = filterConfig.getInitParameter("UrlBroker_Bean_Key");
        if (StringUtils.isNotBlank(configUrlBeanKey)) {
            this.urlBroker = configUrlBeanKey;
        }

        String configPassResourceBeanKey = filterConfig.getInitParameter("PassResource_Bean_Key");
        if (StringUtils.isNotBlank(configPassResourceBeanKey)) {
            this.passResourceBean = configPassResourceBeanKey;
        }

        servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURL = request.getRequestURI();
        requestURL = UrlHelper.normalizedUrl(requestURL.replaceFirst(request.getContextPath(), ""));
        WebApplicationContext webApplicationContext = RequestContextUtils.getWebApplicationContext(request, servletContext);

        PassResourceBean passResourceBean = (PassResourceBean) webApplicationContext.getBean(this.passResourceBean);
        if (passResourceBean.letItGo(requestURL)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (!SessionUtils.isLogin(request.getSession())) {
            URLBrokerFactory urlBrokerFactory = (URLBrokerFactory) webApplicationContext.getBean(urlBroker);
            String loginUrl = urlBrokerFactory.getUrl("ToLogin").toString();
            response.sendRedirect(loginUrl);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


    @Override
    public void destroy() {

    }
}
