package com.kariqu.buyer.web.controller.trade;

import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.service.SupplierService;
import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.PayBank;
import com.kariqu.tradecenter.service.OrderQueryService;
import com.kariqu.tradecenter.payment.PayInfoWrapper;
import com.kariqu.tradecenter.service.TradeService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 付款控制器
 * 传过来的是虚拟订单的ID
 * 要进过处理
 * User: Asion
 * Date: 12-6-11
 * Time: 上午9:16
 */
@Controller
public class OrderPayController  {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private TradeService tradeService;

    /**
     * 去付款界面
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/order/pay/{orderNoList}")
    @RenderHeaderFooter
    @PageTitle("付款页面")
    public String toPay(HttpServletRequest request, Model model, @PathVariable("orderNoList") String orderNoList) {

        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        String[] split = orderNoList.split(",");

        Long[] list = new Long[split.length];
        for (int i = 0; i < split.length; i++) {
            if (!NumberUtils.isNumber(split[i])) {
                throw new RuntimeException("订单不存在");
            } else {
                list[i] = Long.valueOf(split[i]);
            }
        }

        List<Order> orders = new ArrayList<Order>(list.length);
        PayBank payBank = PayBank.Alipay;
        for (Long orderNo : list) {
            Order order = orderQueryService.getOrderByUserIdAndOrderNo(sessionUserInfo.getId(), orderNo);
            if (null == order) {
                model.addAttribute("site_title", "警告");
                model.addAttribute("msg", "没有此订单(" + orderNo + ")！");
                return "order/orderFail";
            }
            if (!order.getOrderState().waitPay(order.getPayType())) {
                model.addAttribute("site_title", "警告");
                model.addAttribute("msg", "订单(" + orderNo + ")不支持付款操作！");
                return "order/orderFail";
            }
            orders.add(order);
            payBank = order.getPayBank();
        }
        model.addAttribute("ordersNoList", orderNoList);
        model.addAttribute("orders", orders);
        model.addAttribute("totalPriceForYuan", tradeService.getPayPriceForYuan(list));
        model.addAttribute("payBank", payBank.toString());

        return "order/onlineOrderSuccess";
    }


    @RequestMapping(value = "/pay/order/status/query")
    @RenderHeaderFooter
    public String queryPayStatus(PayInfoWrapper payInfoWrapper,String orderNo, HttpServletRequest request, Model model) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        Long[] orderNoList =getOrderNoList(orderNo);
        List<Order> orders = new ArrayList<Order>(orderNoList.length);
        for (Long orno : orderNoList) {
            orders.add(orderQueryService.getOrderByUserIdAndOrderNo(sessionUserInfo.getId(), orno));
        }
        Map<Long, String> consignmentInfo = new HashMap<Long, String>();
        for (Order order : orders) {
            Supplier customer = supplierService.queryCustomerById(order.getCustomerId());
            ProductStorage productStorage = supplierService.queryProductStorageById(order.getStorageId());
            StringBuilder sb = new StringBuilder();
            sb.append("由商家");
            sb.append(customer != null ? customer.getName() + "从" : "");
            sb.append(productStorage != null ? productStorage.getName() : "");
            sb.append("发货");
            consignmentInfo.put(order.getOrderNo(), sb.toString());
        }
        model.addAttribute("consignmentInfo", consignmentInfo);

        model.addAttribute("orders", orders);
        model.addAttribute("total_fee", tradeService.getPayPriceForYuan(orderNoList));
        model.addAttribute("site_title", "支付信息");
        return "order/payInfo";
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
