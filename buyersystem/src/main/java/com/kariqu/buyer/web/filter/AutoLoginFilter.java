package com.kariqu.buyer.web.filter;

import com.kariqu.buyer.web.common.CartTrackUtil;
import com.kariqu.buyer.web.common.TrimParameterWrapper;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.BuySessionLogin;
import com.kariqu.session.util.SessionUtils;
import com.kariqu.usercenter.domain.AccountType;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 自动登陆filter
 * User: Asion
 * Date: 11-12-8
 * Time: 下午9:54
 */
public class AutoLoginFilter implements Filter {

    private String ignoreUrl = "^(.+[.])(ico|png|gif|jpg|jpeg|js|css)$";

    private ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String ignoreUrl = filterConfig.getInitParameter("ignoreUrl");
        if (StringUtils.isNotBlank(ignoreUrl)) {
            this.ignoreUrl = ignoreUrl;
        }
        servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        // 写 trackId (如果没有)
        CartTrackUtil.readOrWriteTrackId(httpServletRequest);

        // 所有非 图片/js/css 的请求都验证自动登录
        if (!httpServletRequest.getRequestURI().matches(ignoreUrl)) {
            HttpSession session = httpServletRequest.getSession();
            if (!SessionUtils.isLogin(session)) {
                String autoLoginCode = (String) session.getAttribute(LoginInfo.AUTO_LOGIN_CODE_KEY);
                if (StringUtils.isNotEmpty(autoLoginCode)) {
                    WebApplicationContext webApplicationContext = RequestContextUtils.getWebApplicationContext(httpServletRequest, servletContext);
                    UserService userService = webApplicationContext.getBean(UserService.class);
                    User user = userService.getUserSession(autoLoginCode.trim());
                    if (user != null) {
                        BuySessionLogin buySessionLogin = webApplicationContext.getBean(BuySessionLogin.class);
                        buySessionLogin.doLogin(httpServletRequest, user, AccountType.KRQ, BuySessionLogin.LoginType.AutoLogin);
                    }
                }
            }
        }

        // 去参数的左右空格, 且防止 xss 攻击
        chain.doFilter(new TrimParameterWrapper(httpServletRequest), response);
    }

    @Override
    public void destroy() {}

}
