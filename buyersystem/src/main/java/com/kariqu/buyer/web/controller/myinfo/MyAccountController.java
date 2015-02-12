package com.kariqu.buyer.web.controller.myinfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kariqu.buyer.web.common.*;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.CheckUser;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.CheckUtils;
import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.domain.ProductCollect;
import com.kariqu.productcenter.service.ConsultationService;
import com.kariqu.productcenter.service.ProductPictureResolver;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.OrderItem;
import com.kariqu.tradecenter.service.CouponService;
import com.kariqu.tradecenter.service.IntegralService;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.usercenter.domain.Currency;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserData;
import com.kariqu.usercenter.service.UserPointService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户中心修改密码
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-3
 *        Time: 上午11:43
 */
@Controller
public class MyAccountController {

    private final Log logger = LogFactory.getLog(MyAccountController.class);

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CheckUser checkUser;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private UserPointService userPointService;

    private static final String NAME = "name", URL = "url", NUMBER = "number";

    @RequestMapping(value = "/myself")
    public void userSelf(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        if (userInfo == null) {
            new JsonResult(false, "用户未登陆").toJson(response);
            return;
        }

        List<Map<String, Object>> myselfList = new ArrayList<Map<String, Object>>();

        Object count = tradeCenterUserClient.queryValuationCountByUserIdAndAppraise(userInfo.getId(), 0).get("count");

        int noValuation = 0;
        if (count != null) noValuation += NumberUtils.toInt(count.toString());

        Map<String, Object> orderMap = new LinkedHashMap<String, Object>();
        orderMap.put(NAME, "待处理订单");
        orderMap.put(URL, urlBrokerFactory.getUrl("MyOrders").toString());
        orderMap.put(NUMBER, orderQueryService.queryCountForNoProcessOrder(userInfo.getId()) + noValuation);
        orderMap.put("type", "order");

        int pageNo = 1;
        String orderSizeStr = request.getParameter("orderSize");
        int orderSize = 5;
        if (StringUtils.isNotEmpty(orderSizeStr)) {
            orderSize = Integer.parseInt(orderSizeStr);
        }
        Page<Order> orderPage = tradeCenterUserClient.getNotCancelOrderPageByUserId(userInfo.getId(), new Page<Order>(pageNo, orderSize));
        List<Map<String, String>> orderList = Lists.newArrayList();
        if (orderPage.getResult().size() > 0) {
            for (Order order : orderPage.getResult()) {
                for (OrderItem orderItem : order.getOrderItemList()) {
                    Map<String, String> orderIteminfoMap = Maps.newHashMap();
                    orderIteminfoMap.put("orderURL", urlBrokerFactory.getUrl("OrderDetail").addQueryData("orderNo", order.getOrderNo()).toString());
                    orderIteminfoMap.put("skuMainPicture", ProductPictureResolver.getMinSizeImgUrl(orderItem.getSkuMainPicture()));
                    orderIteminfoMap.put("orderState", orderItem.getOrderState().customerDesc());
                    orderList.add(orderIteminfoMap);
                }
            }
        }
        orderMap.put("orderList", orderList);
        myselfList.add(orderMap);

        Map<String, Object> accountMap = new LinkedHashMap<String, Object>();
        accountMap.put(NAME, "帐户中心");
        accountMap.put(URL, urlBrokerFactory.getUrl("MyAccount").toString());
        myselfList.add(accountMap);

        Map<String, Object> consultMap = new LinkedHashMap<String, Object>();
        consultMap.put(NAME, "咨询回复");
        consultMap.put(URL, urlBrokerFactory.getUrl("MyConsult").toString());
        consultMap.put(NUMBER, consultationService.queryConsultationCountByUserId(userInfo.getId(), false));
        myselfList.add(consultMap);

        Map<String, Object> pointMap = new LinkedHashMap<String, Object>();
        pointMap.put(NAME, "我的积分");
        pointMap.put(URL, urlBrokerFactory.getUrl("MyPoint").toString());
        pointMap.put(NUMBER, userService.getUserById(userInfo.getId()).getCurrency());
        myselfList.add(pointMap);

        Map<String, Object> backMap = new LinkedHashMap<String, Object>();
        backMap.put(NAME, "申请售后服务");
        backMap.put(URL, urlBrokerFactory.getUrl("MyBackGoodsApplyList").toString());
        myselfList.add(backMap);

        /*Map<String, Object> couponMap = new LinkedHashMap<String, Object>();
        couponMap.put(NAME, "我的现金券");
        couponMap.put(URL, urlBrokerFactory.getUrl("MyCoupon").toString());
        couponMap.put(NUMBER, couponService.queryNotUsedCouponCountByUserId(userInfo.getId()));
        myselfList.add(couponMap);*/

        Map<String, Object> addressMap = new LinkedHashMap<String, Object>();
        addressMap.put(NAME, "收货地址管理");
        addressMap.put(URL, urlBrokerFactory.getUrl("MyAddress").toString());
        myselfList.add(addressMap);

        Map<String, Object> followMap = new LinkedHashMap<String, Object>();
        followMap.put(NAME, "我的关注");
        followMap.put(URL, urlBrokerFactory.getUrl("MyFavorites").toString());
        followMap.put(NUMBER, productService.queryProductCollectNum(userInfo.getId()));
        followMap.put("type", "follow");

        String followSizeStr = request.getParameter("followSize");
        int followSize = 5;
        if (StringUtils.isNotEmpty(followSizeStr)) {
            followSize = Integer.parseInt(followSizeStr);
        }
        Page<ProductCollect> productCollectPage = productService.queryProductCollectByPage(pageNo, followSize, userInfo.getId());
        List<Map<String, String>> productCollectList = Lists.newArrayList();
        if (productCollectPage.getResult().size() > 0) {
            for (ProductCollect productCollect : productCollectPage.getResult()) {
                Map<String, String> productCollectMap = Maps.newHashMap();
                productCollectMap.put("productCollectURL", urlBrokerFactory.getUrl("ProductDetail").addQueryData("productId", productCollect.getProductId()).toString());
                productCollectMap.put("productMainPicture", ProductPictureResolver.getMinSizeImgUrl(productCollect.getProductMainPicture()));
                productCollectMap.put("productName", productCollect.getProductName());
                productCollectMap.put("unitPrice", productCollect.getUnitPrice());
                productCollectList.add(productCollectMap);
            }
        }
        followMap.put("productCollectList", productCollectList);
        myselfList.add(followMap);

        new JsonResult(true).addData("result", myselfList).toJson(response);
    }

