package com.kariqu.buyer.web.common;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private String pagePath = "error";

    private boolean online = true;

    @Autowired
    private HeadAndFoot headAndFoot;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.error("发生一个系统异常: ", ex);
        ModelAndView modelAndView = new ModelAndView();
        if (ex instanceof DuplicateSubmitFormException) {
            modelAndView.setViewName("order/orderFail");
        } else {
            headAndFoot.renderHeadFoot(request);
            modelAndView.addObject("site_title", "发生系统错误！");
            modelAndView.addObject("msg", "服务器出错，请稍后再试");
            modelAndView.addObject("ex", ExceptionUtils.getFullStackTrace(ex));
            modelAndView.addObject("online", online);
            modelAndView.setViewName(pagePath);
        }
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
