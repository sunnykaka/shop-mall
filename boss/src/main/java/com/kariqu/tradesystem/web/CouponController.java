package com.kariqu.tradesystem.web;

import com.kariqu.common.CheckUtils;
import com.kariqu.common.DateUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.om.domain.Const;
import com.kariqu.om.service.ConstService;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.service.CouponService;
import com.kariqu.tradesystem.helper.CouponInfo;
import com.kariqu.usercenter.domain.MessageTemplateName;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.MessageTaskService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 13-1-14
 *        Time: 下午12:03
 */
@Controller
public class CouponController {

    private static final Log logger = LogFactory.getLog(CouponController.class);

    /**
     * 默认是提醒 5 天后到期的优惠券
     */
    private static final int REMIND_DELAY = 5;

    @Autowired
    private UserService userService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private MessageTaskService messageTaskService;

    @Autowired
    private ConstService constService;

    /**
     * 每天 15:15 执行一次即可.
     */ // 一分钟
    @Scheduled(cron = "0 15 15 * * ?") // fixedDelay = 1 * 60 * 1000
    public void execute() {
        if (logger.isWarnEnabled())
            logger.warn("========== 每天 15:15 向现金券即将过期的用户发送提醒短信开始 ==========");

        List<Coupon> couponList = couponService.getRemindCouponList(getCouponRemindDay());
        for (Coupon coupon : couponList) {
            try {
                User user = userService.getUserById(coupon.getUserId());
                if (user == null || (StringUtils.isBlank(user.getPhone()) && !CheckUtils.checkPhone(user.getUserName()))) {
                    logger.warn("优惠券(" + coupon.getCode() + ")对应的用户(" + coupon.getUserId() + ")找不到电话信息");
                    continue;
                }
                String phone = user.getPhone();
                if (StringUtils.isBlank(phone))
                    phone = user.getUserName();

                Map<String, String> map = new HashMap<String, String>();
                map.put("userName", user.getUserName());
                map.put("couponCode", coupon.getCode());
                map.put("couponPrice", coupon.getMoney());
                map.put("expireDate", DateUtils.formatDate(coupon.getExpireDate(), DateUtils.DateFormatType.SIMPLE_DATE_FORMAT_STR_DAY));

                messageTaskService.sendSmsMessage(phone, map, MessageTemplateName.EXPIRE_COUPON_REMIND);

                coupon.setMsgRemind(true);
                couponService.updateCoupon(coupon);
            } catch (Exception e) {
                logger.warn("优惠券(" + coupon.getCode() + ")发送提醒信息时异常: " + e.getMessage());
            }
        }
        if (logger.isWarnEnabled())
            logger.warn("========== 向现金券即将过期的用户发送提醒短信结束 ==========");
    }

    private int getCouponRemindDay() {
        Const constInfo = constService.getConstByKey("couponReMindDay");
        int remindDelay = NumberUtils.toInt(constInfo == null ? "" : constInfo.getConstValue(), REMIND_DELAY);
        if (remindDelay <= 0) remindDelay = REMIND_DELAY;
        return remindDelay;
    }

    /**
     * 未使用的现金券列表
     *
     * @return
     */
    @RequestMapping(value = "/coupon/notUsed/grid")
    public void notUsedCouponGrid(@RequestParam("start") int start, @RequestParam("limit") int limit, String code,
                                  String userName, HttpServletResponse response) throws IOException {
        User user = userService.getUserByNameOrEmailOrPhone(userName);
        int userId = (user == null) ? 0 : user.getId();

        Page<Coupon> couponPage = couponService.queryNotUsedCoupon(new Page<Coupon>(start / limit + 1, limit), code, userId);
        List<CouponInfo> couponInfoList = couponInfoList(couponPage.getResult());
        new JsonResult(true).addData("totalCount", couponPage.getTotalCount()).addData("result", couponInfoList).toJson(response);
    }

