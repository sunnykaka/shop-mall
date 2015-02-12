package com.kariqu.buyer.web.controller.trade;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.usercenter.domain.Currency;
import com.kariqu.common.lib.Money;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.Cart;
import com.kariqu.tradecenter.domain.Coupon;
import com.kariqu.tradecenter.domain.SubmitOrderForPrice;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.service.CartService;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Asion
 * Date: 13-1-17
 * Time: 上午10:07
 */
@Controller
public class CouponChecker {

    @Autowired
    private CartService cartService;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @Autowired
    private UserService userService;

    /**
     * 检查积分,用积分抵押现金，如果积分不满足使用规则则报错
     *  参数是本次参与的所有可减金额的营销行为
     * @param code
     * @param integral
     * @param response
     * @param request
     * @throws IOException
     */
    @RequestMapping(value = "/order/integral/check")
    public void checkIntegral(@RequestParam(value = "code", defaultValue = "") String code, @RequestParam(value = "integral", defaultValue = "0") String integral,
                              HttpServletResponse response, HttpServletRequest request) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        Cart cart = cartService.getCartByUserId(sessionUserInfo.getId());
        if (cart != null) {
            try {
                Long integralCent = Currency.CurrencyToIntegral(integral);
                Money money = new Money(cart.getTotalPrice());
                User user = userService.getUserById(sessionUserInfo.getId());
                SubmitOrderForPrice submitOrderForPrice = tradeCenterUserClient.reducePriceBeforeCouponAfterIntegral(money.getCent(), code, integralCent, user);
                long newTotalPrice = submitOrderForPrice.getNewTotalPrice();
                String integralPrice = submitOrderForPrice.getIntegral();
                Coupon coupon = submitOrderForPrice.getCoupon();
                if (coupon != null) {
                    new JsonResult(true).addData("couponPrice", coupon.getPriceValue(money.getCent())).addData("integralPrice", integralPrice)
                            .addData("orderTotalPrice", Money.getMoneyString(newTotalPrice)).toJson(response);
                } else {
                    new JsonResult(true).addData("integralPrice", integralPrice).addData("orderTotalPrice", Money.getMoneyString(newTotalPrice)).toJson(response);
                }
            } catch (OrderBaseException e) {
                new JsonResult(false, e.getMessage()).toJson(response);
            }
        } else {
            new JsonResult(false).toJson(response);
        }
    }

    @RequestMapping(value = "/order/coupon/list", method = RequestMethod.GET)
    public void queryIntegral(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        Cart cart = cartService.getCartByUserId(sessionUserInfo.getId());
        if (cart != null) {
            Money money = new Money(cart.getTotalPrice());
            new JsonResult(true).addData("couponList", tradeCenterUserClient.queryCanUseCouponByTotalPriceAndUserId(money.getCent(), sessionUserInfo.getId())).toJson(response);
            return;
        }
        new JsonResult(false).toJson(response);
    }

}
