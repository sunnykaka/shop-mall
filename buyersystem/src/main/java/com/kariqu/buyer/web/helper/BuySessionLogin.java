package com.kariqu.buyer.web.helper;

import com.kariqu.buyer.web.common.CartTrackUtil;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.common.DateUtils;
import com.kariqu.common.iptools.IpTools;
import com.kariqu.productcenter.service.BrowsingHistoryService;
import com.kariqu.securitysystem.util.MD5;
import com.kariqu.session.KariquSessionLoginCallback;
import com.kariqu.session.util.SessionUtils;
import com.kariqu.tradecenter.domain.Cart;
import com.kariqu.tradecenter.domain.CartItem;
import com.kariqu.tradecenter.service.CartService;
import com.kariqu.usercenter.domain.AccountType;
import com.kariqu.usercenter.domain.StatisticsEntry;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserSessionInfo;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * buy 系统登录
 *
 * @author Athens(刘杰)
 * @Time 13-6-4 下午2:12
 */
public class BuySessionLogin {

    private static final Logger LOGGER = Logger.getLogger(BuySessionLogin.class);

    @Autowired
    protected UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private BrowsingHistoryService browsHistoryService;

    /**
     * <pre>
     * 登录类型
     *   包括有登录、注册(成功后直接登录)、第三方登录 和 自动登录(过滤器实现) 四种方式 >>
     *   登录和注册又包含窗口式和页面式(处理方式一样)
     * </pre>
     */
    public static enum LoginType {
        /**
         * 登录
         */
        Login,

        /**
         * 注册
         */
        Register,

        /**
         * 第三方登录
         */
        Oauth,

        /**
         * 自动登录
         */
        AutoLogin;
    }


    public void doLogin(HttpServletRequest request, User user, AccountType type, LoginType loginType) {
        SessionUtils.doLogin(new BuySessionInnerClass(request, user, loginType, type));
    }

    /**
     * 普通登录. 会设置自动登录、合并购物车、并记录用户登录信息.
     * 若用户有记住用户名, 还需要将用户名写入 session.
     *
     * @param user
     * @param rememberName
     */
    public void doLogin(HttpServletRequest request, User user, boolean rememberName) {
        SessionUtils.doLogin(new BuySessionInnerClass(request, user, rememberName));
    }

    private class BuySessionInnerClass implements KariquSessionLoginCallback {

        private SessionUserInfo sessionUserInfo = new SessionUserInfo();
        private LoginType loginType;
        private boolean rememberName;
        private HttpServletRequest request;

        /**
         * 登录时用此构造器.
         */
        public BuySessionInnerClass(HttpServletRequest request, User user, boolean rememberName) {
            buildSessionUserInfo(user, null);
            this.loginType = LoginType.Login;
            this.rememberName = rememberName;
            this.request = request;
        }

        public BuySessionInnerClass(HttpServletRequest request, User user, LoginType loginType, AccountType type) {
            buildSessionUserInfo(user, type);
            this.loginType = loginType;
            this.request = request;
        }

        private void buildSessionUserInfo(User user, AccountType type) {
            sessionUserInfo.setId(user.getId());
            sessionUserInfo.setUserName(user.getUserName());
            sessionUserInfo.setAccountType(type == null ? AccountType.KRQ : type);
            sessionUserInfo.setEmail(user.getEmail());
            sessionUserInfo.setPhone(user.getPhone());
        }

        @Override
        public HttpSession getHttpSession() {
            return request.getSession();
        }

        @Override
        public Serializable getUserId() {
            return sessionUserInfo.getId();
        }

        @Override
        public void beforeLogin() {
            request.getSession().setAttribute(LoginInfo.USER_SESSION_KEY, sessionUserInfo);
        }

