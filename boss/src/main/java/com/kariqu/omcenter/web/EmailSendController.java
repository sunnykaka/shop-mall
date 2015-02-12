package com.kariqu.omcenter.web;

import com.kariqu.common.DateUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.usercenter.domain.Currency;
import com.kariqu.common.lib.Money;
import com.kariqu.tradecenter.service.CouponService;
import com.kariqu.usercenter.domain.MailHeader;
import com.kariqu.usercenter.domain.MessageTemplateName;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.service.MessageTaskService;
import com.kariqu.usercenter.service.UserPointService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Asion
 * Date: 13-4-24
 * Time: 上午9:34
 */
@Controller
public class EmailSendController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageTaskService messageTaskService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserPointService userPointService;

    @RequestMapping(value = "/email/new")
    public String newEmail() {
        return "emailForm";
    }

    @RequestMapping(value = "/email/send", method = RequestMethod.POST)
    public void sendEmail(String name, String email, String total, String[] shop, String[] product, String[] price, String[] number, String[] point, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (userService.getUserByUserName(email) != null) {
            new JsonResult(false, "邮件已经被使用").toJson(response);
            return;
        }


        if (userService.getUserByEmail(email) != null) {
            new JsonResult(false, "邮件已经被使用").toJson(response);
            return;
        }

        long totalPoint = Currency.CurrencyToIntegral(total);
        User user = new User();
        user.setPointTotal(totalPoint);
        user.setUserName(email);
        user.setEmail(email);
        String password = RandomStringUtils.randomNumeric(6);
        user.setPassword(BCryptUtil.encryptPassword(password));
        user.setRegisterDate(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
        user.setRegisterIP(request.getRemoteHost());
        user.setActive(true);
        userService.createUser(user);

        UserPoint userPoint = new UserPoint();
        userPoint.setUserId(user.getId());
        userPoint.setPoint(totalPoint);
        userPoint.setType(UserPoint.PointType.InComing);
        userPoint.setInOutComingType(UserPoint.InOutComingType.Order);
        userPoint.setDescription("系统直接赠送积分");
        userPointService.createUsePoint(userPoint);

        couponService.fetchCouponForUser(user.getId(), Money.YuanToCent("50"), 1);

        MailHeader header = new MailHeader();
        header.setMailFrom("noreply@yijushang.com");
        header.setMailSubject("我们给的不仅仅是优惠");
        header.setMailTo(email);

        Money totalMoney = new Money();
        for (int i = 0; i < price.length; i++) {
            totalMoney = totalMoney.add(new Money(price[i]).multiply(Long.parseLong(number[i])));
        }


        final Map mailParams = new HashMap();
        mailParams.put("name", name);
        mailParams.put("password", password);
        mailParams.put("email", email);
        mailParams.put("total", total);
        mailParams.put("shop", shop);
        mailParams.put("product", product);
        mailParams.put("price", price);
        mailParams.put("number", number);
        mailParams.put("point", point);
        mailParams.put("totalExpense", totalMoney.toString());

        header.setParams(mailParams);

        messageTaskService.sendHtmlMail(header, MessageTemplateName.SEND_POINT);

        new JsonResult(true).toJson(response);

    }


    @RequestMapping(value = "/email/preview")
    public String previewEmail(String name, String email, String total, String[] shop, String[] product, String[] price, String[] number, String[] point, Model model) {
        Money totalMoney = new Money();
        for (int i = 0; i < price.length; i++) {
            totalMoney = totalMoney.add(new Money(price[i]).multiply(Long.parseLong(number[i])));
        }
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("total", total);
        model.addAttribute("shop", shop);
        model.addAttribute("product", product);
        model.addAttribute("price", price);
        model.addAttribute("number", number);
        model.addAttribute("point", point);
        model.addAttribute("totalExpense", totalMoney.toString());

        return "messageTemplate/email";
    }


}