    /**
     * 已使用的现金券列表
     *
     * @return
     */
    @RequestMapping(value = "/coupon/used/grid")
    public void usedCouponGrid(@RequestParam("start") int start, @RequestParam("limit") int limit, String code,
                               String userName, HttpServletResponse response) throws IOException {
        User user = userService.getUserByNameOrEmailOrPhone(userName);
        int userId = (user == null) ? 0 : user.getId();

        Page<Coupon> couponPage = couponService.queryUsedCoupon(new Page<Coupon>(start / limit + 1, limit), code, userId);
        List<CouponInfo> couponInfoList = couponInfoList(couponPage.getResult());
        new JsonResult(true).addData("totalCount", couponPage.getTotalCount()).addData("result", couponInfoList).toJson(response);
    }

    /**
     * 已过期的现金券列表
     *
     * @return
     */
    @RequestMapping(value = "/coupon/expire/grid")
    public void expireCouponGrid(@RequestParam("start") int start, @RequestParam("limit") int limit, String code,
                                 String userName, HttpServletResponse response) throws IOException {
        User user = userService.getUserByNameOrEmailOrPhone(userName);
        int userId = (user == null) ? 0 : user.getId();

        Page<Coupon> couponPage = couponService.queryExpireCoupon(new Page<Coupon>(start / limit + 1, limit), code, userId);
        List<CouponInfo> couponInfoList = couponInfoList(couponPage.getResult());
        new JsonResult(true).addData("totalCount", couponPage.getTotalCount()).addData("result", couponInfoList).toJson(response);
    }

    /**
     * 可分配的现金券列表
     * 不包含联动优势
     *
     * @return
     */
    @RequestMapping(value = "/coupon/allocation/grid")
    public void allocationCouponGrid(@RequestParam("start") int start, @RequestParam("limit") int limit, String code,
                                     HttpServletResponse response) throws IOException {
        Page<Coupon> couponPage = couponService.queryAllocationCoupon(new Page<Coupon>(start / limit + 1, limit), code);
        List<CouponInfo> couponInfoList = couponInfoList(couponPage.getResult());
        new JsonResult(true).addData("totalCount", couponPage.getTotalCount()).addData("result", couponInfoList).addData("code", code).toJson(response);
    }

    /**
     * 可分配的联动优势现金券列表
     *
     * @return
     */
    @RequestMapping(value = "/coupon/umpay/grid")
    public void allUMPayCouponGrid(@RequestParam("start") int start, @RequestParam("limit") int limit, String code,
                                   HttpServletResponse response) throws IOException {
        Page<Coupon> couponPage = couponService.queryAllUMPayCoupon(new Page<Coupon>(start / limit + 1, limit), code);
        List<CouponInfo> couponInfoList = couponInfoList(couponPage.getResult());
        new JsonResult(true).addData("totalCount", couponPage.getTotalCount()).addData("result", couponInfoList).addData("code", code).toJson(response);
    }


    @RequestMapping(value = "/coupon/allocationUserName", method = RequestMethod.POST)
    public void createCoupon(int id, String userName, HttpServletResponse response) throws IOException {
        try {
            User user = userService.getUserByNameOrEmailOrPhone(userName);
            if (user == null) user = userService.getUserById(NumberUtils.toInt(userName));
            if (user == null) {
                new JsonResult(false, "该用户不存在").toJson(response);
                return;
            }
            Coupon coupon = couponService.getCouponById(id);
            coupon.setUserId(user.getId());
            couponService.updateCoupon(coupon);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("优惠劵分配错误：" + e);
            new JsonResult(false, "优惠劵分配失败").toJson(response);
        }
    }


