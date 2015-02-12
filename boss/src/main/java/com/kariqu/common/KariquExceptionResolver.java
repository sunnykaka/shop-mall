package com.kariqu.common;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常解析器
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-11
 *        Time: 下午5:20
 */
public class KariquExceptionResolver implements HandlerExceptionResolver {

    private final Log logger = LogFactory.getLog(KariquExceptionResolver.class);

    private String pagePath = "error/default";

    private boolean online = true;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.error("发生一个系统异常", ex);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("msg", ExceptionUtils.getFullStackTrace(ex));
        modelAndView.addObject("online", online);
        modelAndView.setViewName(pagePath);
        return modelAndView;
    }

    public String getPagePath() {
        return pagePath;
    }

    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}
