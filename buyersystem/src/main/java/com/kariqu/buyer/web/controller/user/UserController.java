package com.kariqu.buyer.web.controller.user;

import com.kariqu.buyer.web.common.*;
import com.kariqu.buyer.web.helper.CheckUser;
import com.kariqu.common.CheckUtils;
import com.kariqu.common.DateUtils;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.securitysystem.util.MD5;
import com.kariqu.usercenter.domain.MailHeader;
import com.kariqu.usercenter.domain.MessageTemplateName;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.MessageTaskService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户注册 激活
 * User: Asion
 * Date: 11-5-12
 * Time: 下午3:38
 */

@Controller
public class UserController {

    private final Log logger = LogFactory.getLog(UserController.class);

    private final String emailUserActive = "active.yijushang.com";

    @Autowired
    private UserService userService;

    @Autowired
    private HashMap mailKey;

    @Autowired
    private MessageTaskService messageTaskService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private CheckUser checkUser;

    /**
     * 检查注册用户姓名
     *
     * @param userName
     * @throws IOException
     */
    @RequestMapping(value = "/user/checkUserNameForRegister")
    public void checkUserNameForRegister(String userName, HttpServletResponse response) throws IOException {
        try {
            // 校验用户名
            checkUser.checkUserName(userName);
            new JsonResult(true).toJson(response);
        } catch (CheckUserException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }


    /**
     * 验证注册邮箱，邮箱不能重复注册
     *
     * @param email
     * @throws IOException
     */
    @RequestMapping(value = "/user/checkUserEmailForRegister")
    public void checkUserEmailForRegister(String email, HttpServletResponse response) throws IOException {
        try {
            // 校验用户邮箱
            checkUser.checkUserEmail(email);
            new JsonResult(true).toJson(response);
        } catch (CheckUserException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }


    /**
     * 激活用户，重新发送邮件
     *
     * @param userName
     * @throws IOException
     */
    @RequestMapping(value = "/user/sendMailForActiveUser", method = RequestMethod.POST)
    public void sendMailForActiveUser(String userName, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(userName)) {
            new JsonResult(false, "用户不存在").toJson(response);
        }
        User user = userService.getUserByUserName(new String(Base64.decodeBase64(userName)));
        if (null == user) {
            new JsonResult(false, "用户不存在").toJson(response);
        }

        boolean result = sendMail(user);

        if (result) {
            final String email = user.getEmail();
            new JsonResult(true).addData("backUrl", getMailLoginUrl(email)).addData("actionType", "doClose").toJson(response);
        } else {
            new JsonResult(false, "发送激活邮件失败，请重新发送!").toJson(response);
        }
    }

    /**
     * 激活注册用户
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/user/activeUser")
    @RenderHeaderFooter
    public String activeUser(String email, String checkCode, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = userService.getUserByEmail(email);
        if (null != user && null != checkCode) {
            String backUrl = urlBrokerFactory.getUrl("SendMailForActiveUser").addQueryData("userName", user.getUserName()).toString();

            StringBuffer userCheckCode = new StringBuffer();
            userCheckCode.append(user.getUserName())
                    .append(emailUserActive);
            String userNameKey = MD5.getHashString(userCheckCode.toString());

            int userNameKey_length = userNameKey.length();

            if (checkCode.length() > userNameKey_length) {
                String userNameKey_FromEmail = checkCode.substring(0, userNameKey_length);
                String checkTime = checkCode.substring(userNameKey_length);
                boolean isTimeOut = ((DateUtils.getCurrentDate().getTime() - Long.parseLong(checkTime)) / 1000 / 60) > 30;
                boolean isUserInfoRight = userNameKey.equals(userNameKey_FromEmail);
                if (isTimeOut) {
                    resultToPage(model, "2", this.generateSendActiveMailDom(user.getUserName()), "激活账户超时", backUrl);
                } else {
                    if (user.isActive()) {
                        resultToPage(model, "0", this.generateLoginUrlofActiveUserDom(), "帐号已激活", null);
                    } else if (isUserInfoRight) {
                        user.setActive(true);
                        userService.updateUser(user);
                        resultToPage(model, "1", this.generateLoginUrlofActiveUserDom(), "账户激活成功", null);
                    } else {
                        resultToPage(model, "3", "请确认是否使用邮箱收到的链接", "账户激活失败", backUrl);
                    }
                }
            } else {
                resultToPage(model, "3", "请确认是否使用邮箱收到的链接", "账户激活链接错误", backUrl);
            }
            model.addAttribute("userName", user.getUserName());
        } else {
            resultToPage(model, "4", "用户不存在，或激活链接不正确，账户激活失败", "账户激活失败", null);
        }


        return "user/userActiveResult";
    }


    private String generateSendActiveMailDom(String userName) throws UnsupportedEncodingException {
        StringBuffer result = new StringBuffer();
        result.append("<a id = \"sendMailforActiveUser\" href='")
                .append(urlBrokerFactory.getUrl("SendMailForActiveUserHttps"))
                .append("?resendFlag=true")
                .append("&userName=")
                .append(new String(Base64.encodeBase64(userName.getBytes())))
                .append("'>点击重新发送激活邮件</a>");
        return result.toString();
    }

    private Model resultToPage(Model model, String activeResult, String msg, String title, String backUrl) {
        model.addAttribute("activeResult", activeResult);
        model.addAttribute("msg", msg);
        model.addAttribute("title", title);
        model.addAttribute("backUrl", backUrl);
        return model;
    }

    /**
     * 发送HTML邮件
     *
     * @param user
     * @return
     */
    private boolean sendMail(User user) {
        final Map mailParams = new HashMap();
        mailParams.put("url", generateActiveUrl(user));
        mailParams.put("userName", user.getUserName());
        mailParams.put("title", MailConstants.ACCOUNT_ACTIVATE_TITLE);
        mailParams.put(RenderConstants.URL_Broker, urlBrokerFactory);

        // mailFrom 与 JavaMailSenderImpl 中的 userName 值一致, 避免多个地方维护, 这个值移到 JavaMailSenderImpl 发送邮件之前去设置.
        MailHeader mailHeader = new MailHeader();
        mailHeader.setMailSubject(MailConstants.ACCOUNT_ACTIVATE_TITLE);
        mailHeader.setMailTo(user.getEmail());
        mailHeader.setParams(mailParams);
        try {
            messageTaskService.sendHtmlMail(mailHeader, MessageTemplateName.ACCOUNT_ACTIVATE);
        } catch (Exception e) {
            logger.error("邮件发送失败：" + e);
            return false;
        }
        return true;
    }

    /**
     * 生成激活用户地址
     *
     * @param user
     * @return
     */
    private String generateActiveUrl(User user) {
        StringBuffer userCheckCode = new StringBuffer();
        StringBuffer activeUrl = new StringBuffer();

        /**
         * 用户验证链接组成部分为：加密（用户名，关键字 ）+编码（当前时间）组成
         */
        userCheckCode.append(MD5.getHashString(user.getUserName() + emailUserActive)).append(String.valueOf(DateUtils.getCurrentDate().getTime()));

        activeUrl.append(urlBrokerFactory.getUrl("ActiveUser").toString())
                .append("?email=")
                .append(user.getEmail())
                .append("&checkCode=")
                .append(userCheckCode.toString());

        return activeUrl.toString();
    }


    /**
     * 获取用户注册邮箱的登陆地址
     *
     * @param email
     * @return
     */
    private String getMailLoginUrl(String email) {
        String[] domain = email.split("@");
        if (domain.length != 2) {
            return "";
        } else {
            if (null == mailKey.get(domain[1])) {
                return "";
            }
            return mailKey.get(domain[1]).toString();
        }
    }

    /**
     * 导航到注册Frame
     *
     * @param model
     * @return
     */
    @Token
    @RequestMapping(value = "/user/userRegister")
    public String forwardUserRegister(String actionType, Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        model.addAttribute(RenderConstants.URL_Broker, urlBrokerFactory);
        model.addAttribute("imageVersion", RandomStringUtils.randomNumeric(8));
        model.addAttribute("OauthBackUrl", URLEncoder.encode(urlBrokerFactory.getUrl("BuyHome").toString(), "UTF-8"));
        model.addAttribute("actionType", getActionType(actionType));
        model.addAttribute("my_account_url", URLEncoder.encode(urlBrokerFactory.getUrl("MyAccountToData").toString(), "UTF-8"));
        return "user/userRegister";
    }

    /**
     * 跳转或关闭弹出层
     *
     * @param backUrl
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/user/jumpPage")
    public String jumpMailLogin(String backUrl, String actionType, Model model, HttpServletRequest request) {
        model.addAttribute("backUrl", backUrl);
        model.addAttribute("actionType", actionType);
        return "user/jump";
    }

    /**
     * 通过页面登陆跳转到首页
     *
     * @return
     */
    @RequestMapping(value = "/user/redirectBuyHome")
    public String jumpBuyHome() {
        return "redirect:" + urlBrokerFactory.getUrl("BuyHome").toString();
    }

    private String generateLoginUrlofActiveUserDom() {
        StringBuffer result = new StringBuffer();
        result.append("<a href=" + urlBrokerFactory.getUrl("ToPageLogin") + ">点击登陆</a>");
        return result.toString();
    }

    private String getActionType(String actionType) {
        if (StringUtils.isBlank(actionType)) {
            actionType = "doClose";
        }
        return actionType;
    }

    /**
     * 取消订阅
     *
     * @param email
     * @param model
     */
    @RequestMapping(value = "/user/unSubscribe")
    @RenderHeaderFooter
    public String emailUnSubscribe(String email, Model model, HttpServletRequest request) throws IOException {
        if (!CheckUtils.checkEmail(email)) {
            if (logger.isInfoEnabled()) {
                logger.info("email 无效: [" + email + "], 访问 IP: [" + IpTools.getIpAddress(request) + "]");
            }
            model.addAttribute("site_title", "数据错误");
            model.addAttribute("msg", "email 格式不正确: " + email);
            return "error";
        }

        if (userService.queryUnSubscribe(email) > 0) {
            model.addAttribute("site_title", "邮箱已取消订阅过");
            model.addAttribute("msg", "email 已经取消订阅过: " + email);
        } else {
            userService.addUnSubscribe(email);

            model.addAttribute("site_title", "取消订阅成功");
            model.addAttribute("msg", "取消订阅成功!");
        }
        return "user/unSubscribeSuccess";
    }

}
