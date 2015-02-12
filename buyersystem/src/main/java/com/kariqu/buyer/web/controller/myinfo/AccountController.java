package com.kariqu.buyer.web.controller.myinfo;

import com.kariqu.buyer.web.common.*;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.*;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.productcenter.domain.BrowsingHistory;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.ProductCollect;
import com.kariqu.productcenter.service.BrowsingHistoryService;
import com.kariqu.productcenter.service.ConsultationService;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.service.CouponService;
import com.kariqu.tradecenter.service.IntegralService;
import com.kariqu.tradecenter.service.ValuationService;
import com.kariqu.usercenter.domain.Currency;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserGrade;
import com.kariqu.usercenter.domain.UserGradeRule;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Athens(刘杰)
 * @Time 13-7-9 下午5:41
 */
@Controller
public class AccountController {

    private final Log logger = LogFactory.getLog(AccountController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ConsultationService consultationService;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @Autowired
    private TradeViewHelper tradeViewHelper;

    @Autowired
    private ValuationService valuationService;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private ProductService productService;

    @Autowired
    private BrowsingHistoryService browsHistoryService;

    @Autowired
    private CheckUser checkUser;

    @RequestMapping(value = "/my/account")
    @PageTitle("我的boobee")
    @RenderHeaderFooter
    public String account(HttpServletRequest request, Model model) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        int userId = sessionUserInfo.getId();
        User user = userService.getUserById(userId);
        UserGradeRule gradeRule = userService.getGradeRule(user.getGrade());
        UserGradeRule nextGradeRule = userService.getGradeRule(user.getGrade().next());

        // 用户当前等级称呼
        model.addAttribute("userGradeRuleName", gradeRule.getName());
        // 用户到达下一等需要的钱
        model.addAttribute("nextRuleWithMoney", Money.getMoneyString(nextGradeRule.getTotalExpense() - user.getExpenseTotal()));
        // 用户下一等级的称呼
        model.addAttribute("nextGradeRuleName", nextGradeRule.getName());
        // 用户已的咨询回复数量
        model.addAttribute("consultCount", consultationService.queryConsultationCountByUserId(userId, true));
        // 用户积分
        model.addAttribute("currency", user.getCurrency());
        // 待付款
        model.addAttribute("waitPayOrderCount", tradeCenterUserClient.queryWaitPayOrderCountByUserId(userId));

        Map<String, Object> valuationData = tradeCenterUserClient.queryValuationCountByUserIdAndAppraise(userId, 0);
        // 待评价数量
        model.addAttribute("waitValuationCount", valuationData.get("count"));
        // 评价对应的积分数
        double integralSum = 0;
        if (valuationData.get("sum") != null)
            integralSum = NumberUtils.toLong(valuationData.get("sum").toString()) * gradeRule.getValuationRatio() / 100;
        model.addAttribute("valuationForIntegralSum", Currency.IntegralToCurrency((long) integralSum));

        Map<String, Object> waitConfirmOrder = tradeCenterUserClient.queryOrderCountByUserIdAndOrderState(userId, OrderState.Send);
        // 待确认收货数量
        model.addAttribute("waitConfirmOrderCount", waitConfirmOrder.get("count"));
        // 确认收货对应的积分数
        double confirmOrderForIntegralSum = 0;
        if (waitConfirmOrder.get("sum") != null)
            confirmOrderForIntegralSum = NumberUtils.toDouble(waitConfirmOrder.get("sum").toString()) * integralService.getTradeIntegralPercent();
        model.addAttribute("waitConfirmOrderForIntegralSum", Currency.IntegralToCurrency((long) confirmOrderForIntegralSum));
        // 是否有订单信息
        model.addAttribute("recentOrder", tradeCenterUserClient.queryOrderCountByUserIdAndState(userId, 0) > 0);
        // 是否有待评价的商品, 通过待评价数量判断.

        // 用户未使用的现金券
        Page<Coupon> couponPage = couponService.queryNotUsedCouponByUserId(userId, new Page<Coupon>(1, 5));
        model.addAttribute("coupons", couponPage.getResult());

        int couponNum = couponService.queryNotUsedCouponCountByUserId(userId);
        // 用户未使用的现金券数量
        model.addAttribute("couponCount", couponNum);

        // 一元对应的积分数
        model.addAttribute("integralCount", integralService.integralCount());

        // 获取用户的收藏信息
        favoritesByAccountPage(userId, model);
        // 获取用户的关注信息
        historyList(userId, model);

        model.addAttribute("contentVm", "myinfo/account.vm");
        return "myinfo/myInfoLayout";
    }

    /**
     * 获取最近的五条订单信息
     */
    @RequestMapping(value = "/my/recentOrder")
    public String recentOrder(HttpServletRequest request, Model model) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        int pageNo = 1;
        Page<Order> orderPage = tradeCenterUserClient.getNotCancelOrderPageByUserId(sessionUserInfo.getId(), new Page<Order>(pageNo, 5));
        Page<OrderView> orderViewPage = tradeViewHelper.OrderView(orderPage);

        model.addAttribute("orderPage", orderViewPage);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("state", 0);

