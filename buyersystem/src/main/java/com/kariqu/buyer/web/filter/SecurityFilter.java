package com.kariqu.buyer.web.filter;

import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.session.util.SessionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 安全过滤器，对于购物系统，在前端的权限非常简单，基本上就是订单管理需要检查
 * 也不会涉及到角色，组等概念。
 * <p/>
 * 检查需要用户登录才可以进行操作的地址，如果未登录，则跳转到登录页面，成功之后再跳转到之前页面
 * 如果是POST提交的，则跳转到表单填写的页面，数据会丢失。
 * <p/>
 * 要保护的路径通过在filter中进行配置，用逗号隔开
 * <p/>
 * POST最好采取当前弹层配合ajax来实现
 * User: Asion
 * Date: 11-10-31
 * Time: 下午4:37
 */
public class SecurityFilter implements Filter {

    private static final Log logger = LogFactory.getLog(SecurityFilter.class);

    /**
     * 在spring上下文中urlBroker的key
     */
    private String urlBroker = "urlBrokerFactory";

    private ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configBeanKey = filterConfig.getInitParameter("UrlBroker_Bean_Key");
        if (StringUtils.isNotBlank(configBeanKey)) {
            this.urlBroker = configBeanKey;
        }

        servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        WebApplicationContext webApplicationContext = RequestContextUtils.getWebApplicationContext(httpServletRequest, servletContext);
        URLBrokerFactory urlBrokerFactory = (URLBrokerFactory) webApplicationContext.getBean(urlBroker);
        // 主站域名 www.yijushang.com
        String buyHomeUrl = urlBrokerFactory.getUrl("BuyHome").toString();
        // yijushang.com
        String mainUrl = urlBrokerFactory.getUrl("BuyDomain").toString();

        // 验证是否有登陆
        if (!SessionUtils.isLogin(httpServletRequest.getSession())) {
            String loginUrl = urlBrokerFactory.getUrl("ToPageLogin").addQueryData("backUrl",
                    URLEncoder.encode(getCurrentUrl(httpServletRequest, buyHomeUrl, mainUrl), "UTF-8")).toString();
            httpServletResponse.sendRedirect(loginUrl);
            return;
        } else if (!checkSiteUrl(httpServletRequest, mainUrl)) {
            // 有登陆也需要验证请求地址是否来自本站, 若不是主站则跳到首页
            if (logger.isWarnEnabled())
                logger.warn("已登陆的 ip[" + IpTools.getIpAddress(httpServletRequest) + "], 用户("
                    + LoginInfo.getLoginUser(httpServletRequest) + ")通过地址("
                    + getRequestPath(httpServletRequest) + ")请求本站(" + httpServletRequest.getRequestURL() + ")!");
            httpServletResponse.sendRedirect(buyHomeUrl);
            return;
        }
        chain.doFilter(request, response);
    }

    /**
     * 用户未登陆时访问某个地址时, 先跳到登陆页, 然后跳回至之前页.
     *
     * @param request request
     * @param buyHomeUrl 主站地址, 包含 www
     * @param mainUrl 主站地址, 不包含 www
     * @return 如果链接中包含有主站则返回当前正在返回的链接地址, 否则返回主站地址
     */
    private String getCurrentUrl(HttpServletRequest request, String buyHomeUrl, String mainUrl) {
        StringBuilder sbd = new StringBuilder();
        // 获取头
        String path = getRequestPath(request);
        if (StringUtils.isNotBlank(path) && path.substring(path.indexOf(".") + 1).contains(mainUrl)) {
            sbd.append(path);
            String queryString = request.getQueryString();
            if (queryString != null) {
                sbd.append("?");
                sbd.append(queryString);
            }
            return sbd.toString();
        }

        if (StringUtils.isNotBlank(path)) {
            if (logger.isWarnEnabled())
                logger.warn("未登陆的 ip[" + IpTools.getIpAddress(request) + "]通过地址("
                    + path + ")请求(" + request.getRequestURL() + ")!");
        }

        return buyHomeUrl;
    }

    /**
     * 检查请求地址是否是本站
     *
     * @param request request
     * @param home 主站 yijushang.com
     * @return 若包含本站则返回 true
     */
    private boolean checkSiteUrl(HttpServletRequest request, String home) {
        String requestURI = getRequestPath(request);
        return StringUtils.isNotBlank(requestURI) && requestURI.substring(requestURI.indexOf(".") + 1).contains(home);
    }

    private String getRequestPath(HttpServletRequest request) {
        String path = request.getHeader("referer");
        if (StringUtils.isBlank(path)) path = request.getRequestURL().toString();

        return path;
    }

    @Override
    public void destroy() {
    }
}
