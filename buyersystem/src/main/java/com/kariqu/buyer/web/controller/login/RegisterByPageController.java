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
import com.kariqu.usercenter.domain.AccountType;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * User: Asion
 * Date: 13-6-5
 * Time: 上午11:26
 */
@Controller
public class RegisterByPageController {

    private final Log logger = LogFactory.getLog(RegisterByPageController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private BuySessionLogin buySessionLogin;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private CheckUser checkUser;

    /**
     * 注册用户
     *
     * @return
     */
    @RequestMapping(value = "/user/users", method = RequestMethod.POST)
    public void createUser(String userName, String password, String imageCode,
                           final HttpServletResponse response, final HttpServletRequest request) throws IOException, ServletException {
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
            new JsonResult(e.getMessage()).toJson(response);
            return;
        }

        user.setUserName(userName.trim());
        user.setPassword(BCryptUtil.encryptPassword(password));
        user.setRegisterDate(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
        user.setRegisterIP(IpTools.getIpAddress(request));
        user.setActive(true);
        try {
            userService.createUser(user);
            //userPointService.addUserCurrency(user.getId(), integralService.getRegisterCurrencyCount(), "注册得积分"); // 注册送的积分数量
            buySessionLogin.doLogin(request, user, AccountType.KRQ, BuySessionLogin.LoginType.Register);
            new JsonResult(true, "注册成功").addData("backUrl", urlBrokerFactory.getUrl("BuyHome").toString()).toJson(response);
        } catch (Exception e) {
            logger.error("用户" + user.getUserName() + "注册失败！");
            new JsonResult(false, "注册失败请您联系客服人员进行处理！").toJson(response);
        }

    }


}
