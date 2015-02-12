package com.kariqu.buyer.web.controller.trade;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.kariqu.buyer.web.common.CheckFormToken;
import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.service.ProductConversionBaseService;
import com.kariqu.usercenter.domain.Currency;
import com.kariqu.common.lib.Money;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.service.ProductActivityService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.OrderBaseException;
import com.kariqu.tradecenter.service.AddressService;
import com.kariqu.tradecenter.service.CartService;
import com.kariqu.tradecenter.service.CouponService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单控制器
 * 订单中的商品可能来自不同商家的不同仓库，所以需要按照sku所在的仓库进行订单拆分
 * 一个sku可能放在多个仓库，如果这样就不能按照仓库来拆分订单，所以目前一个sku只能放在一个仓库里面
 * 以后如果sku和仓库是多对多的关系，需要按照订单的送货地址确定仓库来拆分订单
 * User: Asion
 * Date: 11-10-14
 * Time: 上午11:15
 */
@Controller
public class OrderController {

    private final Log logger = LogFactory.getLog(OrderController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    @Qualifier("executor")
    private TaskExecutor taskExecutor;

    @Autowired
    private ProductActivityService productActivityService;

    @Autowired
    private ProductConversionBaseService productConversionBaseService;

    @Autowired
    private SkuService skuService;

    /**
     * 提交订单
     *
     * @param source
     * @param model
     * @return
     */
    @RequestMapping(value = "/order/add", method = RequestMethod.POST)
    public String submitOrder(@RequestParam("source") String source, Model model) {
        if (source.equals("cart")) {
            return "forward:/order/byCart/add";
        } else if (source.equals("detail")) {
            return "forward:/order/bySku/add";
        } else if(source.equals("currency")){
            return "forward:/order/byCurrency/add";
        }else {
            model.addAttribute("msg", "参数错误");
            return "order/orderFail";
        }
    }

    /**
     * 提交订单
     * 在这里严格检查库存，不能使用两个事务进行查询和更新
     * 必须保证能下单就必须有货
     * <p/>
     * 依次检查订单中每一个商品
     * <p/>
     * 下订单的时候如果发现这个订单是从购物车来生成的，则要删除购物车数据
     *
     * @return
     */
    @CheckFormToken
//    @RenderHeaderFooter
    @RequestMapping(value = "/order/byCart/add", method = RequestMethod.POST)
    @PageTitle("提交订单")
    public String addCartOrder(String addressId, PayBank payBank, InvoiceInfo invoiceInfo, CouponInfo couponInfo, String integral,
                               String messageInfo, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //1.获取登陆信息
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        Cart cart = cartService.getCartByUserId(sessionUserInfo.getId());
        if (cart == null || cart.getCartItemList().size() <= 0) {
            model.addAttribute("site_title", "出现错误");
            model.addAttribute("msg", "购物车为空");
            return "order/orderFail";
        }
        List<CartItem> cartItems = cart.getCartItemList();

        try {
            int addId = NumberUtils.toInt(addressId);
            Logistics logistics = new Logistics();
            logistics.injectBackUpAddress(addressService.getAddress(addId));
            logistics.setDeliveryInfo(new DeliveryInfo());
            logistics.setAddressId(addId);
            logistics.setAddressOwner(sessionUserInfo.getUserName());

            SubmitOrderInfo submitOrderInfo = new SubmitOrderInfo();
            submitOrderInfo.setLogistics(logistics);
            submitOrderInfo.setUser(sessionUserInfo.getId());
            submitOrderInfo.setUserName(sessionUserInfo.getUserName());
            submitOrderInfo.setAccountType(sessionUserInfo.getAccountType());
            submitOrderInfo.setPayBank(payBank);
            submitOrderInfo.setInvoiceInfo(invoiceInfo);
            submitOrderInfo.setPayType(PayType.OnLine);
            submitOrderInfo.setIntegral(NumberUtils.isNumber(integral) ? Currency.CurrencyToIntegral(integral) : 0);
            if (couponInfo.isUseCoupon()) {
                submitOrderInfo.setCoupon(couponService.getCouponByCode(couponInfo.getCode()));
            }

            List<TradeItem> tradeItemList = new ArrayList<TradeItem>(cartItems.size());
            for (CartItem cartItem : cartItems) {
                OrderItem tradeItem = new OrderItem();
                tradeItem.setSkuId(cartItem.getSkuId());
                tradeItem.setNumber(cartItem.getNumber());
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
            }
            submitOrderInfo.setTradeItemList(tradeItemList);
            submitOrderInfo.setMessageInfo(messageInfo);

            List<Order> orders = tradeCenterUserClient.submitOrder(submitOrderInfo);

            if (addId > 0)
                updateFrequencyByAddressId(addId);
            //清空购物车
            cartService.deleteCart(cart.getId());

            int i = 0;
            for (Order order : orders) {
                if (order.getOrderState() == OrderState.Pay) i++;
            }
            if (i == orders.size()) {
                model.addAttribute("total_fee", 0);
                model.addAttribute("orders", orders);
                model.addAttribute("site_title", "易居尚-交易付款-订单付款成功");
                return "order/paySuccess";
            }

            response.sendRedirect("/order/pay/" + Joiner.on(",").join(Lists.transform(orders, new Function<Order, Long>() {
                @Override
                public Long apply(Order input) {
                    return input.getOrderNo();
                }
            })));

            return null;
        } catch (OrderBaseException ex) {
            logger.error("提交订单时异常", ex);
            model.addAttribute("msg", ex.getMessage());
            return "order/orderFail";
        } catch (Exception ex) {
            logger.error("从购物车提交订单发生错误", ex);
            model.addAttribute("msg", "服务器发生错误，请您联系客服人员进行处理！");
            return "order/orderFail";
        }
    }

    /**
     * 积分商城提交订单
     * @param skuId
     * @param number
     * @param addressId
     * @param payBank
     * @param invoiceInfo
     * @param messageInfo
     * @param model
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @CheckFormToken
    @RenderHeaderFooter
    @RequestMapping(value = "/order/byCurrency/add", method = RequestMethod.POST)
    @PageTitle("提交订单")
    public String addCurrencyOrder(final Long skuId, int number,  String addressId, PayBank payBank, InvoiceInfo invoiceInfo,
                                   String messageInfo, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //1.获取登陆信息
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);

        try {
            final SubmitOrderInfo submitOrderInfo = new SubmitOrderInfo();

            List<TradeItem> tradeItemList = Lists.newArrayList();
            OrderItem tradeItem = new OrderItem();
            tradeItem.setSkuId(skuId);
            tradeItem.setNumber(number);
            tradeItem.setTradePriceStrategy(new TradePriceStrategy() {
                @Override
                public void apply(OrderItem orderItem) {
                    StockKeepingUnit sku = skuService.getStockKeepingUnit(orderItem.getSkuId());

                    //查找出商品的活动, 则记录活动价格
                    ProductConversionBase conversionBase = productConversionBaseService.fetchConversionBySkuIdAndDaytime((int) sku.getId(), sku.getProductId(), new Date());
                    if (conversionBase != null) {
                        if (conversionBase instanceof ProductIntegralConversion) {
                            ProductIntegralConversion integral = (ProductIntegralConversion) conversionBase;
                            orderItem.setUnitPrice(0); //这种是只要积分, 不要钱的那种
                            orderItem.setSkuMarketingId(integral.getId());
                            orderItem.setSkuActivityType(ProductActivityType.IntegralConversion);
                            orderItem.setUserBuyCount(integral.getUserBuyCount());
                            orderItem.setActivityId(integral.getId());
                            orderItem.setIntegral(Long.valueOf(integral.getIntegralCount()));
                        } else if (conversionBase instanceof ProductSuperConversion) {
                            ProductSuperConversion superConversion = (ProductSuperConversion)conversionBase;
                            orderItem.setUnitPrice(superConversion.getMoney());
                            orderItem.setSkuMarketingId(superConversion.getId());
                            orderItem.setSkuActivityType(ProductActivityType.SuperConversion);
                            orderItem.setUserBuyCount(superConversion.getUserBuyCount());
                            orderItem.setActivityId(superConversion.getId());
                            orderItem.setIntegral(Long.valueOf(superConversion.getIntegralCount()));
                        }
                    }
                }

            });
            tradeItemList.add(tradeItem);

            int addId = NumberUtils.toInt(addressId);
            Logistics logistics = new Logistics();
            logistics.injectBackUpAddress(addressService.getAddress(addId));
            logistics.setDeliveryInfo(new DeliveryInfo());
            logistics.setAddressId(addId);
            logistics.setAddressOwner(sessionUserInfo.getUserName());

            submitOrderInfo.setLogistics(logistics);
            submitOrderInfo.setUser(sessionUserInfo.getId());
            submitOrderInfo.setUserName(sessionUserInfo.getUserName());
            submitOrderInfo.setAccountType(sessionUserInfo.getAccountType());
            submitOrderInfo.setPayBank(payBank);
            submitOrderInfo.setInvoiceInfo(invoiceInfo);//发票信息
            submitOrderInfo.setPayType(PayType.OnLine);

            /*submitOrderInfo.setIntegral(integralTemp);//积分*/

            submitOrderInfo.setTradeItemList(tradeItemList);
            submitOrderInfo.setMessageInfo(messageInfo);

            List<Order> orders = tradeCenterUserClient.submitCurrencyOrder(submitOrderInfo);

            int i = 0;
            for (Order order : orders) {
                if (order.getOrderState() == OrderState.Pay) i++;
            }
            if (i == orders.size()) {
                model.addAttribute("total_fee", 0);
                model.addAttribute("orders", orders);
                model.addAttribute("site_title", "易居尚-交易付款-订单付款成功");
                return "order/paySuccess";
            }

            response.sendRedirect("/order/pay/" + Joiner.on(",").join(Lists.transform(orders, new Function<Order, Long>() {
                @Override
                public Long apply(Order input) {
                    return input.getOrderNo();
                }
            })));

            return null;
        } catch (OrderBaseException ex) {
            logger.error("提交订单时异常", ex);
            model.addAttribute("msg", ex.getMessage());
            return "order/orderFail";
        } catch (Exception ex) {
            logger.error("从积分商城提交订单发生错误", ex);
            model.addAttribute("msg", "服务器发生错误，请您联系客服人员进行处理！");
            return "order/orderFail";
        }
    }


    /**
     * 手动取消订单
     * 需要还原库存
     *
     * @param orderId
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/order/cancel", method = RequestMethod.POST)
    public void cancelOrder(long orderId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        try {
            tradeCenterUserClient.cancelOrder(orderId, sessionUserInfo.getId());
            new JsonResult(true).toJson(response);

        } catch (Exception e) {

            logger.error("订单取消失败! orderId: " + orderId + " , loginUser: " + sessionUserInfo, e);
            new JsonResult(false, "取消失败").toJson(response);
        }
    }

    /**
     * 用户手动确认订单完成
     *
     * @param orderId
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/order/confirmReceipt", method = RequestMethod.POST)
    public void confirmReceipt(long orderId, HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        try {
            tradeCenterUserClient.confirmOrderSuccess(orderId, sessionUserInfo.getId());
            new JsonResult(true).addData("url", this.urlBrokerFactory.getUrl("BuyHome")
                    + "/my/valuation/toValuation/" + orderId)
                    .toJson(response);
        } catch (OrderBaseException oe) {
            logger.error("确认收货失败！orderid = " + orderId, oe);
            new JsonResult(false, oe.getMessage()).toJson(response);

        } catch (Exception e) {
            logger.error("确认收货失败！orderid = " + orderId, e);
            new JsonResult(false, "确认收货失败").toJson(response);
        }
    }


    private void updateFrequencyByAddressId(final int addressId) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    addressService.updateFrequencyByAddressId(addressId);
                } catch (Exception e) {
                    logger.error("更新用户收获地址使用频次失败！" + addressId, e);

                }
            }
        });
    }
}
