package com.kariqu.buyer.web.controller.trade;

import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.PayBank;
import com.kariqu.tradecenter.payment.PayInfoWrapper;
import com.kariqu.tradecenter.payment.PayRequestHandler;
import com.kariqu.tradecenter.payment.PaymentManager;
import com.kariqu.tradecenter.service.TradeService;
import com.kariqu.tradecenter.service.impl.PayMethod;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-15
 *        Time: 下午6:14
 */
@Controller
public class PaymentController extends CommonPayController {


    @Autowired
    private TradeService tradeService;


    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;


    @RequestMapping("/test")
    public String testPay(Model model){
        PayInfoWrapper wrapper = new PayInfoWrapper();
        wrapper.setTotalFee(1);
        wrapper.setCallBackClass(OrderPayCallback.class);
        wrapper.setPayMethod(PayMethod.bankPay);
        wrapper.setDefaultbank(PayBank.COMM.toString());
        wrapper.setBizType("coupon");

        PayRequestHandler handler = PaymentManager.getPayRequestHandler(wrapper.getPayMethod());
        model.addAttribute("form", handler.forwardToPay(wrapper));
        return "order/payTo";
    }


    /**
     * 先创建交易记录
     * 再跳转到支付宝付款界面
     *
     * @return
     */
    @RenderHeaderFooter
    @RequestMapping(value = "/toPay", method = RequestMethod.POST)
    public String toPayOrder(PayInfoWrapper payInfoWrapper,String orderNo, HttpServletRequest request, Model model) throws IOException {
        payInfoWrapper.setCallBackClass(OrderPayCallback.class);
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        Long[] orderNoList = getOrderNoList(orderNo);
        PayMethod payMethod = payInfoWrapper.getPayMethod();
        if (orderNoList.length > 0) {
            for (Long orNo : orderNoList) {

                Order order = orderQueryService.getOrderByUserIdAndOrderNo(sessionUserInfo.getId(), orNo);

                if (null == order) {
                    model.addAttribute("site_title", "警告");
                    model.addAttribute("msg", "没有此订单！");
                    return "order/orderFail";
                }

                if (checkOrderItem(order)) {
                    model.addAttribute("site_title", "提示");
                    model.addAttribute("msg", "订单：" + order.getOrderNo() + "中商品已下架或移除");
                    return "order/orderFail";
                }

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
                tradeCenterUserClient.updateOrderPayBank(orNo, bank);
            }
            tradeService.createOrderTradeInfo(payInfoWrapper.getTradeNo(), orderNoList);
            //订单总金额，显示在支付宝收银台里的“应付总额”里
            long total_fee = tradeService.getPayPriceForCent(orderNoList);
            payInfoWrapper.setTotalFee(total_fee);

            PayRequestHandler payService = PaymentManager.getPayRequestHandler(payMethod);

            payInfoWrapper.setBizType("order");//设置购买方式为order，订单
            model.addAttribute("form", payService.forwardToPay(payInfoWrapper));
            model.addAttribute("title", "请稍后，正在提交信息到支付宝！");
            return "order/payTo";
        } else {
            model.addAttribute("msg", "没有订单号");
            return "order/orderFail";
        }
    }


    private Long[] getOrderNoList(String orderNo) {
        if (StringUtils.isNotEmpty(orderNo)) {
            String[] split = orderNo.split(",");
            Long[] list = new Long[split.length];
            for (int i = 0; i < split.length; i++) {
                list[i] = Long.valueOf(split[i]);
            }
            return list;
        }
        return new Long[]{};
    }
}
