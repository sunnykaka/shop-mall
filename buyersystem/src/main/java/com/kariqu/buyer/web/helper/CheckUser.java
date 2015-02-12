package com.kariqu.buyer.web.helper;

import com.kariqu.buyer.web.common.CheckUserException;
import com.kariqu.buyer.web.common.WebSystemUtil;
import com.kariqu.common.CheckUtils;
import com.kariqu.common.ChineseLength;
import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 检测用户信息 包括：验证码 用户名 邮箱
 * User: Alec
 * Date: 12-11-20
 * Time: 上午11:27
 */
public class CheckUser {

    private final Log logger = LogFactory.getLog(CheckUser.class);

    @Autowired
    private UserService userService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    public String checkBackUrl(String backUrl) throws UnsupportedEncodingException {
        // 若 backUrl 为空则默认使用主页.
        String bugHomeUrl = urlBrokerFactory.getUrl("BuyHome").toString();
        if (StringUtils.isBlank(backUrl)) {
            backUrl = bugHomeUrl;
        } else {
            // backUrl 为空, decode 会 NullPointException.
            backUrl = URLDecoder.decode(backUrl.trim(), "UTF-8");
            String mainUrl = urlBrokerFactory.getUrl("BuyDomain").toString();

            if ((backUrl.substring(backUrl.indexOf(".") + 1)).indexOf(mainUrl) == -1) {
                backUrl = bugHomeUrl;
            }
        }
        return backUrl;
    }

    public void checkImageCode(HttpServletRequest request, String imageCode) throws CheckUserException {
        Object imageCodeInSession = request.getSession().getAttribute("imageCode");
        if (null == imageCode || null == imageCodeInSession || !imageCode.equals(imageCodeInSession.toString())) {
            throw new CheckUserException("验证码错误");
        }
    }

    public void checkUserLogin(User user, String password) throws CheckUserException {
        if (null == user) {
            throw new CheckUserException("用户不存在");
        }
        if (user.isDelete()) {
            throw new CheckUserException("用户不存在");
        }
        if (!BCryptUtil.check(password, user.getPassword())) {
            throw new CheckUserException("密码错误");
        }
        if (user.isHasForbidden()) {
            throw new CheckUserException("用户被禁用");
        }
    }

    public void checkPasswordByStyle(String password) throws CheckUserException {
        if (StringUtils.isBlank(password)) {
            throw new CheckUserException("密码不能为空");
        }
        int passwordLength = password.length();
        if (!(passwordLength >= 6 && passwordLength <= 16)) {
            throw new CheckUserException("密码长度应为6-16位");
        }
    }

    public void checkUserName(String userName) throws CheckUserException {
        if (StringUtils.isBlank(userName)) {
            throw new CheckUserException("用户名不能为空");
        }

        int userNameLength = ChineseLength.length(userName);
        if (CheckUtils.checkEmail(userName)) {
            if (!(userNameLength >= 5 && userNameLength <= 50)) {
                throw new CheckUserException("邮箱能由5-50个字符组成");
            }
            checkUserEmail(userName);
        } else {
            if (!(userNameLength >= 4 && userNameLength <= 20)) {
                throw new CheckUserException("用户名只能由4-20个字符组成");
            }
        }

        User user = userService.getUserByUserName(userName);
        if (null != user) {
            throw new CheckUserException("该用户已存在");
        }
    }


    public void checkRegisterNumber(HttpServletRequest request) throws CheckUserException {
        String registerIP = IpTools.getIpAddress(request);
        int count = userService.selectCountForSameIPToday(registerIP);
        if (logger.isDebugEnabled()) {
            logger.debug(registerIP + "注册次数：" + count);
        }
        if (count >= 5) {
            throw new CheckUserException("该IP地址当天注册超过限制");
        }
    }

    public void checkUserPassword(User user, String oldPassword, String newPassword) throws CheckUserException {
        if (user == null) {
            throw new CheckUserException("账户不存在");
        }
        // 校验原始密码
        if (!(user.isActive() && BCryptUtil.check(oldPassword, user.getPassword()))) {
            throw new CheckUserException("原始密码有误");
        }
        // 校验密码格式
        checkPasswordByStyle(newPassword);
    }

    public void checkUserEmail(String email) throws CheckUserException {
        if (logger.isDebugEnabled()) {
            logger.debug("注册邮箱为：" + email);
        }
        if (StringUtils.isBlank(email)) {
            throw new CheckUserException("邮箱不能为空");
        }
        if (!WebSystemUtil.checkEmail(email)) {
            throw new CheckUserException("邮箱格式有误");
        }

        User user = userService.getUserByEmail(email);

        if (null != user) {
            throw new CheckUserException("该邮箱已被使用");
        }
    }

    public void checkPhone(String phone) throws CheckUserException {
        if (!WebSystemUtil.checkMobile(phone)) {
            throw new CheckUserException("不是有效的手机号");
        }

        User user = userService.getUserByPhone(phone);
        if (user != null) {
            throw new CheckUserException("手机号码已被使用");
        }
    }

}
