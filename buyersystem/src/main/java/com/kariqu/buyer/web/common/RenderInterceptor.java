package com.kariqu.buyer.web.common;

import com.kariqu.designcenter.client.service.ProdRenderPageService;
import com.kariqu.securitysystem.util.MD5;
import com.kariqu.session.util.SessionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * User: kyle
 * Date: 13-2-20
 * Time: 上午11:37
 */
public class RenderInterceptor implements HandlerInterceptor {

    @Autowired
    private HeadAndFoot headAndFoot;

    @Autowired
    private ProdRenderPageService prodRenderPageService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            Token token = handlerMethod.getMethodAnnotation(Token.class);
            if (token != null) {
                this.generateFormToken(request);
            }

            RenderHeaderFooter renderHeaderFooter = handlerMethod.getMethodAnnotation(RenderHeaderFooter.class);
            if (renderHeaderFooter != null) {
                headAndFoot.renderHeadFoot(request);
                request.setAttribute("categoryInfo", prodRenderPageService.prodRenderGlobalCommonModule("category",
                        new HashMap<String, Object>()).getPageContent());
            }

            CheckFormToken checkFormToken = handlerMethod.getMethodAnnotation(CheckFormToken.class);
            if (checkFormToken != null) {
                if (!checkFormToken(request)) {
                    request.setAttribute("site_title", "请求错误");
                    request.setAttribute("msg", "请不要重复提交");
                    throw new DuplicateSubmitFormException("表单重复提交!");
                } else {
                    generateFormToken(request);
                }
            }
        }
        return true;
    }

    private boolean checkFormToken(HttpServletRequest request) {
        String token = request.getParameter(SessionConstants.TOKEN);
        HttpSession session = request.getSession();
        String oldToken = (String) session.getAttribute(SessionConstants.TOKEN);
        return token.equals(oldToken);
    }

    private void generateFormToken(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        String hashString = MD5.getHashString(System.currentTimeMillis() + sessionId);
        request.getSession().setAttribute(SessionConstants.TOKEN, hashString);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Object target = handlerMethod.getBean();
            if (modelAndView != null && !modelAndView.getModelMap().containsKey("site_title")) {
                PageTitle pageTitle = handlerMethod.getMethodAnnotation(PageTitle.class);
                if (pageTitle != null) {
                    modelAndView.getModel().put("site_title", pageTitle.value());
                } else {
                    pageTitle = target.getClass().getAnnotation(PageTitle.class);
                    if (pageTitle != null) {
                        modelAndView.getModel().put("site_title", pageTitle.value());
                    }
                }
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
