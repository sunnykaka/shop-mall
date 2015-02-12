package com.kariqu.buyer.web.controller.trade;

import com.kariqu.buyer.web.common.CartTrackUtil;
import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.JsonSku;
import com.kariqu.buyer.web.helper.OrderSplitHelper;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.buyer.web.helper.TradeViewHelper;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.open.api.ProductService;
import com.kariqu.designcenter.domain.open.module.Product;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.productcenter.service.SkuStorageService;
import com.kariqu.session.util.SessionUtils;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.tradecenter.domain.Cart;
import com.kariqu.tradecenter.domain.CartItem;
import com.kariqu.tradecenter.service.CartService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 购物车管理控制器
 * User: Asion
 * Date: 11-10-13
 * Time: 上午11:20
 */
@Controller
public class CartController {

    private static Log logger = LogFactory.getLog(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuStorageService skuStorageService;

    @Autowired
    private TradeViewHelper tradeViewAdaptor;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private OrderSplitHelper orderSplitHelper;


    /**
     * 显示购物车
     * 用户登陆了就用用户的ID去找购物车，没登陆就用跟踪ID去找
     *
     * @param model
     * @return
     */
//    @RenderHeaderFooter
    @RequestMapping(value = "/cart")
    public String displayCart(HttpServletRequest request, Model model) {
        model.addAttribute("site_title", "我的购物车");
        Cart cart = getCart(request);
        if (cart != null) {
            model.addAttribute("cartItemGroup", orderSplitHelper.buildSplitOrderForView(cart.convertToOrderItemList()));
            model.addAttribute("cart", cart);
        }
        return "cart";
    }

    /**
     * 将购物车数据以json格式输出
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/cart/json")
    public void displayCartByJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cart cart = getCart(request);
        if (cart == null) {
            new JsonResult(false, "购物车中没有商品").toJson(response);
            return;
        }

        List<CartItem> cartItemList = cart.getCartItemList();
        List<JsonSku> jsonSkus = tradeViewAdaptor.adaptJsonSku(cartItemList);
        int total = 0;
        for (JsonSku jsonSku : jsonSkus) {
            total += jsonSku.getNumber();
        }
        new JsonResult(true).addData("cartId", cart.getId()).addData("totalPrice",
                cart.getTotalPrice()).addData("skuList", jsonSkus).addData("totalNumber", total).toJson(response);
    }

    private Cart getCart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (SessionUtils.isLogin(session)) {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            return cartService.getCartByUserId(sessionUserInfo.getId());
        } else {
            String cartTrackId = CartTrackUtil.getTrackId(request);
            if (StringUtils.isNotBlank(cartTrackId)) {
                return cartService.getCartByTrackId(cartTrackId);
            }
            return null;
        }
    }

    /**
     * 响应客户端把一个sku加入购物车
     * 一个商品加入购物车之前会检查库存，如果没库存了不允许增加
     * 如果没有购物车cookie跟踪就新建cookie，同时建立购物车，把商品放入购物车
     *
     * @param response
     * @param skuId
     * @return
     */
    @RequestMapping(value = "/cart/sku/add")
    public void addSkuToCart(HttpServletRequest request, HttpServletResponse response, Long skuId, Integer number) throws IOException {
        try {
            if (!skuService.isSkuUsable(skuId)) {
                new JsonResult(false, "商品已下架或被移除").addData("errorType", "prodNotExist").toJson(response);
                return;
            }
            StockKeepingUnit stockKeepingUnit;
            ProductStorage concretionStorage = skuStorageService.getConcretionStorage(skuId);

            if (concretionStorage != null) {
                stockKeepingUnit = skuService.getStockKeepingUnitWithStock(skuId, concretionStorage.getId());
            } else {
                stockKeepingUnit = skuService.getStockKeepingUnit(skuId);
            }

            if (number < 1) {
                new JsonResult(false, "购买数量不能小于1").addData("errorType", "exceedMinNum").toJson(response);
                return;
            }

            Cart cart = getCart(request);
            int addNumber = 0;
            if (cart != null) {
                List<CartItem> cartItemList = cart.getCartItemList();
                for (CartItem cartItem : cartItemList) {
                    if (cartItem.getSkuId() == skuId) {
                        addNumber = number + cartItem.getNumber();
                        break;
                    }
                }
            }
            if (addNumber > stockKeepingUnit.getTradeMaxNumber()) {
                new JsonResult(false, "超过最大购买数量</br>购物车中已有" + (addNumber - number) + "件该商品").addData("errorType", "exceedMaxNum").toJson(response);
                return;
            }

            if (stockKeepingUnit.getStockQuantity() <= 0) {
                new JsonResult(false, "已经售完").addData("errorType", "sellOut").toJson(response);
                return;
            }

            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            if (null != sessionUserInfo) {
                useUserId(sessionUserInfo.getId(), skuId, number);
            } else {
                useTrackId(request, skuId, number);
            }
            new JsonResult(true).addData("cartUrl", urlBrokerFactory.getUrl("Cart").toString()).toJson(response);
        } catch (final Exception e) {
            logger.error("sku[" + skuId + "]数量为[" + number + "]添加购物车时出现异常:", e);
            new JsonResult(false, "加入购物车时服务器发生异常").addData("errorType", "systemError").toJson(response);
        }
    }


    /**
     * 从商品加入购物车，需要判断sku数量
     * 如果是单sku就可以直接加，如是多个就返回skumap
     *
     * @param request
     * @param response
     * @param productId
     * @throws IOException
     */
    @RequestMapping(value = "/cart/product/add")
    public void addProductToCart(HttpServletRequest request, HttpServletResponse response, Integer productId) throws IOException {
        if (productId == null) {
            new JsonResult(false, "没有这个商品").toJson(response);
            return;
        }
        Product product = productService.buildOpenProduct(productId);
        if (product == null) {
            new JsonResult(false, "没有这个商品").toJson(response);
            return;
        }
        if (!product.isOnline()) {
            new JsonResult(false, "该商品已下架").toJson(response);
            return;
        }
        if (product.isSingleSku()) {
            addSkuToCart(request, response, product.getDefaultSkuObject().getId(), 1);
        } else {
            new JsonResult(true).addData("canAdd", false).addData("skuMap", product.getStockKeepingUnitMap()).addData("defaultSku", product.getDefaultSkuObject()).addData("skuUrl", urlBrokerFactory.getUrl("GetSkuPanel").addQueryData("productId", productId).toString()).toJson(response);
        }
    }


    /**
     * 渲染sku选择html
     *
     * @param productId
     * @param model
     * @return
     */
    @RequestMapping(value = "/cart/product/skuPic")
    public String renderSkuPic(Integer productId, Model model) {
        Product product = productService.buildOpenProduct(productId);
        model.addAttribute(RenderConstants.PRODUCT_CONTEXT_KEY, product);
        return "sku";
    }

    private Cart useUserId(int userId, long skuId, int number) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart == null) {
            cartService.initCartByUserId(userId, skuId, number);
        } else {
            cartService.addSkuToCart(skuId, cart, number);
        }
        //重新加载购物车
        cart = cartService.getCartByUserId(userId);
        return cart;

    }

    private Cart useTrackId(HttpServletRequest request, long skuId, int number) throws IOException {
        String trackId = CartTrackUtil.readOrWriteTrackId(request);
        Cart cart = cartService.getCartByTrackId(trackId);
        if (cart == null) {
            cartService.initCartByTrackId(trackId, skuId, number);
        } else {
            cartService.addSkuToCart(skuId, cart, number);
        }

        //重新加载购物车
        cart = cartService.getCartByTrackId(trackId);
        return cart;
    }


    /**
     * 管理购物车的时候删除某个SKU商品
     *
     * @param skuId
     */
    @RequestMapping(value = "/cart/deleteCartItem", method = RequestMethod.POST)
    public void deleteCartItem(HttpServletResponse response, Long skuId, Integer cartId) throws IOException {
        try {
            cartService.deleteCartItemBySkuIdAndCartId(skuId, cartId);
            Cart cart = cartService.getCart(cartId);
            new JsonResult(true).addData("totalPrice", cart.getTotalPrice()).addData("totalNumber",cart.getTotalNumber())
                    .addData("discountPrice",cart.getTotalDiscountPrice()).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    /**
     * 管理购物车的时候删除所有SKU商品
     *
     * @param cartId
     * @throws IOException
     */
    @RequestMapping(value = "/cart/deleteAllCartItem", method = RequestMethod.POST)
    public void deleteAllCartItem(HttpServletResponse response, Integer cartId) throws IOException {
        try {
            cartService.deleteCartItemByCartId(cartId);
            Cart cart = cartService.getCart(cartId);
            new JsonResult(true).addData("totalPrice", cart.getTotalPrice()).toJson(response);
        } catch (Exception e) {
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }

    /**
     * 修改某个SKU的数量，要更新它
     *
     * @param cartItem
     */
    @RequestMapping(value = "/cart/updateCartItem", method = RequestMethod.POST)
    public void updateCartItem(CartItem cartItem, HttpServletResponse response) throws IOException {
        cartService.updateCartItem(cartItem);
        Cart cart = cartService.getCart(cartItem.getCartId());
        new JsonResult(true).addData("totalPrice", cart.getTotalPrice()).addData("totalNumber",cart.getTotalNumber())
                .addData("discountPrice",cart.getTotalDiscountPrice()).toJson(response);
    }


    /**
     * 购物车结算之前检查商品项是否满足库存约束
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/cart/check")
    public void checkCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        if (sessionUserInfo != null) {
            Cart cart = cartService.getCartByUserId(sessionUserInfo.getId());
            if (cart != null) {
                List<CartItem> cartItemList = cart.getCartItemList();
                if (cartItemList.size() == 0) {
                    new JsonResult(false).addData("errorMsg", new LinkedList<String>() {{
                        add("购物车中没有商品");
                    }}).toJson(response);
                    return;
                }
                List<String> errorMsg = tradeViewAdaptor.checkTradeItem(cartItemList);
                if (errorMsg.size() > 0) {
                    new JsonResult(false).addData("errorMsg", errorMsg).toJson(response);
                    return;
                }
                new JsonResult(true).toJson(response);
                return;
            }
        }
        new JsonResult(false).addData("errorMsg", "没有购物车或者购物车过期").toJson(response);
    }


}
