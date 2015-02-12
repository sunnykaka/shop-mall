package com.kariqu.buyer.web.controller.trade;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.tradecenter.domain.CanntJoinActivityResult;
import com.kariqu.tradecenter.service.IntegralActivityService;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跟积分活动相关, 如积分兑换商品,优惠购等
 * Created by Canal.wen on 2014/7/24 15:02.
 */
@Controller
public class KaMoneyActivityController {
    @Autowired
    private UserService userService;

    @Autowired
    private IntegralActivityService integralActivityService;

    @RequestMapping("/activity/checkUserCanJoin/{activityType}/{activityId}")
    public void checkUserCanJoin(HttpServletRequest request, HttpServletResponse response,
                                 @PathVariable("activityType") String activityType,
                                 @PathVariable("activityId") Integer activityId ) throws IOException {
        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        if (userInfo == null) {
            new JsonResult(false, "用户没有登陆").toJson(response);
            return;
        }

        User user = userService.getUserById(userInfo.getId());
        if (user == null) {
            new JsonResult(false, "没有找到用户信息").toJson(response);
            return;
        }

        JsonResult jsonResult = new JsonResult();
        CanntJoinActivityResult reason = integralActivityService.checkUserCanJoin(user, activityType, activityId);
        if (reason == CanntJoinActivityResult.OK) {
            jsonResult.setSuccess(true).setMsg("可以参加活动");
        } else {
            jsonResult.setSuccess(false).setMsg("不能参加活动").addData("reasonType", reason.toString());
        }
        jsonResult.addData("totalPoint", user.getCurrency()).addData("userName", user.getUserName());
        jsonResult.toJson(response);
    }

}
