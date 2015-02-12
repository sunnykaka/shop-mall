package com.kariqu.buyer.web.controller.trade;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.productcenter.domain.MealItem;
import com.kariqu.productcenter.service.MealSetService;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.service.AddressService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 13-5-7 上午10:28
 */
@Controller
public class OrderMealController {

    private static final Logger LOGGER = Logger.getLogger(OrderMealController.class);

    @Autowired
    private AddressService addressService;

    @Autowired
    private MealSetService mealSetService;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    /**
     * 套餐提交订单.
     *
     * @param addressId
     * @param payBank
     * @param invoiceInfo
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/order/meal/add", method = RequestMethod.POST)
    public String orderMealAdd(int addressId, PayBank payBank, InvoiceInfo invoiceInfo, String mealId, String skuCount,
                               String messageInfo, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!NumberUtils.isNumber(mealId)) {
            response.sendError(404);
            return null;
        }

        List<TradeItem> tradeItemList = new ArrayList<TradeItem>();
        for (int i = 0; i < Integer.parseInt(skuCount); i++) {
            String skuId = request.getParameter("skuId_" + i);
            if (!NumberUtils.isNumber(skuId)) {
                response.sendError(404);
                return null;
            }

            final MealItem mealItem = mealSetService.queryMealItemBySkuIdAndMealId(Long.parseLong(skuId), Integer.parseInt(mealId));
            if (mealItem == null) {
                response.sendError(404);
                return null;
            }

            TradeItem tradeItem = new TradeItem();
            tradeItem.setSkuId(mealItem.getSkuId());
            tradeItem.setNumber(mealItem.getNumber());
            tradeItem.setTradePriceStrategy(new TradePriceStrategy() {
                @Override
                public void apply(OrderItem orderItem) {
                    orderItem.setUnitPrice(mealItem.getSkuPrice());
                }
            });

            tradeItemList.add(tradeItem);
        }

        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        Logistics logistics = new Logistics();
        logistics.injectBackUpAddress(addressService.getAddress(addressId));
        logistics.setDeliveryInfo(new DeliveryInfo());
        logistics.setAddressId(addressId);
        logistics.setAddressOwner(sessionUserInfo.getUserName());

        SubmitOrderInfo submitOrderInfo = new SubmitOrderInfo();
        submitOrderInfo.setLogistics(logistics);
        submitOrderInfo.setUser(sessionUserInfo.getId());
        submitOrderInfo.setAccountType(sessionUserInfo.getAccountType());
        submitOrderInfo.setPayBank(payBank);
        submitOrderInfo.setInvoiceInfo(invoiceInfo);
        submitOrderInfo.setPayType(PayType.OnLine);
        submitOrderInfo.setMessageInfo(messageInfo);

        submitOrderInfo.setTradeItemList(tradeItemList);

        try {
            List<Order> orders = tradeCenterUserClient.submitOrder(submitOrderInfo);

            response.sendRedirect("/order/pay/" + Joiner.on(",").join(Lists.transform(orders, new Function<Order, Long>() {
                @Override
                public Long apply(Order input) {
                    return input.getOrderNo();
                }
            })));

            return null;
        } catch (OrderBaseException ex) {
            LOGGER.error("从套餐处提交订单时异常", ex);
            model.addAttribute("msg", ex.getMessage());
            return "order/orderFail";
        } catch (Exception ex) {
            LOGGER.error("提交订单发生错误", ex);
            model.addAttribute("msg", "服务器发生错误，请您联系客服人员进行处理！");
            return "order/orderFail";
        }
    }

}
