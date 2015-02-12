package com.kariqu.buyer.web.controller.trade;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.DateUtils;
import com.kariqu.om.domain.Const;
import com.kariqu.om.service.ConstService;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.excepiton.TradeFailException;
import com.kariqu.tradecenter.service.IntegralService;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.UserPointService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

/** 签到, 兑换 */
@Controller
@RequestMapping(value = "/user")
public class SignInController {

    private static final Log LOG = LogFactory.getLog(SignInController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private ConstService constService;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @Autowired
    private IntegralService integralService;

    /**
     * 获取用户的积分总数
     */
    @RequestMapping("/totalPoint")
    public void totalPoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        if (userInfo != null) {
            User user = userService.getUserById(userInfo.getId());
            if (user != null) {
                new JsonResult(true)
                        .addData("totalPoint", user.getCurrency())
                        .addData("userName", user.getUserName())
                        .toJson(response);
            } else {
                new JsonResult(false, "没有找到用户信息").toJson(response);
            }
        } else {
            new JsonResult(false, "用户没有登陆").toJson(response);
        }
    }

    /***
     * 返回json的key值有:
     * signInCount  签到次数            int
     * point        本次签到得到的积分数   double
     * totalPoint   用户总的积分数        double
     * totalChangeMoney  积分可换成的元  double
     * tomorrowPoint 明天得到的积分数      double
     * activityDays   活动连续天数    int
     * activityTotalPoint 连续签到可领取的总积分数  double
     */
    @RequestMapping(value = "/sign")
    public void signIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        if (userInfo == null) {
            new JsonResult(false, "用户未登陆").toJson(response);
            return;
        }
        try {
            Const constInfo = constService.getConstByKey("signInRule");
            double rate = integralService.integralCount();//积分到人民币的兑换率
            Map<String, Number> map = userPointService.signIn(userInfo.getId(), constInfo == null ? "" : constInfo.getConstValue(), rate);
            JsonResult json = new JsonResult(true);
            json.addData("signInCount", map.get("signInCount"))
                    .addData("point", formatNumber(map.get("point"), "0.00"))
                    .addData("totalPoint", formatNumber(map.get("totalPoint"), "0.00"))
                    .addData("totalChangeMoney", formatNumber(map.get("totalChangeMoney"), "0.00"))
                    .addData("tomorrowPoint", formatNumber(map.get("tomorrowPoint"), "0.00"))
                    .addData("activityDays", map.get("activityDays"))
                    .addData("activityTotalPoint", formatNumber(map.get("activityTotalPoint"), "0.00"))
                .toJson(response);
        } catch (RuntimeException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    private String formatNumber(Number d, String format) {
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return d == null ? "" : decimalFormat.format(d);
    }

    @RequestMapping(value = "/exchange/coupon")
    public void exchangeCoupon(String count, HttpServletRequest request, HttpServletResponse response) throws IOException {
        int num = NumberUtils.toInt(count);
        if (num != 20 && num != 50 && num != 100) {
            new JsonResult(false, "无此积分兑换").toJson(response);
            return;
        }

        Date now = new Date();
        if (now.before(DateUtils.parseDate("2014-11-01", DateUtils.DateFormatType.SIMPLE_DATE_FORMAT_STR))
                || now.after(DateUtils.parseDate("2014-11-10 23:59:59", DateUtils.DateFormatType.DATE_FORMAT_STR))) {
            new JsonResult(false, "积分兑换时间只在 11月1号 到 11月10号 之间").toJson(response);
            return;
        }

        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        if (userInfo == null) {
            new JsonResult(false, "用户未登陆").toJson(response);
            return;
        }

        try {
            tradeCenterUserClient.exchangeCoupon(userInfo.getId(), num);
            new JsonResult(true, "积分兑换 " + count + " 元现金券成功").toJson(response);
        } catch (TradeFailException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/receive")
    public void receiveCoupon(String count, HttpServletRequest request, HttpServletResponse response) throws IOException {
        int num = NumberUtils.toInt(count);
        if (num != 10 && num != 20 && num != 50) {
            new JsonResult(false, "没有这个优惠券领取方式").toJson(response);
            return;
        }

        Date now = new Date();
        if (//now.before(DateUtils.parseDate("2014-11-01", DateUtils.DateFormatType.SIMPLE_DATE_FORMAT_STR)) ||
                now.after(DateUtils.parseDate("2014-11-11 23:59:59", DateUtils.DateFormatType.DATE_FORMAT_STR))) {
            new JsonResult(false, "领取优惠券时间只能在到 11月11号 之前").toJson(response);
            return;
        }

        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        if (userInfo == null) {
            new JsonResult(false, "用户未登陆").toJson(response);
            return;
        }

        User user = userService.getUserById(userInfo.getId());
        if (user == null) {
            new JsonResult(false, "无此用户信息").toJson(response);
            return;
        }

        try {
            tradeCenterUserClient.receiveCoupon(userInfo.getId(), num);
            new JsonResult(true, "领取 " + count + " 元优惠券成功").toJson(response);
        } catch (TradeFailException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

}
