package com.kariqu.buyer.web.controller.user;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.DateUtils;
import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.securitysystem.util.MD5;
import com.kariqu.usercenter.domain.MailHeader;
import com.kariqu.usercenter.domain.MessageTemplateName;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.MessageTaskService;
import com.kariqu.usercenter.service.UserService;
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
 * 密码控制
 * User: ennoch
 * Date: 12-8-17
 * Time: 上午10:24
 */
@Controller
public class PasswordController {

    protected final Log logger = LogFactory.getLog(PasswordController.class);

    private final String emailChangePsw = "changepsw.yijushang.com";

    @Autowired
    private UserService userService;

    @Autowired
    private MessageTaskService messageTaskService;

    @Autowired
    private HashMap mailKey;

    @Autowired
    protected URLBrokerFactory urlBrokerFactory;

    /**
     * 找回密码验证用户名称和邮箱
     *
     * @param email
     */
    @RequestMapping(value = "/user/checkUserForRetakePassword")
    public void checkUserForRetakePassword(String email, String code, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Object imageCodeInSession = request.getSession().getAttribute("imageCode");
        if (null == code || imageCodeInSession == null || !code.equals(imageCodeInSession.toString())) {
            new JsonResult(false, "验证码错误").toJson(response);
            return;
        }
        User user = userService.getUserByEmail(email);
        if (user == null) {
            new JsonResult(false, "安全邮箱错误").toJson(response);
            return;
        }
        if (!email.equals(user.getEmail())) {
            new JsonResult(false, "安全邮箱错误").toJson(response);
            return;
        }
        if (!sendMail(user)) {
            new JsonResult(false, "邮件发送失败，请重试").toJson(response);
            return;
        }

        new JsonResult(true, "邮件已发送，请注意查收").addData("backUrl", getMailLoginUrl(user.getEmail()))
                .addData("resend", urlBrokerFactory.getUrl("SendMailForRetakePasswordHttps")
                        .addQueryData("userEmail", email).toString()).toJson(response);
    }

    /**
     * 效验用户和邮箱是否匹配
     *
     * @param userName
     * @param email
     * @throws IOException
     */
    @RequestMapping(value = "/user/checkMatchingOfUserForRetakePassword")
    public void checkMatchingOfUser(final String userName, String email, HttpServletResponse response) throws IOException {
        final User user = userService.getUserByUserName(userName);
        if (!user.getEmail().equals(email)) {
            new JsonResult(false, "用户名与邮箱不匹配").toJson(response);
        } else {
            new JsonResult(true).toJson(response);
        }

    }

    /**
     * 找回密码，验证邮箱
     *
     * @param userEmail
     * @throws IOException
     */
    @RequestMapping(value = "/user/checkUserEmailforRetakePassword")
    public void checkUserNameForRetakePassword(String userEmail, HttpServletResponse response) throws IOException {
        User user = userService.getUserByEmail(userEmail);
        if (null == user) {
            new JsonResult(false, "邮箱不存在").toJson(response);
        } else {
            new JsonResult(true).toJson(response);
        }
    }

