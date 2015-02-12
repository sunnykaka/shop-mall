package com.kariqu.buyer.web.controller.login;

import com.kariqu.buyer.web.common.CheckUserException;
import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.WebSystemUtil;
import com.kariqu.buyer.web.helper.BuySessionLogin;
import com.kariqu.buyer.web.helper.CheckUser;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserData;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 弹出框登陆
 * User: Asion
 * Date: 11-11-23
 * Time: 上午10:14
 */
@Controller
public class LoginByWindowController {

    @Autowired
    private BuySessionLogin buySessionLogin;

    @Autowired
    private UserService userService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private CheckUser checkUser;

    /**
     * 导航到登陆Frame
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/user/toWindowLogin")
    public String forwardUserLogin(String actionType, String backUrl, Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        model.addAttribute("actionType", getActionType(actionType));
        model.addAttribute("backUrl", checkUser.checkBackUrl(backUrl));
        model.addAttribute("jumpBackUrl", URLEncoder.encode(backUrl, "UTF-8"));
        model.addAttribute("userName", request.getSession().getAttribute(LoginInfo.AUTO_LOGIN_NAME_KEY));
        return "user/loginFrame";
    }

    /**
     * 弹出框登陆
     *
     * @param userName
     * @param password
     * @throws IOException
     */
    @RequestMapping(value = "/user/window/login")
    public void popupLogin(String actionType, String userName, String password, String backUrl, /*String autologin,*/
                             /*Model model,*/ String rememberName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        actionType = getActionType(actionType);
        /*model.addAttribute("jumpBackUrl", URLEncoder.encode(backUrl, "UTF-8"));
        model.addAttribute("backUrl", backUrl);
        model.addAttribute("actionType", actionType);
        model.addAttribute("userName", userName);*/

        try {
            User user;
            if (WebSystemUtil.checkEmail(userName)) {
                user = userService.getUserByEmail(userName.trim());
            } else if (WebSystemUtil.checkMobile(userName)) {
                user = userService.getUserByPhone(userName.trim());
            } else {
                user = userService.getUserByUserName(userName.trim());
            }

            checkUser.checkUserLogin(user, password);
            //以前未激活的用户登录后激活
            if (!user.isActive()) {
                user.setActive(true);
                userService.updateUser(user);
            }

            //ToDo 兼容以前注册的用户
            UserData userData = userService.queryUserDataByUserId(user.getId());
            if (null == userData) {
                userService.createUserData(new UserData(user.getId()));
            }

            buySessionLogin.doLogin(request, user, StringUtils.isNotEmpty(rememberName) && "on".equals(rememberName));

            /*response.sendRedirect(urlBrokerFactory.getUrl("JumpPage").addQueryData("actionType", actionType)
                    .addQueryData("backUrl", URLEncoder.encode(backUrl, "UTF-8")).toString());*/
            new JsonResult(true).addData("jumpUrl", urlBrokerFactory.getUrl("JumpPage").addQueryData("actionType", actionType)
                    .addQueryData("backUrl", URLEncoder.encode(backUrl, "UTF-8")).toString()).toJson(response);
        } catch (CheckUserException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    private String getActionType(String actionType) {
        if (StringUtils.isBlank(actionType)) {
            actionType = "redirectUrl";
        }
        return actionType;
    }
}
