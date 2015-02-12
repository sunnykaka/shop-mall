package com.kariqu.buyer.web.filter;

import com.kariqu.common.uri.URLBrokerFactory;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

/**
 * 防sql注入 过滤器
 * User: Alec
 * Date: 13-1-10
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */

public class SQLFilter implements Filter {

    private String inj_str = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|; |or|-|+|,";

    private String[] inj_stra = null;

    /**
     * 在spring上下文中urlBroker的key
     */
    private String urlBroker = "urlBrokerFactory";

    private ServletContext servletContext;

    public void init(FilterConfig config) throws ServletException {
        servletContext = config.getServletContext();

        String configBeanKey = config.getInitParameter("UrlBroker_Bean_Key");
        if (StringUtils.isNotBlank(configBeanKey)) {
            this.urlBroker = configBeanKey;
        }

        String keywords = config.getInitParameter("keywords");
        if (StringUtils.isNotEmpty(keywords)) {
            this.inj_str = keywords;
        }

        inj_stra = inj_str.split("\\|");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        Iterator values = req.getParameterMap().values().iterator();//获取所有的表单参数
        if (checkHadSqlWords(values)) {
            WebApplicationContext webApplicationContext = RequestContextUtils.getWebApplicationContext(req, servletContext);
            URLBrokerFactory urlBrokerFactory = (URLBrokerFactory) webApplicationContext.getBean(urlBroker);
            String toErrorPage = urlBrokerFactory.getUrl("ToErrorPage").addQueryData("error", "sql").toString();
            res.sendRedirect(toErrorPage);
            return;
        }
        chain.doFilter(request, response);
    }

    public boolean sql_inj(String str) {
        for (String s : inj_stra) {
            if (str.contains(" " + s + " ") || str.contains(" " + s.toUpperCase() + " ")) {
                return true;
            }
        }
        return false;
    }

    public boolean checkHadSqlWords(Iterator values) {
        while (values.hasNext()) {
            String[] value = (String[]) values.next();
            for (String str : value) {
                if (sql_inj(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}