    /**
     * 修改密码时，验证原密码是否正确
     *
     * @param oldPassword
     * @throws IOException
     */
    @RequestMapping(value = "/user/checkOldPassword", method = RequestMethod.POST)
    public void checkOldPassword(String oldPassword, HttpServletResponse response, HttpServletRequest request) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        String passWord = userService.getUserById(sessionUserInfo.getId()).getPassword();
        if (null == sessionUserInfo) {
            new JsonResult(false, "用户登陆过期，请重新登陆！").toJson(response);
            return;
        }
        boolean checkResult = BCryptUtil.check(oldPassword, passWord);
        if (!checkResult) {
            new JsonResult(false, "原密码错误").toJson(response);
        } else {
            new JsonResult(true).toJson(response);
        }
    }

    /**
     * 用户在京东或天猫商城购买后, 易居尚建立账户并邮件通知用户, 而后由从邮件过来的修改密码(与使用邮箱密码找回不同)
     */
    @RequestMapping(value = "/user/mallChangePwd")
    @RenderHeaderFooter
    public String mallChangePwd(String email, HttpServletRequest request, Model model) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            if (logger.isInfoEnabled()) {
                logger.info("没有此 email 的相关用户信息: [" + email + "], 访问 IP: [" + IpTools.getIpAddress(request)+ "]");
            }
            model.addAttribute("site_title", "数据错误");
            model.addAttribute("msg", "没有此 email 的相关用户信息: " + email);
            return "error";
        }

        model.addAttribute("isMall", true);
        model.addAttribute("userName", email);
        return "user/changepassword";
    }

    /**
     * 通过“找回密码”修改用户密码
     *
     * @param passWord
     * @throws IOException
     */
    @RequestMapping(value = "/user/changePassword", method = RequestMethod.POST)
    public void changePassword(String email, String checkCode, String oldPassWord, String passWord, HttpServletResponse response) throws IOException {
        try {
            User user = userService.getUserByEmail(email);
            if (StringUtils.isNotBlank(oldPassWord)) {
                if (!BCryptUtil.check(oldPassWord, user.getPassword())) {
                    new JsonResult(false, "原密码错误").toJson(response);
                    return;
                }
            }

            if (StringUtils.isBlank(checkCode)) {
                new JsonResult(false, "地址验证失败，请确认是否为邮箱收到的地址").toJson(response);
                return;
            }
            if (user == null) {
                new JsonResult(false, "地址验证失败，请确认是否为邮箱收到的地址").toJson(response);
                return;
            }
            StringBuilder userCheckCode = new StringBuilder();
            userCheckCode.append(user.getEmail()).append(emailChangePsw).append(user.getPassword());
            String userEmailKey = MD5.getHashString(userCheckCode.toString());

            int userEmailKey_length = userEmailKey.length();
            if (checkCode.length() <= userEmailKey_length) {
                new JsonResult(false, "请确认是否使用邮箱收到的链接").toJson(response);
                return;
            }

            String checkTime = checkCode.substring(userEmailKey_length);
            boolean isTimeOut = ((DateUtils.getCurrentDate().getTime() - Long.parseLong(checkTime)) / 1000 / 60) > 10;
            if (isTimeOut) {
                new JsonResult(false, "修改账户链接访问超时").toJson(response);
                return;
            }

            String userEmailKey_FromEmail = checkCode.substring(0, userEmailKey_length);
            if (!userEmailKey.equals(userEmailKey_FromEmail)) {
                new JsonResult(false, "请确认是否使用邮箱收到的链接").toJson(response);
                return;
            }

            user.setPassword(BCryptUtil.encryptPassword(passWord));
            userService.updateUser(user);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("修改密码失败：" + e);
            new JsonResult(false, "修改密码失败").toJson(response);
        }
    }


    /**
     * 导航到找回密码页面
     *
     * @return
     */
    @RequestMapping(value = "/user/forwardRetakePassword")
    public String retakePassword() throws UnsupportedEncodingException {
        return "user/checkUser";
    }

    /**
     * 找回密码，重新发送邮件
     *
     * @param userEmail
     * @throws IOException
     */
    @RequestMapping(value = "/user/sendMailForRetakePassword", method = RequestMethod.POST)
    public void sendMailForRetakePassword(String userEmail, Model model, HttpServletResponse response, HttpServletRequest request) throws IOException {
        final User user = userService.getUserByEmail(userEmail);
        boolean sendMailResult = sendMail(user);
        if (sendMailResult) {
            new JsonResult(true, "邮件已发送，请注意查收").addData("backUrl", getMailLoginUrl(user.getEmail())).toJson(response);
        } else {
            new JsonResult(false, "邮件发送失败，请重试").toJson(response);
        }
    }

    /**
     * 导航到修改密码页面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/user/forwardChangePassword")
    @RenderHeaderFooter
    public String forwardChangePassword(String email, String checkCode, Model model) throws UnsupportedEncodingException {
        User user = userService.getUserByEmail(email);
        if (null == user) {
            resultToPage(model, "4", "地址验证失败，请确认是否为邮箱收到的地址", "地址验证失败", null);
            return "user/userActiveResult";
        }

        model.addAttribute("userName", user.getUserName());
        model.addAttribute("backUrl", getMailLoginUrl(email));

        if (checkCode == null) {
            resultToPage(model, "4", "地址验证失败，请确认是否为邮箱收到的地址", "地址验证失败", null);
            return "user/userActiveResult";
        }
        StringBuilder userCheckCode = new StringBuilder();
        userCheckCode.append(user.getEmail()).append(emailChangePsw).append(user.getPassword());
        String userEmailKey = MD5.getHashString(userCheckCode.toString());
        String backUrl = urlBrokerFactory.getUrl("SendMailForRetakePassword").addQueryData("userEmail",email).toString();

        int userEmailKey_length = userEmailKey.length();
        if (checkCode.length() <= userEmailKey_length) {
            resultToPage(model, "3", "请确认是否使用邮箱收到的链接", "账户修改链接错误", backUrl);
            return "user/userActiveResult";
        }

        String userEmailKey_FromEmail = checkCode.substring(0, userEmailKey_length);
        String checkTime = checkCode.substring(userEmailKey_length);
        boolean isTimeOut = ((DateUtils.getCurrentDate().getTime() - Long.parseLong(checkTime)) / 1000 / 60) > 10;
        boolean isUserInfoRight = userEmailKey.equals(userEmailKey_FromEmail);
        if (isTimeOut) {
            resultToPage(model, "2", "修改账户链接访问超时", "修改账户超时", backUrl);
            return "user/userActiveResult";
        }
        if (!isUserInfoRight) {
            resultToPage(model, "3", "请确认是否使用邮箱收到的链接", "账户修改失败", backUrl);
            return "user/userActiveResult";
        }

        model.addAttribute("email", email);
        model.addAttribute("checkCode", checkCode);
        return "user/changepassword";
    }

    /**
     * 生成修改密码的链接地址
     *
     * @return
     */
    private String generateChangePasswordUrl(User user) throws UnsupportedEncodingException {
        StringBuilder userCheckCode = new StringBuilder();
        StringBuilder changePasswordUrl = new StringBuilder();

        userCheckCode.append(MD5.getHashString(user.getEmail() + emailChangePsw + user.getPassword())).append(String.valueOf(DateUtils.getCurrentDate().getTime()));

        changePasswordUrl.append(urlBrokerFactory.getUrl("ForwardChangePassword").toString())
                .append("?email=")
                .append(user.getEmail())
                .append("&checkCode=")
                .append(userCheckCode.toString());

        return changePasswordUrl.toString();
    }

    /**
     * 生成在页面中显示的修改密码链接DOM结构
     *
     * @return
     */
    private String generateSendMailAboutChangePasswordDom() {
        StringBuilder result = new StringBuilder();
        result.append("<a id = \"SendMailForRetakePassword\" href='")
                .append(urlBrokerFactory.getUrl("SendMailForRetakePasswordHttps"))
                .append("'>点击重新发送找回密码验证邮件</a>");
        return result.toString();
    }

    /**
     * 导航到找回密码Frame
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/user/userPassword")
    @RenderHeaderFooter
    public String forwardUserPassword(Model model) throws UnsupportedEncodingException {
        model.addAttribute("imageVersion", RandomStringUtils.randomNumeric(8));
        model.addAttribute("OauthBackUrl", URLEncoder.encode(urlBrokerFactory.getUrl("BuyHome").toString(), "UTF-8"));
        return "user/userPassword";
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
     * 发送HTML邮件
     *
     * @param user
     * @return
     */
    private boolean sendMail(User user) throws UnsupportedEncodingException {
        final Map mailParams = new HashMap();
        mailParams.put("url", generateChangePasswordUrl(user));
        mailParams.put("userName", user.getUserName());
        mailParams.put("title", MailConstants.PASSWORD_RECOVERY_TITLE);
        mailParams.put(RenderConstants.URL_Broker, urlBrokerFactory);

        // mailFrom 与 JavaMailSenderImpl 中的 userName 值一致, 避免多个地方维护, 这个值移到 JavaMailSenderImpl 发送邮件之前去设置.
        MailHeader mailHeader = new MailHeader();
        mailHeader.setMailSubject(MailConstants.PASSWORD_RECOVERY_TITLE);
        mailHeader.setMailTo(user.getEmail());
        mailHeader.setParams(mailParams);

        try {
            messageTaskService.sendHtmlMail(mailHeader, MessageTemplateName.PASSWORD_RECOVERY);
        } catch (Exception e) {
            logger.error("邮件发送失败：" + e);
            return false;
        }
        return true;

    }


    /**
     * 后台修改用户密码
     *
     * @param password
     * @throws IOException
     */
    @RequestMapping(value = "/my/changePassword", method = RequestMethod.POST)
    public void changeUserPassword(String oldPassword, String password, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            if (null == sessionUserInfo) {
                new JsonResult(false, "用户登陆过期，请重新登陆！").toJson(response);
                return;
            }

            User user = userService.getUserById(sessionUserInfo.getId());
            boolean checkResult = BCryptUtil.check(oldPassword, user.getPassword());
            if (!checkResult) {
                new JsonResult(false, "原密码错误").toJson(response);
            } else {
                user.setPassword(BCryptUtil.encryptPassword(password));
                userService.updateUser(user);
                new JsonResult(true).toJson(response);
            }
        } catch (Exception e) {
            logger.error("修改密码失败：" + e);
            new JsonResult(false, "修改密码失败").toJson(response);
        }
    }


    private Model resultToPage(Model model, String activeResult, String msg, String title, String backUrl) {
        model.addAttribute("activeResult", activeResult);
        model.addAttribute("msg", msg);
        model.addAttribute("title", title);
        model.addAttribute("backUrl", backUrl);
        return model;
    }

}