    /**
     * 跳转后台修改密码页
     */
    @RenderHeaderFooter
    @RequestMapping(value = "/my/toChangePsw")
    @PageTitle("修改密码")
    public String forwardMessage(Model model) {
        model.addAttribute("contentVm", "myinfo/changePsw.vm");
        return "myinfo/myInfoLayout";
    }

    /**
     * 在“我的账户详情”中修改用户密码
     */
    //@RenderHeaderFooter
    @RequestMapping(value = "/my/changePsw", method = RequestMethod.POST)
    //@PageTitle("修改密码")
    public void changePassword(String oldPassword, String password, HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        try {
            User user = userService.getUserById(sessionUserInfo.getId());
            // 校验密码格式
            checkUser.checkUserPassword(user, oldPassword, password);

            user.setPassword(BCryptUtil.encryptPassword(password));
            userService.updateUser(user);

            new JsonResult(true, "密码修改成功").toJson(response);
        } catch (CheckUserException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        } catch (Exception e) {
            logger.error("用户" + sessionUserInfo.getId() + "_" + sessionUserInfo.getUserName() + "后台修改密码出错", e);

            new JsonResult(false, "密码修改失败").toJson(response);
        }
    }


    /**
     * 完善资料
     */
    //@RenderHeaderFooter
    @RequestMapping(value = "/my/myData", method = RequestMethod.POST)
    //@PageTitle("账户设置")
    //@CheckFormToken
    public void myUserData(String userName, String email, String phone, String password, UserData userData,
                           HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        UserData oldUserData = userService.queryUserDataByUserId(sessionUserInfo.getId());
        User oldUser = userService.getUserById(sessionUserInfo.getId());
        String oldUserName = oldUser.getUserName();
        String oldEmail = oldUser.getEmail();
        String oldPhone = oldUser.getPhone();
        String oldUserId = String.valueOf(oldUser.getId());

        try {
            // 如果用户名 <是邮箱, 手机号, 或者跟用户Id 相同>, 且有传入用户名, 且与原用户名不一致, 则更新
            if ((CheckUtils.checkEmail(oldUserName) || CheckUtils.checkPhone(oldUserName)
                    || oldUserName.equals(String.valueOf(oldUserId)))
                    && StringUtils.isNotBlank(userName) && !userName.equals(oldUserName)) {
                // 校验用户名
                checkUser.checkUserName(userName);
                oldUser.setUserName(userName);
                sessionUserInfo.setUserName(userName);
            }

            // 用户数据中的邮箱为空才能更新
            if (StringUtils.isBlank(oldEmail)) {
                // 校验用户邮箱
                checkUser.checkUserEmail(email);
                oldUser.setEmail(email);
                sessionUserInfo.setEmail(email);
            }

            // 有传入手机号码, 且与用户的原手机号码不一致, 则更新
            if (StringUtils.isNotBlank(phone) && !phone.equals(oldPhone)) {
                checkUser.checkPhone(phone);
                oldUser.setPhone(phone);
                sessionUserInfo.setPhone(phone);
            }

            // 用户Id 和用户名一致, 且有传入密码信息, 则更新
            if (oldUserName.equals(String.valueOf(oldUserId)) && StringUtils.isNotEmpty(password)) {
                checkUser.checkPasswordByStyle(password);
                oldUser.setPassword(BCryptUtil.encryptPassword(password));
            }

            // 用户名, 邮箱, 电话, 只要有一个有值时才需要更新用户数据
            if (StringUtils.isNotBlank(userName) || StringUtils.isNotBlank(email) || StringUtils.isNotBlank(phone)) {
                oldUser.setActive(true);
                userService.updateUser(oldUser);
                request.getSession().setAttribute(LoginInfo.USER_SESSION_KEY, sessionUserInfo);
            }

            userData.setBirthday(request.getParameter("birthday_year") + "-" + request.getParameter("birthday_month") + "-" + request.getParameter("birthday_day"));
            userData.setUserId(sessionUserInfo.getId());
            userService.updateUserData(userData);
            JsonResult result = new JsonResult(true, "账户设置成功");
            if (oldUserData.getCreateDate().compareTo(oldUserData.getUpdateDate()) == 0) {
                // 第一次完善资料送的积分数量
                long registerCurrencyCount = integralService.getRegisterCurrencyCount();
                userPointService.addUserCurrency(sessionUserInfo.getId(), registerCurrencyCount, "完善资料送积分");

                result.addData("firstUpdate", true).addData("registerCount",Currency.IntegralToCurrencyWithFormat(registerCurrencyCount,"#.##"));
            }
            result.toJson(response);
        } catch (CheckUserException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        } catch (Exception e) {
            logger.error("用户" + sessionUserInfo.getId() + "_" + sessionUserInfo.getUserName() + "后台账户设置出错", e);
            new JsonResult(false, "账户设置失败").toJson(response);
        }
    }

