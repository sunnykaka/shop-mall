package com.kariqu.buyer.web.controller.myinfo;

import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.OrderView;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.buyer.web.helper.TradeViewHelper;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.Progress;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-3
 *        Time: 上午11:39
 */
@Controller
public class MyOrdersController  {

    @Autowired
    private TradeViewHelper tradeViewHelper;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @RenderHeaderFooter
    @RequestMapping(value = "/my/order/nav")
    public String toMyOrders(Integer state, Model model, HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        state = state != null ? state : 0;

        queryAllStateOrderCount(sessionUserInfo.getId(),model);

        model.addAttribute("site_title", "我的订单");
        model.addAttribute("state", state);

        model.addAttribute("contentVm", "myinfo/myOrders.vm");
        return "myinfo/myInfoLayout";
    }

    /**
     * 获取同一类别的状态列表
     *
     * @param state 0.全部, 1.未付款, 2.已付款, 3.交易完成, 4.无效交易
     */
    @RequestMapping(value = "/my/order/list")
    public String myOrderList(Integer pageNo, Integer state, Model model, HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        pageNo = pageNo != null ? pageNo : 1;
        state = state != null ? state : 0;

        Page<Order> orderPage = tradeCenterUserClient.getOrderPageByUserId(sessionUserInfo.getId(), state, new Page<Order>(pageNo, 5));
        Page<OrderView> orderViewPage = tradeViewHelper.OrderView(orderPage);

        model.addAttribute("orderPageBar", PageProcessor.process(orderPage));
        model.addAttribute("orderPage", orderViewPage);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("state", state);

        return "myinfo/orderList";
    }

    /**
     * 进入订单详情
     * //自填的收获地址 order.logistics.province
     * //运单号 order.logistics.deliveryInfo.waybillNumber
     * //物流公司类型 order.logistics.deliveryInfo.deliveryType
     * @param request
     * @param model
     * @param orderNo
     * @return
     */
    @RenderHeaderFooter
    @RequestMapping(value = "/my/order/{orderNo}")
    public String orderList(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable("orderNo") long orderNo) throws IOException {
        try {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            //订单详情
            Order order = tradeCenterUserClient.getOrderDetails(orderNo, sessionUserInfo.getId());
            if (order == null) {
                response.sendError(404);
                return null;
            }
            if (order.getIntegral() > 0)
                model.addAttribute("integralPrice", tradeCenterUserClient.calculateIntegralWithMoney(order.getIntegral()));
            if (StringUtils.isNotBlank(order.getCouponMoney()))
                model.addAttribute("couponMoney", order.getCouponMoney());

            //订单进度条
            Progress progress = tradeCenterUserClient.getOrderProgress(order.getId());

            model.addAttribute("order", order);
            model.addAttribute("progress", progress);
            model.addAttribute("orderMessage", tradeCenterUserClient.queryUserMessage(order.getId()));

            model.addAttribute("site_title", "订单详情");

            model.addAttribute("contentVm", "myinfo/orderDetail.vm");
            return "myinfo/myInfoLayout";
        } catch (Exception  e) {
            response.sendError(404);
            return null;
        }
    }

    /**
     * 获取用户所有状态下的订单总数
     * @param userId
     * @param model
     */
    private void queryAllStateOrderCount(Integer userId, Model model) {
        Map<String,Integer> orderCount = new HashMap<String,Integer>();

        // 0.全部, 1.未付款, 2.已付款, 3.交易完成, 4.无效交易
        orderCount.put("all",tradeCenterUserClient.queryOrderCountByUserIdAndState(userId,0));
        orderCount.put("notPay",tradeCenterUserClient.queryOrderCountByUserIdAndState(userId,1));
        orderCount.put("pay",tradeCenterUserClient.queryOrderCountByUserIdAndState(userId,2));
        orderCount.put("tradeSuccess",tradeCenterUserClient.queryOrderCountByUserIdAndState(userId,3));
        orderCount.put("invalidTrade",tradeCenterUserClient.queryOrderCountByUserIdAndState(userId,4));

        model.addAttribute("orderCount",orderCount);
    }

}
