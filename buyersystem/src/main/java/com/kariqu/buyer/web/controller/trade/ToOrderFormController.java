package com.kariqu.buyer.web.controller.trade;

import com.google.common.collect.Lists;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.common.Token;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.controller.myinfo.AddressConstants;
import com.kariqu.buyer.web.helper.*;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.service.*;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.service.*;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下订单界面渲染控制器
 *
 * @author Asion
 * @since 1.0.0
 *        Date: 13-1-14
 *        Time: 下午5:48
 */
@Controller
public class ToOrderFormController {

    private final Log logger = LogFactory.getLog(ToOrderFormController.class);

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private InvoiceCompanyService invoiceCompanyService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private TradeViewHelper tradeViewAdaptor;

    @Autowired
    private OrderSplitHelper orderSplitHelper;

    @Autowired
    private UserService userService;

    @Autowired
    private MealSetService mealSetService;

    @Autowired
    private SkuStorageService skuStorageService;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ProductIntegralConversionService productIntegralConversionService;

    @Autowired
    private ProductSuperConversionService productSuperConversionService;

    /**
     * 通过购物车转到订单填写页面
     * 到达这个地址之前必须登录，这里通过登录的ID去查询购物车
     * 在转到订单之前，检查所有的购物项是否满足库存约束
     *
     */
//    @RenderHeaderFooter
    @Token
    @RequestMapping(value = "/order")
    public String newOrderByCart(Model model, HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        Cart cart = cartService.getCartByUserId(sessionUserInfo.getId());
        if (cart == null) {
            logger.warn("购物车对象为null的时候进入了填写订单页面:" + sessionUserInfo);
            return "redirect:" + urlBrokerFactory.getUrl("Cart").toString();
        }
        List<CartItem> cartItemList = cart.getCartItemList();
        if (cartItemList.size() == 0) {
            logger.warn("购物车为空的时候进入了填写订单页面:" + sessionUserInfo);
            return "redirect:" + urlBrokerFactory.getUrl("Cart").toString();
        }
        List<String> errorMap = tradeViewAdaptor.checkTradeItem(cartItemList);
        if (errorMap.size() > 0) {
            model.addAttribute("msg", errorMap.toString());
            model.addAttribute("site_title", "发生错误");
            return "error";
        }

        renderAddress(addressService.queryAllAddress(sessionUserInfo.getId()), model);
        model.addAttribute("couponList",couponService.queryCanUseCouponByTotalPriceAndUserId(Money.YuanToCent(cart.getTotalPrice()),sessionUserInfo.getId()));

        writeOrderItem(model, cart.convertToOrderItemList());
        model.addAttribute("totalPrice", cart.getTotalPrice());
        model.addAttribute("fromCart", true);
        model.addAttribute("totalCurrency", userService.getUserById(sessionUserInfo.getId()).getCurrency());
        model.addAttribute("OrderSubmitUrl", urlBrokerFactory.getUrl("SubmitOrder").toString());
        model.addAttribute("companyList", invoiceCompanyService.queryUserInvoiceCompany(sessionUserInfo.getId()));
        model.addAttribute("integralCount", integralService.integralCount());
        model.addAttribute("site_title", "填写订单");

        return "order/order";
    }

    /**
     * 积分商城订单页
     * @param model
     * @param id
     * @param activityType
     * @param request
     * @return
     */
    @RenderHeaderFooter
    @Token
    @RequestMapping(value = "/currency/order/{id}/{activityType}")
    public String newOrderByCurrency(Model model, @PathVariable int id, @PathVariable ProductActivityType activityType,HttpServletRequest request) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        int buyNumber = ServletRequestUtils.getIntParameter(request, "buyNumber", 1);//购买数量
        if (buyNumber <= 1 ) {
            buyNumber = 1;
        }
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (ProductActivityType.IntegralConversion == activityType){//积分兑换
            ProductIntegralConversion productIntegralConversion = productIntegralConversionService.queryProductIntegralConversionById(id);
            model.addAttribute("productIntegralConversion",productIntegralConversion);

            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(productIntegralConversion.getSkuId());
            orderItem.setNumber(buyNumber);
            orderItemList.add(orderItem);
            model.addAttribute("totalPayIntegral", productIntegralConversion.fetchCurrencyMutiNum(buyNumber)); //要付出的总积分
        }else if (ProductActivityType.SuperConversion == activityType){//超值兑换
            ProductSuperConversion productSuperConversion = productSuperConversionService.queryProductSuperConversionById(id);
            model.addAttribute("productSuperConversion",productSuperConversion);

            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(productSuperConversion.getSkuId());
            orderItem.setNumber(buyNumber);
            orderItemList.add(orderItem);
            model.addAttribute("totalPayIntegral", productSuperConversion.fetchCurrencyMutiNum(buyNumber)); //要付出的总积分
            model.addAttribute("totalPayPrice", productSuperConversion.fetchMoneyForPriceMutiNum(buyNumber)); //要付出的价钱
        }

