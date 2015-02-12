package com.kariqu.buyer.web.controller.myinfo;

import com.google.common.collect.Lists;
import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserGradeHistory;
import com.kariqu.usercenter.domain.UserGradeRule;
import com.kariqu.usercenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * User: kyle
 * Date: 13-3-18
 * Time: 下午3:00
 */
@Controller
@PageTitle("我的会员等级")
public class MyMembershipController {


    @Autowired
    private UserService userService;


    @RenderHeaderFooter
    @RequestMapping(value = "/my/membership")
    public String toView(HttpServletRequest request, Model model) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        User user = userService.getUserById(sessionUserInfo.getId());
        List<UserGradeHistory> userGradeHistories = userService.getUserGradeHistory(sessionUserInfo.getId());
        List<UserGradeRule> userGradeRules = userService.queryAllGradeRule();
        UserGradeRule userGradeRule = userService.getGradeRule(user.getGrade());

        if (null != userGradeRule.getGrade().next()) {
            UserGradeRule nextUserGradeRule = userService.getGradeRule(userGradeRule.getGrade().next());
            Money money = Money.CentToYuan(nextUserGradeRule.getTotalExpense());
            money.subtractFrom(Money.CentToYuan(user.getExpenseTotal()));
            model.addAttribute("nextUserGradeRule", nextUserGradeRule);
            model.addAttribute("money", Money.getMoneyString(money.getCent()));

        }

        model.addAttribute("user", user);
        model.addAttribute("expenseTotal", Money.CentToYuan(user.getExpenseTotal()));

        model.addAttribute("userGradeRule", userGradeRule);

        model.addAttribute("userHistroyViews", this.getUserHistoryView(userGradeRules, userGradeHistories));
        model.addAttribute("userGradeRules", userGradeRules);

        model.addAttribute("contentVm", "myinfo/myMembership.vm");
        return "myinfo/myInfoLayout";
    }


    private List<UserHistoryView> getUserHistoryView(List<UserGradeRule> userGradeRules,
                                                     List<UserGradeHistory> userGradeHistories) {

        List<UserHistoryView> userHistoryViewArrayList = Lists.newArrayList();
        for(UserGradeHistory userGradeHistory : userGradeHistories){
            for(UserGradeRule userGradeRule : userGradeRules){
                if(userGradeHistory.getGrade().equals(userGradeRule.getGrade())){
                    UserHistoryView userHistoryView = new UserHistoryView();
                    userHistoryView.setName(userGradeRule.getName());
                    userHistoryView.setGradePic(userGradeRule.getGradePic());
                    userHistoryView.setDate(userGradeHistory.getCreateTime());
                    userHistoryViewArrayList.add(userHistoryView);
                    continue;
                }
            }
        }
        return userHistoryViewArrayList;
    }

    private Date getReachDate(UserGradeRule userGradeRule, List<UserGradeHistory> userGradeHistories) {

        for (UserGradeHistory userGradeHistory : userGradeHistories) {
            if (userGradeHistory.getGrade().equals(userGradeRule.getGrade())) {
                return userGradeHistory.getCreateTime();
            }
        }
        return null;
    }

    public class UserHistoryView {

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGradePic() {
            return gradePic;
        }

        public void setGradePic(String gradePic) {
            this.gradePic = gradePic;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public boolean isReach() {
            return reach;
        }

        public void setReach(boolean reach) {
            this.reach = reach;
        }

        private String name;
        private String gradePic;
        private Date date;
        private  boolean  reach;

    }


}
