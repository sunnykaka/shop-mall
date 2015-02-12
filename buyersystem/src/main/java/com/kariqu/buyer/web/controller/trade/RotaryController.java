package com.kariqu.buyer.web.controller.trade;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.excepiton.TradeFailException;
import com.kariqu.usercenter.domain.User;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** 抽奖 */
@Controller
@RequestMapping(value = "/rotary")
public class RotaryController {

    private static final Log logger = LogFactory.getLog(RotaryController.class);

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{rotaryId}")
    @RenderHeaderFooter
    public String rotaryList(@PathVariable("rotaryId") String rotaryId, Model model,
                             HttpServletRequest request) throws IOException {
        int id = NumberUtils.toInt(rotaryId);
        if (id <= 0) {
            model.addAttribute("site_title", "操作有误！");
            model.addAttribute("msg", "没有此抽奖活动");
            return "error";
        }

        try {
            String loginUserName = "未登陆用户";
            String pointTotal = "0.00";
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            if (sessionUserInfo != null) {
                loginUserName = sessionUserInfo.getUserName();
                int userId = sessionUserInfo.getId();
                User user = userService.getUserById(userId);
                pointTotal = (user == null) ? "0.00" : user.getCurrency();
            }
            model.addAttribute("site_title", "小积分 抽大奖 玩转积分");
            model.addAttribute("loginUserName", loginUserName);
            model.addAttribute("pointTotal", pointTotal);

            model.addAttribute("rotary", tradeCenterUserClient.getRotaryById(id));
            model.addAttribute("lotteryList", tradeCenterUserClient.getAllLotteryByRotaryId(id));
            return "rotary/lottery";
        } catch (TradeFailException e) {
            model.addAttribute("site_title", "操作有误！");
            model.addAttribute("msg", e.getMessage());
            return "error";
        }
    }

    @RequestMapping(value = "/talent")
    public void talent(String rotaryId, HttpServletResponse response) throws IOException {
        int id = NumberUtils.toInt(rotaryId);
        if (id <= 0) {
            new JsonResult(false, "没有此抽奖活动").toJson(response);
            return;
        }

        new JsonResult(true).addData("lotteryList", tradeCenterUserClient.getAllLotteryByRotaryId(id)).toJson(response);
    }

    @RequestMapping(value = "/lottery")
    public void lottery(String rotaryId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        if (userInfo == null) {
            new JsonResult(false, "用户未登陆").toJson(response);
            return;
        }
        int id = NumberUtils.toInt(rotaryId);
        if (id <= 0) {
            new JsonResult(false, "没有此抽奖活动").toJson(response);
            return;
        }

        try {
            new JsonResult(true).addData("meed", tradeCenterUserClient.lottery(id, userInfo.getId(), userInfo.getUserName())).toJson(response);
        } catch (TradeFailException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        } catch (Exception e) {
            logger.error("用户(" + userInfo.getUserName() + ")抽奖时异常: ", e);
            new JsonResult(false, "抽奖错误, 请您联系客服人员进行处理!").toJson(response);
        }
    }

    @RequestMapping(value = "/lottery/send", method = RequestMethod.POST)
    public void lotterySend(Integer lotteryId, String consigneeName, String consigneePhone,
                            String consigneeAddress, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        if (userInfo == null) {
            new JsonResult(false, "用户未登陆").toJson(response);
            return;
        }
        if (StringUtils.isBlank(consigneeName) || StringUtils.isBlank(consigneePhone) || StringUtils.isBlank(consigneeAddress)) {
            new JsonResult(false, "请务必填写收货信息").toJson(response);
            return;
        }

        try {
            tradeCenterUserClient.updateSendLottery(userInfo.getUserName(), lotteryId, consigneeName, consigneePhone, consigneeAddress);
            new JsonResult(true).toJson(response);
        } catch (TradeFailException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        } catch (Exception e) {
            logger.error("用户(" + userInfo.getUserName() + ")抽奖时异常: ", e);
            new JsonResult(false, "抽奖错误, 请您联系客服人员进行处理!").toJson(response);
        }
    }

}
