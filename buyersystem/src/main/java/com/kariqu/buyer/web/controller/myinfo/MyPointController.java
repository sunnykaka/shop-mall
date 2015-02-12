package com.kariqu.buyer.web.controller.myinfo;

import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.service.UserPointQuery;
import com.kariqu.usercenter.service.UserPointService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * User: kyle
 * Date: 13-3-18
 * Time: 上午11:41
 */
@Controller
@PageTitle("我的积分")
public class MyPointController {

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private UserService userService;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;


    @RequestMapping(value = "/my/point/list",method = RequestMethod.GET)
    @RenderHeaderFooter
    public String queryPoint(@RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
            String pointType, String pageSize, Model model, HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        User user = userService.getUserById(sessionUserInfo.getId());
        int size = NumberUtils.toInt(pageSize, 5);
        if (size <= 0 || size > 50) size = 5;
        model.addAttribute("currency", user.getCurrency());
        model.addAttribute("pointTotalAsMoney", tradeCenterUserClient.calculateIntegralWithMoney(user.getPointTotal()));
        //构建UserPointQuery point查询参数
        UserPointQuery userPointQuery = new BuildUserPointQuery(pageNo, pointType, model, sessionUserInfo, size).invoke();
        Page<UserPoint> pointPage = userPointService.queryUserPoint(userPointQuery);
        model.addAttribute("pointPage", pointPage);
        model.addAttribute("pointPageBar", PageProcessor.process(pointPage));

        model.addAttribute("contentVm", "myinfo/myPoint.vm");
        return "myinfo/myInfoLayout";
    }


    private class BuildUserPointQuery {
        private int pageNo;
        private String pointType;
        private Model model;
        private SessionUserInfo sessionUserInfo;
        private int size;

        public BuildUserPointQuery(int pageNo, String pointType, Model model, SessionUserInfo sessionUserInfo, int size) {
            this.pageNo = pageNo;
            this.pointType = pointType;
            this.model = model;
            this.sessionUserInfo = sessionUserInfo;
            this.size = size;
        }

        public UserPointQuery invoke() {
            UserPointQuery userPointQuery = UserPointQuery.where(sessionUserInfo.getId(), size);
            UserPoint.PointType up;
            try{
                up = Enum.valueOf(UserPoint.PointType.class, pointType);
                userPointQuery.and(up);
                model.addAttribute("currentPointType", up.name());
            }   catch (Exception e){
                // 如果传入的积分状态为空或为非法字符，则为空
                model.addAttribute("currentPointType", "");
            }
            userPointQuery.setPageNo(pageNo);
            return userPointQuery;
        }
    }
}