        /**
         * 登录之后, 写 cookie, 合并购物车, 若用户有勾选记住用户名则记录
         */
        @Override
        public void afterLogin() {
            // 记录用户登录信息, 只第三方登录时不需要记录
            if (loginType != LoginType.Oauth) {
                recordLoginInfo(sessionUserInfo.getId(), sessionUserInfo.getUserName(), IpTools.getIpAddress(request));
            }
            // 合并购物车, 只自动登录时没必要合并
            if (loginType != LoginType.AutoLogin) {
                mergeCart(request);
                mergeHistory(request);
            }

            if (loginType == LoginType.Login || loginType == LoginType.Register) {
                // 设置自动登录
                writeSessionInClient(sessionUserInfo, request);
                if (rememberName) {
                    // 记住用户名
                    request.getSession().setAttribute(LoginInfo.AUTO_LOGIN_NAME_KEY, sessionUserInfo.getUserName());
                }
            }
        }
    }

    /**
     * 记录用户登录信息, 更新用户登录次数时当前登录时间.
     *
     * @param id
     * @param userName
     * @param ip
     */
    private void recordLoginInfo(int id, String userName, String ip) {
        // 记录此值不应该影响登录, 因此使用 try catch.
        try {
            userService.updateUserLoginInfo(id);
            StatisticsEntry entry = new StatisticsEntry();
            entry.setUserName(userName);
            entry.setEntryIP(ip);
            userService.loginOn(entry);
        } catch (Exception e) {
            LOGGER.error("记录用户(" + userName + ")登录信息时异常: " + e);
        }
    }


    /**
     * 将用户信息写入 cookie 以完成自动登录功能
     *
     * @param sessionUserInfo
     * @param request
     */
    private void writeSessionInClient(SessionUserInfo sessionUserInfo, HttpServletRequest request) {
        request.getSession().setAttribute(LoginInfo.AUTO_LOGIN_NAME_KEY, sessionUserInfo.getUserName());
        UserSessionInfo info = new UserSessionInfo();
        info.setUserId(String.valueOf(sessionUserInfo.getId()));

        // 将用户名加一个随机数, 再 md5 加密一把, 存入 db, 并写入 客户端. 以后通过比对此值完成自动登录
        String hashString = MD5.getHashString(sessionUserInfo.getUserName() + UUID.randomUUID());

        info.setCookieValue(hashString);
        info.setCheckInDate(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
        userService.createUserSessionInfo(info);
        request.getSession().setAttribute(LoginInfo.AUTO_LOGIN_CODE_KEY, hashString);
    }


    /**
     * 合并购物车
     *
     * @param request
     */
    private void mergeCart(HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        String cartTrackId = CartTrackUtil.getTrackId(request);
        if (StringUtils.isNotBlank(cartTrackId)) {
            Cart cartByCookie = cartService.getCartByTrackId(cartTrackId);

            Cart cartByUser = cartService.getCartByUserId(sessionUserInfo.getId());
            //如果存在用户购物车且cookie购物车也不为空，则要将cookie购物车导进用户购物车
            if (cartByUser != null && cartByCookie != null) {
                List<CartItem> cartItemList = cartByCookie.getCartItemList();
                for (CartItem cartItem : cartItemList) {
                    cartService.addSkuToCart(cartItem.getSkuId(), cartByUser, cartItem.getNumber());
                }
                cartService.deleteCart(cartByCookie.getId());
            } else if (cartByCookie != null) {
                cartByCookie.setUserId(sessionUserInfo.getId());
                cartByCookie.setTrackId("");
                cartService.updateCart(cartByCookie);
            }
        }
    }

    /**
     * 同步浏览历史记录
     *
     * @param request
     */
    private void mergeHistory(HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        String cartTrackId = CartTrackUtil.getTrackId(request);
        if (sessionUserInfo != null && StringUtils.isNotEmpty(cartTrackId)) {
            browsHistoryService.syncBrowsHistoryWhenLogin(sessionUserInfo.getId(), cartTrackId);
        }
    }
}