        return "myinfo/recentOrder";
    }

    /**
     * 待评价的订单.
     */
    @RequestMapping(value = "/my/waitValuation")
    public String waitValuation(HttpServletRequest request, Model model) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        Page<OrderItem> orderItemPage = tradeCenterUserClient.queryValuationByUserIdAndAppraise(sessionUserInfo.getId(), 0, new Page<OrderItem>(1, 5));

        List<ValuationView> valuationViewList = new ArrayList<ValuationView>(orderItemPage.getResult().size());
        for (OrderItem orderItem : orderItemPage.getResult()) {
            ValuationView view = new ValuationView();
            view.setProductId(orderItem.getProductId());
            view.setSkuId(orderItem.getSkuId());
            view.setOrderItemId(orderItem.getId());
            view.setSkuName(orderItem.getSkuName());
            view.setSkuExplain(orderItem.getSkuExplain());
            view.setSkuMainPicture(orderItem.getSkuMainPicture());
            view.setCreateDate(orderItem.getCreateDate());
            // 商品评价数
            view.setValuationCount(valuationService.queryValuationCountByProductId(orderItem.getProductId()));

            valuationViewList.add(view);
        }
        model.addAttribute("valuationList", valuationViewList);

        return "myinfo/waitValuation";
    }

    @RequestMapping(value = "/my/gradeRule")
    public void gradeRule(HttpServletRequest request, HttpServletResponse response, String rule) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        User user = userService.getUserById(sessionUserInfo.getId());

        UserGrade userGrade = user.getGrade();
        if (StringUtils.isNotBlank(rule) && "n".equals(rule))
            userGrade = user.getGrade().next();

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(userService.getGradeRule(userGrade).getGradeDescription());
    }

    @RequestMapping(value = "/order/info/{orderId}")
    public void orderInfo(@PathVariable("orderId") long orderId, String top, HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        int number = NumberUtils.toInt(top, 0);

        List<ProgressDetail> progressDetailList = tradeCenterUserClient.getProgressDetail(sessionUserInfo.getId(), orderId, number);
        if (progressDetailList != null && progressDetailList.size() > 0)
            new JsonResult(true).addData("result", progressDetailList).toJson(response);
        else
            new JsonResult(false, "无订单信息").toJson(response);
    }

    @RequestMapping(value = "/my/changePhone", method = RequestMethod.POST)
    public void changePhone(String phone, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        User oldUser = userService.getUserById(sessionUserInfo.getId());

        try {
            if (StringUtils.isBlank(phone)) {
                throw new CheckUserException("手机号不能为空");
            }

            if (!phone.equals(oldUser.getPhone())) {
                checkUser.checkPhone(phone);
                oldUser.setPhone(phone);
                sessionUserInfo.setPhone(phone);
                userService.updateUser(oldUser);
            }

            new JsonResult(true, "手机号码修改成功").toJson(response);
        } catch (CheckUserException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        } catch (Exception e) {
            logger.error("用户" + sessionUserInfo.getId() + "_" + sessionUserInfo.getUserName() + "后台账户手机设置出错", e);
            new JsonResult(false, "账户手机设置失败").toJson(response);
        }
    }

    @RequestMapping(value = "/my/putEmail", method = RequestMethod.POST)
    public void putEmail(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        User oldUser = userService.getUserById(sessionUserInfo.getId());

        try {
            // 用户数据中的邮箱为空才能更新
            if (StringUtils.isBlank(oldUser.getEmail())) {
                // 校验用户邮箱
                checkUser.checkUserEmail(email);
                oldUser.setEmail(email);
                sessionUserInfo.setEmail(email);
                userService.updateUser(oldUser);
            }

            new JsonResult(true, "邮箱设置成功").toJson(response);
        } catch (CheckUserException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        } catch (Exception e) {
            logger.error("用户" + sessionUserInfo.getId() + "_" + sessionUserInfo.getUserName() + "后台账户邮箱设置出错", e);
            new JsonResult(false, "账户邮箱设置失败").toJson(response);
        }
    }

    /**
     * 账户页面中我的收藏
     *
     * @param userId
     * @param model
     * @return
     */
    private void favoritesByAccountPage(int userId, Model model) {
        int pageNo = 1;
        Page<ProductCollect> productCollectPage = productService.queryProductCollectByPage(pageNo, 10, userId);

        if (productCollectPage.getResult().size() > 0) {
            model.addAttribute("consultationPageBar", PageProcessor.process(productCollectPage));
            model.addAttribute("productCollectPage", productCollectPage);
        }
    }

    /**
     * 账户页面中的浏览记录
     *
     * @param userId
     * @param model
     */
    private void historyList(int userId, Model model) {
        List<BrowsingHistory> historyList = new LinkedList<BrowsingHistory>();

        historyList = browsHistoryService.queryBrowsHistoryByUserIdWithOutProductId(userId, 0, 10);

        List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
        for (BrowsingHistory history : historyList) {
            Map<String, Object> productMap = productService.getProductMap(history.getProductId());
            if (productMap != null) {
                productMap.put("id", history.getId());
                list.add(productMap);
            }
        }

        model.addAttribute("list", list);
    }

}