    @RequestMapping(value = "/coupon/add", method = RequestMethod.POST)
    @Permission("创建了现金券")
    public void createCoupon(int number, Coupon.CouponType couponType, String price, String miniApplyOrderPrice,
                             String startDate, String expireDate, HttpServletResponse response) throws IOException {
        Date now = new Date(System.currentTimeMillis());
        Date beginDate = now;
        if (StringUtils.isNotBlank(startDate))
            beginDate = DateUtils.parseDate(startDate, com.kariqu.common.DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);

        Date EXPIRE_DATE = DateUtils.parseDate(expireDate, com.kariqu.common.DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA);
        if (now.getTime() >= EXPIRE_DATE.getTime()) {
            new JsonResult(false, "过期时间不能小于当前时间").toJson(response);
            return;
        }
        if (StringUtils.isNotBlank(startDate) && beginDate.getTime() >= EXPIRE_DATE.getTime()) {
            new JsonResult(false, "过期时间不能小于开始时间").toJson(response);
            return;
        }
        long p = NumberUtils.toLong(price);
        if (couponType == Coupon.CouponType.Ratio && (p >= 100 || p <= 0)) {
            new JsonResult(false, "100 块钱的东西, 你想让用户使用这张优惠券后花" + (p != 0 ? (100 * 100 / p) : 0) + "元买走么?").toJson(response);
            return;
        }

        long cent = (couponType == Coupon.CouponType.Normal) ? Money.YuanToCent(price) : p;
        long apply = Money.YuanToCent(miniApplyOrderPrice);
        if (apply <= cent) {
            logger.warn("现金券可能导致订单价格为0. 现金券价格: " + price + ", 订单的最小使用价格: " + miniApplyOrderPrice + ", 生成数量: " + number + ", 过期时间: " + expireDate + ".");
        }

        List<Coupon> coupons = couponService.generateCouponList(number, cent, apply, couponType, beginDate, EXPIRE_DATE);
        if (coupons.size() != number) {
            new JsonResult(true, "生成成功，但是可能失败了一些").toJson(response);
            return;
        }

        new JsonResult(true, "生成成功，数据库生成了" + coupons.size() + "张").toJson(response);
    }

    @RequestMapping(value = "/coupon/delete/batch", method = RequestMethod.POST)
    public void deleteRole(@RequestParam("ids") int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                Coupon coupon = couponService.getCouponById(id);
                if (coupon == null) {
                    new JsonResult(false, "没有此优惠劵(" + id + ")").toJson(response);
                    return;
                }
                if (coupon.isUsed() || coupon.getUserId() != 0) {
                    new JsonResult(false, "优惠劵(" + id + ", " + coupon.getCode() + ")已有拥有者或已被使用，不允许删除").toJson(response);
                    return;
                }
                if (!coupon.isExpire()) {
                    new JsonResult(false, "优惠劵(" + id + ", " + coupon.getCode() + ")还未到过期时间，不允许删除").toJson(response);
                    return;
                }
                couponService.deleteCoupon(id);
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("批量删除角色错误：" + e);
            new JsonResult(false, "批量删除失败").toJson(response);
        }
    }


    private List<CouponInfo> couponInfoList(List<Coupon> couponList) {
        List<CouponInfo> couponInfoList = new ArrayList<CouponInfo>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Coupon coupon : couponList) {
            CouponInfo couponInfo = new CouponInfo();
            couponInfo.setId(coupon.getId());
            couponInfo.setCode(coupon.getCode());
            couponInfo.setCouponType(coupon.getCouponType().name());
            couponInfo.setCreateDate(format.format(coupon.getCreateDate()));
            couponInfo.setStartDate(format.format(coupon.getStartDate()));
            couponInfo.setExpireDate(format.format(coupon.getExpireDate()));
            couponInfo.setUpdateDate(format.format(coupon.getUpdateDate()));
            couponInfo.setMiniApplyOrderPrice(coupon.getMiniApplyMoney());
            couponInfo.setOrderNo(coupon.getOrderNo());
            couponInfo.setPrice(coupon.getMoney());
            couponInfo.setPublish(coupon.isPublish());
            couponInfo.setUsed(coupon.isUsed());
            User user = userService.getUserById(coupon.getUserId());
            String userName = "";
            if (user != null) {
                userName = user.getUserName();
            }
            couponInfo.setUserName(userName);
            couponInfoList.add(couponInfo);
        }
        return couponInfoList;
    }

}
