package com.kariqu.buyer.web.controller.login;

import com.kariqu.buyer.web.common.HeadAndFoot;
import com.kariqu.buyer.web.helper.BuySessionLogin;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.common.oauth.jointlogin.OauthInfo;
import com.kariqu.common.oauth.jointlogin.OuterUserInfo;
import com.kariqu.common.oauth.jointlogin.services.impl.Alipay;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.usercenter.Oauth;
import com.kariqu.usercenter.domain.AccountType;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 第三方登录
 * User: Alec
 * Date: 12-7-16
 * Time: 下午14:00
 */
@Controller
public class OauthController {

    private final Log logger = LogFactory.getLog(OauthController.class);

    @Autowired
    protected URLBrokerFactory urlBrokerFactory;

    @Autowired
    private BuySessionLogin buySessionLogin;

    @Autowired
    private UserService userService;

    @Autowired
    private HeadAndFoot headAndFoot;

    /**
     * @param backUrl 登陆成功过后浏览器重定向的地址
     * @param flag    开放合作方的标志，比如renren,qq
     * @param request
     * @throws IOException
     */
    @RequestMapping(value = "/user/jointLogin")
    public String jointLogin(@RequestParam("backUrl") String backUrl, @RequestParam("flag") AccountType flag,
                             HttpServletRequest request, Model model) throws IOException {
        String authorizationUrl = Oauth.join(flag).auth();

        if (StringUtils.isEmpty(authorizationUrl)) {
            modelAddMsg(request,model,"暂不支持" + flag.toDesc() + "登录");
            return "error";
        }

        request.getSession().setAttribute(LoginInfo.BACK_URL, checkBackUrl(backUrl));
        return "redirect:" + authorizationUrl;
    }

    /**
     * 支付宝登录
     *
     * @param backUrl
     * @param request
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/user/loginByAlipay")
    public String jointLoginByAlipay(String backUrl, HttpServletRequest request, Model model) throws IOException {
        request.getSession().setAttribute(LoginInfo.BACK_URL, checkBackUrl(backUrl));
        try {
            return "redirect:" + Alipay.getAuthorizationUrl();
        } catch (Exception e) {
            logger.error("获取支付宝登录地址出错", e);
            modelAddMsg(request,model,"很抱歉，暂时不支持支付宝登录");
            return "error";
        }
    }

    /**
     * @param request
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/oauth/alipay")
    public String alipayCallBack(HttpServletRequest request, Model model) throws IOException {
        String homePage = urlBrokerFactory.getUrl("BuyHome").toString();
        String is_success = request.getParameter("is_success");
        String real_name = request.getParameter("real_name");
        String token = request.getParameter("token");
        String user_id = request.getParameter("user_id");

        if (StringUtils.isEmpty(is_success) || StringUtils.isEmpty(real_name) || StringUtils.isEmpty(user_id) || !is_success.equals("T")) {
            logger.error("支付宝登录获取用户信息出错:is_success:" + is_success + ",real_name:" + real_name + ",user_id" + user_id);
            return "redirect:" + homePage;
        }
        OuterUserInfo userinfo = new OuterUserInfo();
        userinfo.setOuterId(user_id);
        userinfo.setUserName(real_name);
        userinfo.setUserHeadImg(null);
        userinfo.setFlag(AccountType.Alipay.toString());
        userinfo.setAccessToken(token);

        User user = userService.doLoginWhenOauthLater(userinfo, IpTools.getIpAddress(request));
        buySessionLogin.doLogin(request, user, user.getAccountType(), BuySessionLogin.LoginType.Oauth);

        String backUrl = (String) request.getSession().getAttribute(LoginInfo.BACK_URL);
        if (StringUtils.isBlank(backUrl)) {
            backUrl = homePage;
        }
        return "redirect:" + backUrl;
    }


    @RequestMapping(value = "/oauth/callback")
    public String oauthCallback(@RequestParam("flag") AccountType flag, String code, HttpServletRequest request, Model model) throws IOException {

        String homePage = urlBrokerFactory.getUrl("BuyHome").toString();
        // 第三方登录 用户取消跳转到首页
        String error = request.getParameter(OauthInfo.ERROR);
        String error_description = request.getParameter(OauthInfo.ERROR_DESC);
        if (StringUtils.isNotBlank(error)) {
            logger.error("第三方登陆时发生错误,错误信息:" + error + " " + error_description);
            modelAddMsg(request,model,flag.toDesc() + "登录出错！");
            return "error";
        }

        OuterUserInfo jointLoginInfo = Oauth.join(flag).userInfo(code);
        if (jointLoginInfo == null) {
            logger.error("第三方登陆错误, 外部用户信息为空: " + flag + ", code:" + code);
            modelAddMsg(request,model,flag.toDesc() + "登录出错！");
            return "error";
        }

        User user = userService.doLoginWhenOauthLater(jointLoginInfo, IpTools.getIpAddress(request));
        buySessionLogin.doLogin(request, user, user.getAccountType(), BuySessionLogin.LoginType.Oauth);

        String backUrl = (String) request.getSession().getAttribute(LoginInfo.BACK_URL);
        if (StringUtils.isBlank(backUrl)) {
            backUrl = homePage;
        }
        return "redirect:" + backUrl;
    }

    /**
     * 统一发送错误信息
     * @param request
     * @param model
     * @param msg
     */
    private void modelAddMsg(HttpServletRequest request, Model model, String msg) {
        headAndFoot.renderHeadFoot(request);
        model.addAttribute("site_title", "易居尚友情提示");
        model.addAttribute("msg", msg);
    }

    String checkBackUrl(String backUrl) throws UnsupportedEncodingException {
        String buyHomeUrl = urlBrokerFactory.getUrl("BuyHome").toString();
        if (StringUtils.isEmpty(backUrl)) {
            return buyHomeUrl;
        }
        backUrl = URLDecoder.decode(backUrl, "UTF-8");

        String mainUrl = urlBrokerFactory.getUrl("BuyDomain").toString();
        // 确保回调地址为 yijushang.com
        if (StringUtils.isNotBlank(backUrl)) {
            if ((backUrl.substring(backUrl.indexOf(".") + 1)).indexOf(mainUrl) == -1) {
                backUrl = buyHomeUrl;
            }
        } else {
            backUrl = buyHomeUrl;
        }
        return backUrl;
    }
}
