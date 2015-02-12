package com.kariqu.buyer.web.controller.login;

import com.kariqu.buyer.web.common.CheckUserException;
import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.helper.BuySessionLogin;
import com.kariqu.buyer.web.helper.CheckUser;
import com.kariqu.common.CheckUtils;
import com.kariqu.common.DateUtils;
import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.tradecenter.service.IntegralService;
import com.kariqu.usercenter.domain.AccountType;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.UserPointService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
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
import java.util.Date;

/**
 * 弹出框注册
 * User: Alec
 * Date: 12-11-15
 * Time: 下午1:40
 */
@Controller
public class RegisterByWindowController {

    private Log logger = LogFactory.getLog(RegisterByWindowController.class);

    @Autowired
    private BuySessionLogin buySessionLogin;

    @Autowired
    private UserService userService;

    @Autowired
    private CheckUser checkUser;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private UserPointService userPointService;

    /**
     * 导航到登陆Frame
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/user/toWindowRegister")
    public String forwardUserRegister(String actionType, String backUrl, Model model) throws UnsupportedEncodingException {
        model.addAttribute("actionType", getActionType(actionType));
        model.addAttribute("backUrl", checkUser.checkBackUrl(backUrl));
        model.addAttribute("imageVersion", RandomStringUtils.randomNumeric(8));
        return "user/registerFrame";
    }

    /**
     * 弹出框注册
     *
     * @param imageCode
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/user/window/register")
    public void popupRegister(String userName, String password, String email, String imageCode, String backUrl,
                              String actionType,/* Model model, */HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = new User();
        try {
            // 校验验证码
            checkUser.checkImageCode(request, imageCode);

            // 校验密码格式
            checkUser.checkPasswordByStyle(password);

            // 校验用户名
            checkUser.checkUserName(userName);

            if (CheckUtils.checkEmail(userName.trim())) {
                user.setEmail(userName.trim());
            } else if (CheckUtils.checkPhone(userName.trim())) {
                checkUser.checkPhone(userName.trim());
                user.setPhone(userName.trim());
            }
        } catch (CheckUserException e) {
           /* model.addAttribute("msg", e.getMessage());
            return "user/registerFrame";*/
            new JsonResult(false, e.getMessage()).toJson(response);
            return;
        }

        user.setUserName(userName.trim());
        user.setPassword(BCryptUtil.encryptPassword(password));
        user.setRegisterIP(IpTools.getIpAddress(request));
        user.setRegisterDate(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
        user.setActive(true);
        try {
            userService.createUser(user);
            userPointService.addUserCurrency(user.getId(), integralService.getRegisterCurrencyCount(), "注册得积分"); // 注册送的积分数量
            buySessionLogin.doLogin(request, user, AccountType.KRQ, BuySessionLogin.LoginType.Register);
            /*return "redirect:" + urlBrokerFactory.getUrl("JumpPage").addQueryData("actionType", actionType)
                    .addQueryData("backUrl", URLEncoder.encode(backUrl, "UTF-8")).toString();*/
            new JsonResult(true).addData("jumpUrl", urlBrokerFactory.getUrl("JumpPage").addQueryData("actionType", actionType)
                    .addQueryData("backUrl", URLEncoder.encode(backUrl, "UTF-8")).toString()).toJson(response);
        } catch (Exception e) {
            /*model.addAttribute("backUrl", checkUser.checkBackUrl(backUrl));
            model.addAttribute("actionType", actionType);
            model.addAttribute("msg", "很抱歉，注册失败！");*/
            new JsonResult(false, "很抱歉，注册失败！").addData("backUrl", checkUser.checkBackUrl(backUrl)).addData("actionType", actionType).toJson(response);
        }
        /*return "user/registerFrame";*/
    }

    private String getActionType(String actionType) {
        if (StringUtils.isBlank(actionType)) {
            actionType = "redirectUrl";
        }
        return actionType;
    }

}
