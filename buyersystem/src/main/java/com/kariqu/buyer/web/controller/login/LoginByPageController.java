package com.kariqu.buyer.web.controller.login;

import com.kariqu.buyer.web.common.CheckUserException;
import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.common.WebSystemUtil;
import com.kariqu.buyer.web.helper.BuySessionLogin;
import com.kariqu.buyer.web.helper.CheckUser;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserData;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * 页面登陆
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-3
 *        Time: 下午1:24
 */
@Controller
public class LoginByPageController {

    private Log logger = LogFactory.getLog(LoginByPageController.class);
    
    @Autowired
    private BuySessionLogin buySessionLogin;

    @Autowired
    private CheckUser checkUser;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/toErrorPage")
    @RenderHeaderFooter
    public String toErrorPage(Model model, String error) throws IOException {
        if (StringUtils.isNotEmpty(error) && error.equals("sql")) {
            error = "提交的数据格式有误，请认真检查！";
        }
        model.addAttribute("site_title", "操作有误！");
        model.addAttribute("msg", error);
        return "error";
    }

    /**
     * 导航到登陆页面
     *
     * @return
     */
    @RequestMapping(value = "/user/toPageLogin")
    @RenderHeaderFooter
    public String toLogin(Model model, String backUrl, HttpServletRequest request) throws UnsupportedEncodingException {
        backUrl = checkUser.checkBackUrl(backUrl);
        model.addAttribute("backUrl", backUrl);
        model.addAttribute("OauthBackUrl", URLEncoder.encode(backUrl, "UTF-8"));
        model.addAttribute("userName", request.getSession().getAttribute(LoginInfo.AUTO_LOGIN_NAME_KEY));
        return "user/loginPage";
    }


    /**
     * 登陆页面登陆
     *
     * @param userName
     * @param password
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/user/pageLogin")
    public void pageLogin(String userName, String password, String backUrl, String autologin,
                          String rememberName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = null;
            if (WebSystemUtil.checkEmail(userName)) {
                user = userService.getUserByEmail(userName.trim());
            }
            if (user == null && WebSystemUtil.checkMobile(userName)) {
                user = userService.getUserByPhone(userName.trim());
            }
            if (user == null) {
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

            buySessionLogin.doLogin(request,user, StringUtils.isNotBlank(rememberName) && "on".equals(rememberName));

            new JsonResult(true).addData("backUrl", checkUser.checkBackUrl(backUrl)).toJson(response);
        } catch (CheckUserException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        } catch (Exception e) {
            logger.error(e);
            new JsonResult(false, "登陆发生错误").toJson(response);
        }
    }

}
