package com.kariqu.buyer.web.controller.login;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.session.KariquSession;
import com.kariqu.session.util.SessionUtils;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登陆状态
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-3
 *        Time: 下午12:56
 */
@Controller
public class LoginStatusController  {

    private final Log logger = LogFactory.getLog(LoginStatusController.class);
    @Autowired
    private UserService userService;

    @Autowired
     protected URLBrokerFactory urlBrokerFactory;


    /**
     * 退出
     *
     * @param request
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/user/loginOut")
    public void loginOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        if (null != sessionUserInfo) {
            if (logger.isDebugEnabled()) {
                logger.debug("退出的用户为：" + sessionUserInfo.getUserName());
            }
            HttpSession session = request.getSession();

            if (null != session.getAttribute(LoginInfo.AUTO_LOGIN_CODE_KEY)) {
                session.removeAttribute(LoginInfo.AUTO_LOGIN_CODE_KEY);
            }
            if (null != session.getAttribute(LoginInfo.AUTO_LOGIN_NAME_KEY)) {
                session.removeAttribute(LoginInfo.AUTO_LOGIN_NAME_KEY);
            }

            userService.deleteUserSession(String.valueOf(sessionUserInfo.getId()));
        }
        request.getSession().invalidate();
        response.sendRedirect(urlBrokerFactory.getUrl("BuyHome").toString());
    }


    /**
     * 检查用户是否登录，同时发送sessionId给客户端，可做跨域名共享session
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/user/checkLogin")
    public void checkLoginStatus(HttpServletRequest request, HttpServletResponse response, String callback) throws IOException {
        JsonResult jsonResult = new JsonResult(SessionUtils.isLogin(request.getSession()));
        String sessionId = ((KariquSession) request.getSession()).getEncryptId();
        jsonResult.addData("sessionId", sessionId);
        String json = jsonResult.toJson();

        StringBuffer result = new StringBuffer();
        result.append(callback)
                .append("([")
                .append(json)
                .append("])");
        response.getWriter().write(result.toString());
    }


    /**
     * 获取登陆用户名
     *
     * @param response
     * @param request
     * @param callback
     * @throws IOException
     */
    @RequestMapping(value = "/user/acquireLoginUserName")
    public void acquireLoginUser(HttpServletResponse response, HttpServletRequest request, String callback) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        final String loginUserName = (null == sessionUserInfo) ? "" : sessionUserInfo.getUserName();
        String json = "";
        if ("".equals(loginUserName)) {
            json = new JsonResult(false, "") {{
                this.addData("loginUserName", "");
            }}.toJson();
        } else {
            json = new JsonResult(true) {{
                this.addData("loginUserName", loginUserName);
            }}.toJson();
        }
        StringBuffer result = new StringBuffer();
        result.append(callback)
                .append("([")
                .append(json)
                .append("])");
        response.getWriter().write(result.toString());
    }

}
