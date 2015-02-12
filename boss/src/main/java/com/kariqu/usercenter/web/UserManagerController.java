package com.kariqu.usercenter.web;

import com.kariqu.common.CheckUtils;
import com.kariqu.common.DateUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.usercenter.domain.Currency;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.lib.Money;
import com.kariqu.tradecenter.client.TradeCenterBossClient;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.service.CouponService;
import com.kariqu.usercenter.UserGradeRuleView;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserGrade;
import com.kariqu.usercenter.domain.UserGradeRule;
import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.helper.UserVo;
import com.kariqu.usercenter.service.UserPointService;
import com.kariqu.usercenter.service.UserQuery;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-9-5
 * Time: 上午10:56
 */
@Controller
public class UserManagerController {

    private final Log logger = LogFactory.getLog(UserManagerController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private TradeCenterBossClient tradeCenterBossClient;

    /**
     * 读取用户列表
     *
     * @return
     */
    @RequestMapping(value = "/user/list")
    public void userGrid(UserQuery userQuery, HttpServletResponse response) throws IOException {
        Page<User> page = userService.queryUserByPage(userQuery);
        List<UserVo> userVoList = new ArrayList<UserVo>();
        for (User dbUser : page.getResult()) {
            UserVo userVo = new UserVo();
            userVo.setId(dbUser.getId());
            userVo.setUserName(dbUser.getUserName());
            userVo.setActive(dbUser.isActive());
            userVo.setEmail(dbUser.getEmail());
            userVo.setHasForbidden(dbUser.isHasForbidden());
            userVo.setLoginCount(dbUser.getLoginCount());
            userVo.setRegisterDate(dbUser.getRegisterDate());
            userVo.setPointTotal(dbUser.getCurrency());
            userVo.setPhone(dbUser.getPhone());
            UserGradeRule gradeRule = userService.getGradeRule(dbUser.getGrade());
            if (gradeRule != null) {
                userVo.setGrade(gradeRule.getName());
            } else {
                userVo.setGrade(dbUser.getGrade().toString());
            }
            if (dbUser.getLoginTime() == null) {
                userVo.setLastLoginTime("无");
            } else {
                userVo.setLastLoginTime(DateUtils.formatDate(dbUser.getLoginTime(), DateUtils.DateFormatType.DATE_FORMAT_STR));
            }
            userVoList.add(userVo);
        }
        new JsonResult(true).addData("totalCount", page.getTotalCount()).addData("result", userVoList).toJson(response);
    }

    @Permission("删除用户")
    @RequestMapping(value = "/user/delete", method = RequestMethod.POST)
    public void deleteUserById(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                userService.deleteUser(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除用户信息错误：" + e);
            new JsonResult(false, "删除用户失败").toJson(response);
        }
    }

    /**
     * 根据用户Id修改用户禁用状态
     *
     * @param id
     * @param hasForbidden
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/user/updateStatus", method = RequestMethod.POST)
    public void updateUserForbiddenStatus(@RequestParam("id") int id, @RequestParam("hasForbidden") boolean hasForbidden, HttpServletResponse response) throws IOException {
        try {
            userService.updateUserForbiddenStatus(id, hasForbidden);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("修改用户禁用状态错误：" + e);
            new JsonResult(false, "修改用户禁用状态失败").toJson(response);
        }
    }

    /**
     * 激活用户
     *
     * @param id
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/user/activeUser", method = RequestMethod.POST)
    public void activeUserUser(@RequestParam("id") int id, HttpServletResponse response) throws IOException {
        try {
            userService.updateUserIsActive(id, true);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("修改用户禁用状态错误：" + e);
            new JsonResult(false, "修改用户禁用状态失败").toJson(response);
        }
    }


    /**
     * 手动修改用户积分
     *
     * @param id
     * @param point
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/user/changePoint", method = RequestMethod.POST)
    @Permission("手动修改用户积分")
    public void changePoint(@RequestParam("id") int id, String point, HttpServletResponse response) throws IOException {
        try {
            long integral = Currency.CurrencyToIntegral(point);
            UserPoint userPoint = new UserPoint();
            userPoint.setUserId(id);
            userPoint.setPoint(Math.abs(integral));
            userPoint.setType(integral > 0 ? UserPoint.PointType.InComing : UserPoint.PointType.OutComing);
            userPoint.setInOutComingType(UserPoint.InOutComingType.Order);
            userPoint.setDescription(integral > 0 ? "系统直接赠送积分" : "系统直接扣减积分");
            userPointService.createUsePoint(userPoint);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("修改用户禁用状态错误：" + e);
            new JsonResult(false, "修改用户禁用状态失败").toJson(response);
        }
    }

    /**
     * 添加用户
     *
     * @param user
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public void createUser(User user, HttpServletResponse response) throws IOException {
        try {
            if (StringUtils.isBlank(user.getUserName()) || user.getUserName().length() < 6) {
                new JsonResult(false, "请保证用户名大于 6 位, 后 6 位是密码!").toJson(response);
                return;
            }
            if (userService.getUserByUserName(user.getUserName()) != null) {
                new JsonResult(false, "用户已经存在").toJson(response);
                return;
            }

            if (CheckUtils.checkPhone(user.getUserName()))
                user.setPhone(user.getUserName());
            else if (CheckUtils.checkEmail(user.getUserName()))
                user.setEmail(user.getUserName());

            user.setPassword(BCryptUtil.encryptPassword(user.getUserName().substring(user.getUserName().length() - 6)));
            user.setActive(true);
            user.setRegisterDate(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
            user.setRegisterIP(InetAddress.getLocalHost().getHostAddress());
            userService.createUser(user);
        } catch (Exception e) {
            logger.error("添加用户出现异常：" + e);
            new JsonResult(false, "添加用户出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    @RequestMapping(value = "/user/getGradeRule")
    public void getUserGrade(UserGrade grade, HttpServletResponse response) throws IOException {
        UserGradeRule gradeRule = userService.getGradeRule(grade);
        if (gradeRule == null) {
            gradeRule = new UserGradeRule();
            gradeRule.setGrade(grade);
            gradeRule.setName("没设置");
            new JsonResult(true).addData("rule", gradeRule).toJson(response);
        } else {
            UserGradeRuleView view = new UserGradeRuleView();
            view.setGrade(gradeRule.getGrade());
            view.setGradeDescription(gradeRule.getGradeDescription());
            view.setGradePic(gradeRule.getGradePic());
            view.setName(gradeRule.getName());
            view.setValuationRatio(gradeRule.getValuationRatio());
            view.setOnceExpense(Money.getMoneyString(gradeRule.getOnceExpense()));
            view.setTotalExpense(Money.getMoneyString(gradeRule.getTotalExpense()));
            new JsonResult(true).addData("rule", view).toJson(response);
        }
    }


    /**
     * 等级设置
     *
     * @param view
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/user/changeGradeRule", method = RequestMethod.POST)
    @Permission("改变等级设置")
    public void changeGradeRule(UserGradeRuleView view, HttpServletResponse response) throws IOException {
        try {
            if (view.getGrade().pre() != null) {
                UserGradeRule gradeRule = userService.getGradeRule(view.getGrade().pre());
                if (gradeRule == null) {
                    new JsonResult(false, "请先设置低级别").toJson(response);
                    return;
                }
                if (Money.YuanToCent(view.getOnceExpense()) <= gradeRule.getOnceExpense() || Money.YuanToCent(view.getTotalExpense()) <= gradeRule.getTotalExpense()) {
                    new JsonResult(false, "高级别设置不能低于低级别").toJson(response);
                    return;
                }
            }

            UserGradeRule rule = new UserGradeRule();
            rule.setName(view.getName());
            rule.setGrade(view.getGrade());
            rule.setValuationRatio(view.getValuationRatio());
            rule.setGradePic(view.getGradePic());
            rule.setGradeDescription(view.getGradeDescription());
            rule.setOnceExpense(Money.YuanToCent(view.getOnceExpense()));
            rule.setTotalExpense(Money.YuanToCent(view.getTotalExpense()));

            userService.changeGradeRule(rule);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("修改等级设置错误：" + e);
            new JsonResult(false, "修改等级设置发生错误").toJson(response);
        }
    }


    /**
     * 后台给用户赠送现金券
     *
     * @param userId
     * @param money
     * @param number
     */
    @RequestMapping(value = "/user/assignCoupon", method = RequestMethod.POST)
    public void assignCouponForUser(int userId, String money, int number, HttpServletResponse response) throws IOException {
        List<Coupon> coupons = couponService.fetchCouponForUser(userId, Money.YuanToCent(money), number);
        if (coupons.size() == 0) {
            new JsonResult(false, "系统中不存在符合条件的现金券").toJson(response);
        } else {
            new JsonResult(true).addData("number", coupons.size()).toJson(response);
        }
    }

    /**
     * 批量给用户添加积分, 赠送现金券
     */
    @RequestMapping(value = "/user/assignIntegralAndCoupon", method = RequestMethod.POST)
    public void assignIntegralAndCouponForUser(String userInfo, HttpServletResponse response) throws IOException {
        String userName;
        int index = 0, nullCount = 0, count = 0, successCount = 0;
        String[] info;
        User user;
        Long integral;
        List<Integer> noPatternList = new ArrayList<Integer>();
        List<Integer> noUserList = new ArrayList<Integer>();

        String[] userIntegralCoupon = userInfo.split(System.getProperty("line.separator"));
        if (userIntegralCoupon.length < 2) userIntegralCoupon = userInfo.split("\n");
        for (String str : userIntegralCoupon) {
            index++;
            if (StringUtils.isBlank(str)) {
                nullCount++;
                continue;
            }

            // 空行忽略后, 总行数增加
            count++;

            // 先使用 | 拆分, 再使用制表符拆分, 若都没有, 则忽略当前行
            info = str.split("\\|");
            if (info.length < 2) info = str.split("\t");
            if (info.length < 2) {
                noPatternList.add(index);
                continue;
            }

            userName = info[0];
            user = userService.getUserByNameOrEmailOrPhone(userName);
            if (user == null) {
                noUserList.add(index);
                continue;
            }

            try {
                integral = Currency.CurrencyToIntegral(info[1]);
            } catch (Exception e) {
                noPatternList.add(index);
                continue;
            }

            tradeCenterBossClient.assignIntegralAndCoupon(user, integral, (info.length < 3) ? "" : info[2]);
            successCount++;
        }
        StringBuilder sbd = new StringBuilder();
        sbd.append("共有 ").append(count).append(" 行数据,");
        if (nullCount > 0) sbd.append(nullCount).append(" 条空行,");
        sbd.append("成功 ").append(successCount).append(" 条.");

        if (count != successCount) sbd.append(" 其中,");
        if (noPatternList.size() > 0)
            sbd.append(" 第 ").append(noPatternList).append(" 行格式不正确.");
        if (noUserList.size() > 0)
            sbd.append(" 无第 ").append(noUserList).append(" 行的用户信息.");

        new JsonResult(true, sbd.toString()).toJson(response);
    }

}