    /**
     * 账户设置
     */
    //@Token
    @RenderHeaderFooter
    @RequestMapping(value = "/my/toMyData")
    public String forwardUserData(HttpServletRequest request, Model model) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        forMyData(model, sessionUserInfo);

        model.addAttribute("contentVm", "myinfo/myData.vm");
        return "myinfo/myInfoLayout";
    }

    private void forMyData(Model model, SessionUserInfo sessionUserInfo) {
        model.addAttribute("site_title", "账户设置");

        UserData userData = userService.queryUserDataByUserId(sessionUserInfo.getId());
        model.addAttribute("myData", userData);
        if (null != userData && null != userData.getBirthday()) {
            String[] birthday = userData.getBirthday().split("-");
            if (birthday.length > 0) {
                model.addAttribute("birthday_year", birthday[0]);
            }
            if (birthday.length > 1) {
                model.addAttribute("birthday_month", birthday[1]);
            }
            if (birthday.length > 2) {
                model.addAttribute("birthday_day", birthday[2]);
            }
        }

        User oldUser = userService.getUserById(sessionUserInfo.getId());
        if (oldUser != null) {
            // 如果用户名 <是邮箱, 手机号, 或者跟用户Id 相同>, 则可以设置
            if (CheckUtils.checkEmail(oldUser.getUserName()) || CheckUtils.checkPhone(oldUser.getUserName())
                    || oldUser.getUserName().equals(String.valueOf(oldUser.getId()))) {
                model.addAttribute("changeName", true);
            }
            // 若邮箱为空, 则可以设置
            if (StringUtils.isBlank(oldUser.getEmail())) {
                model.addAttribute("changeEmail", true);
            }
            // 第三方过来的用户, 用户名跟用户id 一致, 则用户需要设置密码
            if (oldUser.getUserName().equals(String.valueOf(oldUser.getId()))) {
                model.addAttribute("setPwd", true);
            } else {
                model.addAttribute("accountUserName", oldUser.getUserName());
            }
            model.addAttribute("accountEmail", oldUser.getEmail());
            model.addAttribute("phoneNumber", oldUser.getPhone());
        }
    }

}