        writeOrderItem(model, orderItemList);
        renderAddress(addressService.queryAllAddress(sessionUserInfo.getId()), model);
        model.addAttribute("totalIntegral", userService.getUserById(sessionUserInfo.getId()).getCurrency());//用户总积分
        model.addAttribute("fromCart", false);//直接兑换
        model.addAttribute("OrderSubmitUrl", urlBrokerFactory.getUrl("SubmitOrder").toString());
        model.addAttribute("companyList", invoiceCompanyService.queryUserInvoiceCompany(sessionUserInfo.getId()));//发票抬头公司列表
        model.addAttribute("integralCount", integralService.integralCount());
        model.addAttribute("site_title", "填写订单");
        model.addAttribute("buyNumber", buyNumber);

        return "orderForCurrency";
    }

    /**
     * 向订单界面渲染地址数据
     *
     */
    private void renderAddress(List<Address> addressList, Model model) {
        //取出常用的三个地址
        List<Address> topAddressList = addressList;
        if (addressList.size() > AddressConstants.Top_Max_Count) {
            topAddressList = addressList.subList(0, AddressConstants.Top_Max_Count);
        }
        Address defaultAddress = null;
        for (Address top : topAddressList) {
            if (top.isDefaultAddress()) {
                defaultAddress = top;
                break;
            }
        }

        //如果没有默认值就用列表中第一个，因为频率是最高的
        if (defaultAddress == null && topAddressList.size() > 0) {
            defaultAddress = topAddressList.get(0);
            defaultAddress.setDefaultAddress(true);
        }

        model.addAttribute("addressList", topAddressList);
        model.addAttribute("defaultAddress", defaultAddress);
        model.addAttribute("addressSize", addressList.size());
    }

    private String getTotalPrice(List<TradeItemView> itemList) {
        long cent = 0;
        for (TradeItemView item : itemList) {
            cent = cent + (new Money(item.getPrice()).getCent()) * item.getNumber();
        }
        Money money = new Money();
        money.setCent(cent);
        return money.toString();
    }


    /**
     * 往model中写入订单拆分信息，价格，优惠信息，sku列表
     *
     */
    private void writeOrderItem(Model model, List<OrderItem> orderItemList) {
        Map<ProductStorage, List<TradeItemView>> productStorageListMap = orderSplitHelper.buildSplitOrderForView(orderItemList);
        model.addAttribute("cartItemGroup", productStorageListMap);

        Map<ProductStorage, String> priceMap = new HashMap<ProductStorage, String>();

        for (Map.Entry<ProductStorage, List<TradeItemView>> productStorageListEntry : productStorageListMap.entrySet()) {
            priceMap.put(productStorageListEntry.getKey(), getTotalPrice(productStorageListEntry.getValue()));
        }
        model.addAttribute("priceMap", priceMap);
    }

    /**
     * 购买套餐
     *
     */
    @RenderHeaderFooter
    @Token
    @RequestMapping(value = "/order/meal", method = RequestMethod.POST)
    public String newOrderByMeal(String mealId, String skuCount,
                  Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!NumberUtils.isNumber(mealId)) {
            response.sendError(404);
            return null;
        }
        if (!NumberUtils.isNumber(skuCount)) {
            response.sendError(404);
            return null;
        }

        List<MealTradeItemView> mealTradeItemViewList = new ArrayList<MealTradeItemView>();
        List<TradeItem> tradeItems = new ArrayList<TradeItem>();
        String skuId = "";
        long mealTotalPrice = 0l;
        for (int i = 0; i < Integer.parseInt(skuCount); i++) {
            skuId = request.getParameter("skuId_" + i);
            if (!NumberUtils.isNumber(skuId)) {
                response.sendError(404);
                return null;
            }

            MealItem mealItem = mealSetService.queryMealItemBySkuIdAndMealId(Long.parseLong(skuId), Integer.parseInt(mealId));
            if (mealItem == null) {
                response.sendError(404);
                return null;
            }
            mealTotalPrice += mealItem.getSkuPrice() * mealItem.getNumber();

            TradeItem tradeItem = new TradeItem();
            tradeItem.setSkuId(mealItem.getSkuId());
            tradeItem.setNumber(mealItem.getNumber());
            tradeItems.add(tradeItem);

            MealTradeItemView mealTradeItemView = new MealTradeItemView();
            mealTradeItemView.setTradeItem(orderSplitHelper.convertToView(tradeItem));
            mealTradeItemView.setMealPrice(Money.getMoneyString(mealItem.getSkuPrice()));
            mealTradeItemViewList.add(mealTradeItemView);
        }

        List<String> errorMap = tradeViewAdaptor.checkTradeItem(tradeItems);
        if (errorMap.size() > 0) {
            model.addAttribute("msg", errorMap.toString());
            model.addAttribute("site_title", "发生错误");
            return "error";
        }

        // 套餐相关数据
        model.addAttribute("meal", mealSetService.getMealSetById(Integer.parseInt(mealId)));
        model.addAttribute("mealTotalPrice", Money.getMoneyString(mealTotalPrice));
        model.addAttribute("mealTradeItemViewList", mealTradeItemViewList);

        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        renderAddress(addressService.queryAllAddress(sessionUserInfo.getId()), model);
        model.addAttribute("companyList", invoiceCompanyService.queryUserInvoiceCompany(sessionUserInfo.getId()));
        model.addAttribute("site_title", "填写订单");

        // 所有的 sku 都放在一个仓库中
        model.addAttribute("productStorage", skuStorageService.getConcretionStorage(Long.parseLong(skuId)));

        return "orderMeal";
    }

}
