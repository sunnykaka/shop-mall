package com.kariqu.suppliersystem.supplierManager.vo;

import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.common.uri.UrlHelper;
import com.kariqu.securitysystem.domain.Role;
import com.kariqu.securitysystem.service.SecurityService;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 角色检查过滤，这个过滤器位于全局安全过滤之后
 * 如果没有用户没有角色则报错，有一个角色放行，如果有多个角色要重定向到角色选择页面
 * User: Asion
 * Date: 11-12-5
 * Time: 下午4:49
 */
public class RoleCheckFilter implements Filter {

    private final Log logger = LogFactory.getLog(RoleCheckFilter.class);


    private ServletContext servletContext;

    /**
     * 在spring上下文中urlBroker的key
     */
    private String urlBroker = "urlBrokerFactory";

    /**
     * 安全服务在spring上下文中的key
     */
    private String securityService = "securityService";

    /**
     * 放过url的管理bean
     */
    private String passResourceBean = "passResourceBean";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        String configBeanKey = filterConfig.getInitParameter("UrlBroker_Bean_Key");
        if (StringUtils.isNotBlank(configBeanKey)) {
            this.urlBroker = configBeanKey;
        }

        String configSecurityBeanKey = filterConfig.getInitParameter("SecurityService_Bean_Key");
        if (StringUtils.isNotBlank(configSecurityBeanKey)) {
            this.securityService = configSecurityBeanKey;
        }

        String configPassResourceBeanKey = filterConfig.getInitParameter("PassResource_Bean_Key");
        if (StringUtils.isNotBlank(configPassResourceBeanKey)) {
            this.passResourceBean = configPassResourceBeanKey;
        }

        servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestURL = httpServletRequest.getRequestURI();
        requestURL = UrlHelper.normalizedUrl(requestURL.replaceFirst(httpServletRequest.getContextPath(), ""));
        WebApplicationContext webApplicationContext = RequestContextUtils.getWebApplicationContext(httpServletRequest, servletContext);

        PassResourceBean passResourceBean = (PassResourceBean) webApplicationContext.getBean(this.passResourceBean);
        SupplierAccount supplierAccount = SessionUtils.getLoginAccount(httpServletRequest.getSession());
        if (passResourceBean.letItGo(requestURL) || supplierAccount.isMainAccount()) {
            chain.doFilter(request, response);
            return;
        }
        Role currentRole = SessionUtils.getLoginRole(httpServletRequest.getSession());
        if (currentRole == null) {
            SecurityService securityService = (SecurityService) webApplicationContext.getBean(this.securityService);
            List<Integer> roleIdList = securityService.queryAccountRole(supplierAccount.getId());
            //如果没有角色直接出错
            if (roleIdList.size() == 0) {
                logger.error("没有读取到用户角色");
                URLBrokerFactory urlBrokerFactory = (URLBrokerFactory) webApplicationContext.getBean(urlBroker);
                String loginUrl = urlBrokerFactory.getUrl("Error").toString();
                httpServletResponse.sendRedirect(loginUrl);
                return;
            }
            currentRole = securityService.getRole(roleIdList.get(0));
            SessionUtils.withRole(httpServletRequest.getSession(), currentRole);
        }
        //角色验证成功之后根路径放过，根路径不受权限检查，它是展示系统的界面
        if (requestURL.endsWith("/")) {
            chain.doFilter(request, response);
            return;
        }
        if (currentRole.hasPermission(requestURL)) {
            chain.doFilter(request, response);
        } else {
            logger.error("这个账户没有权限，操作URL是:" + requestURL);
            URLBrokerFactory urlBrokerFactory = (URLBrokerFactory) webApplicationContext.getBean(urlBroker);
            String loginUrl = urlBrokerFactory.getUrl("Error").toString();
            httpServletResponse.sendRedirect(loginUrl);
        }
    }

    @Override
    public void destroy() {

    }
}
