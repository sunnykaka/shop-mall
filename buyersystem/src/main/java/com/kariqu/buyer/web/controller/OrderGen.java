package com.kariqu.buyer.web.controller;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.ProductActivityType;
import com.kariqu.productcenter.domain.SkuPriceDetail;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.ProductActivityService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.service.AddressService;
import com.kariqu.usercenter.domain.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User: Asion
 * Date: 13-8-27
 * Time: 上午11:52
 */
@Controller
public class OrderGen {


    @Autowired
    private AddressService addressService;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @Autowired
    private ProductActivityService productActivityService;

    @Autowired
    private SkuService skuService;

    private static List<Long> skuList = new ArrayList<Long>();

    static {
        skuList.add(2406l);
        skuList.add(1779l);
        skuList.add(1123l);
        skuList.add(2106l);
        skuList.add(1123l);
        skuList.add(2106l);
        skuList.add(1779l);
        skuList.add(2106l);
        skuList.add(2406l);
        skuList.add(1123l);
    }


    @RequestMapping(value = "/order/gen/add")
    public void gen(HttpServletResponse response,int number) throws OrderBaseException, IOException {

        for (int i = 0; i < number; i++) {
            Logistics logistics = new Logistics();
            logistics.injectBackUpAddress(addressService.getAddress(54));
            logistics.setDeliveryInfo(new DeliveryInfo());
            logistics.setAddressId(54);
            logistics.setAddressOwner("asion");

            SubmitOrderInfo submitOrderInfo = new SubmitOrderInfo();
            submitOrderInfo.setLogistics(logistics);
            submitOrderInfo.setUser(2);
            submitOrderInfo.setUserName("asion");
            submitOrderInfo.setAccountType(AccountType.KRQ);
            submitOrderInfo.setPayBank(PayBank.Alipay);

            InvoiceInfo invoiceInfo = new InvoiceInfo();
            invoiceInfo.setCompanyName("ddddd");


            submitOrderInfo.setInvoiceInfo(invoiceInfo);
            submitOrderInfo.setPayType(PayType.OnLine);
            submitOrderInfo.setIntegral(1000000000000000000l);


            List<TradeItem> tradeItemList = new ArrayList<TradeItem>(1);
            OrderItem tradeItem = new OrderItem();
            Random rand = new Random();
            int index = rand.nextInt(9);
            tradeItem.setSkuId(skuList.get(index));
            tradeItem.setNumber(1);
            tradeItem.setTradePriceStrategy(new TradePriceStrategy() {
                @Override
                public void apply(OrderItem orderItem) {
                    StockKeepingUnit sku = skuService.getStockKeepingUnit(orderItem.getSkuId());
                    // 商品如果有参加活动, 则记录活动价格
                    SkuPriceDetail skuPriceDetail = productActivityService.getSkuMarketingPrice(sku);
                    // 如果有活动则使用活动价格
                    String price = skuPriceDetail.getSellPrice();
                    orderItem.setUnitPrice(Money.YuanToCent(price));
                    if (skuPriceDetail.getActivityType() != null) {
                        orderItem.setSkuMarketingId(skuPriceDetail.getMarketingId());
                        orderItem.setSkuActivityType(skuPriceDetail.getActivityType());
                    }
                }
            });
            tradeItemList.add(tradeItem);

            submitOrderInfo.setTradeItemList(tradeItemList);

            tradeCenterUserClient.submitOrder(submitOrderInfo);
        }



        response.getWriter().write("ok");


    }


}
