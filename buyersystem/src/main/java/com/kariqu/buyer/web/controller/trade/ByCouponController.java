package com.kariqu.buyer.web.controller.trade;

import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.PayBank;
import com.kariqu.tradecenter.payment.PayInfoWrapper;
import com.kariqu.tradecenter.payment.PayRequestHandler;
import com.kariqu.tradecenter.payment.PaymentManager;
import com.kariqu.tradecenter.service.CouponService;
import com.kariqu.tradecenter.service.impl.PayMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 用户购买现金券
 * User: Alec
 * Date: 13-11-1
 * Time: 上午9:52
 */
@Controller
public class ByCouponController {
    private final Log logger = LogFactory.getLog(ByCouponController.class);
    @Autowired
    private CouponService couponService;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * 确定付款
     *
     * @param model
     * @return
     */
    @RenderHeaderFooter
    @RequestMapping("/couponSet/{type}")
    public String couponSet(@PathVariable("type") CouponSetType type, Model model, HttpServletRequest request) throws UnsupportedEncodingException {
        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        if (null == userInfo) {
            return "redirect:" + urlBrokerFactory.getUrl("ToPageLogin").addQueryData("backUrl", URLEncoder.encode(urlBrokerFactory.getUrl("CouponSet").addQueryData("type", type).toString(), "UTF-8")).toString();
        }
        model.addAttribute("setType", type);
        model.addAttribute("totalPrice", Money.CentToYuan(type.price()));
        model.addAttribute("couponSetDesc", type.toDesc());
        return "onlineCouponSuccess";
    }

    /**
     * 为优惠券套餐付款
     *
     * @param payMethod
     * @param totlePrice
     * @param model
     * @return
     */
    @RequestMapping("/payCouponSet")
    public String payCouponSet(PayMethod payMethod, String totlePrice, Model model) {
        PayInfoWrapper wrapper = new PayInfoWrapper();
        wrapper.setTotalFee(Money.YuanToCent(totlePrice));
        wrapper.setCallBackClass(CouponSetPayCallback.class);
        wrapper.setPayMethod(PayMethod.directPay);
        wrapper.setBizType("coupon");  //设置付款方式为优惠券
        PayRequestHandler handler = PaymentManager.getPayRequestHandler(wrapper.getPayMethod());
        model.addAttribute("form", handler.forwardToPay(wrapper));
        return "payTo";
    }

    /**
     * 跳转支付页面
     *
     * @param payInfoWrapper
     * @param setType
     * @param request
     * @param model
     * @return
     */
    @RenderHeaderFooter
    @Transactional
    @RequestMapping(value = "/toPayCouponSet", method = RequestMethod.POST)
    public String toPayCouponSet(PayInfoWrapper payInfoWrapper, CouponSetType setType, HttpServletRequest request, Model model) throws UnsupportedEncodingException {
        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        if (null == userInfo) {
            return "redirect:" + urlBrokerFactory.getUrl("ToPageLogin").addQueryData("backUrl", URLEncoder.encode(urlBrokerFactory.getUrl("CouponSet").addQueryData("type", setType).toString(), "UTF-8")).toString();
        }

        int userId = LoginInfo.getLoginUser(request).getId();

        payInfoWrapper.setCallBackClass(CouponSetPayCallback.class);

        PayMethod payMethod = payInfoWrapper.getPayMethod();

        //默认为支付宝,主要用来更新支付方式
        PayBank bank = PayBank.Alipay;
        //目前设置默为阿里支付
        if (payInfoWrapper.isBank()) {
            bank = PayBank.valueOf(payInfoWrapper.getDefaultbank());
        } else {
            if (payMethod.equals(PayMethod.Tenpay)) {
                bank = PayBank.Tenpay;
            }
        }

        //订单总金额，显示在支付宝收银台里的“应付总额”里
        payInfoWrapper.setTotalFee(setType.price());

        PayRequestHandler payService = PaymentManager.getPayRequestHandler(payMethod);
        model.addAttribute("form", payService.forwardToPay(payInfoWrapper));
        model.addAttribute("title", "请稍后，正在提交信息到支付宝！");

        /**
         * 记录用户购买的优惠券套餐
         */

        String sql = "insert into CouponSet (userId,setType,tradeNo,payMethod,totalPrice,createDate,updateDate,isDelete,allocated) values " +
                "(" + userId + ",'" + setType + "'," + payInfoWrapper.getTradeNo() + ",'" + payMethod + "'," + setType.price() + ",now(),now(),0,0)";
        jdbcTemplate.update(sql);

        return "payTo";
    }

}
