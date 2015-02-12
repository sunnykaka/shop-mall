package com.kariqu.tradecenter.service.impl;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.domain.SkuPriceDetail;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.ProductActivityService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.tradecenter.domain.Cart;
import com.kariqu.tradecenter.domain.CartItem;
import com.kariqu.tradecenter.repository.CartRepository;
import com.kariqu.tradecenter.service.CartService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 购物车服务
 * User: Asion
 * Date: 11-10-13
 * Time: 上午11:17
 */

public class CartServiceImpl implements CartService {
    protected final Log logger = LogFactory.getLog(CartServiceImpl.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ProductActivityService productActivityService;

    /**
     * 向购物车中加入商品
     *
     * @param skuId
     * @param cart
     */
    @Transactional
    public void addSkuToCart(long skuId, Cart cart, int number) {
        List<CartItem> cartItemList = cart.getCartItemList();
        boolean hasAdded = false;
        //判断购物车中是否加入过这个商品，如果加过则只改数量
        for (CartItem cartItem : cartItemList) {
            if (cartItem.getSkuId() == skuId) {
                cartItem.setNumber(cartItem.getNumber() + number);
                this.updateCartItem(cartItem);
                hasAdded = true;
                break;
            }
        }
        if (!hasAdded) {
            CartItem cartItem = new CartItem();
            cartItem.setSkuId(skuId);
            cartItem.setCartId(cart.getId());
            cartItem.setNumber(number);
            this.createCartItem(cartItem);
        }
    }

    @Override
    public void initCartByTrackId(String trackString, long skuId, int number) {
        Cart cart = new Cart(trackString);
        this.createCart(cart);
        CartItem cartItem = new CartItem();
        cartItem.setSkuId(skuId);
        cartItem.setCartId(cart.getId());
        cartItem.setNumber(number);
        this.createCartItem(cartItem);
    }

    @Override
    public void initCartByUserId(int userId, long skuId, int number) {
        Cart cart = new Cart(userId);
        this.createCart(cart);
        CartItem cartItem = new CartItem();
        cartItem.setSkuId(skuId);
        cartItem.setCartId(cart.getId());
        cartItem.setNumber(number);
        this.createCartItem(cartItem);
    }


    @Override
    @Transactional
    public void updateCart(Cart cart) {
        cartRepository.updateCart(cart);
    }


    @Override
    @Transactional
    public void createCart(Cart cart) {
        cartRepository.createCart(cart);
    }

    @Override
    @Transactional
    public void deleteCart(long cartId) {
        cartRepository.deleteCart(cartId);
    }

    @Override
    public Cart getCart(long cartId) {
        Cart cart = cartRepository.getCart(cartId);
        return buildCart(cart);
    }


    @Override
    public Cart getCartByTrackId(String trackId) {
        return buildCart(cartRepository.getCartByTrackId(trackId));
    }

    @Override
    public Cart getCartByUserId(int userId) {
        return buildCart(cartRepository.getCartByUserId(userId));
    }

    /**
     * 将Cart对象构建完整
     *
     * @param cart
     * @return
     */
    private Cart buildCart(Cart cart) {
        if (cart != null) {
            List<CartItem> cartItems = cartRepository.queryCarItemsByCartId(cart.getId());
            cart.setCartItemList(cartItems);
            //合计价格
            long cent = 0;
            long discountPrice = 0; //促销活动优惠的价格
            int number = 0; //购买的商品总数
            for (CartItem cartItem : cartItems) {
                StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnit(cartItem.getSkuId());
                if (stockKeepingUnit != null) {
                    SkuPriceDetail skuPriceDetail = productActivityService.getSkuMarketingPrice(stockKeepingUnit);
                    discountPrice = discountPrice + new Money(skuPriceDetail.getPriceDifference()).multiply(cartItem.getNumber()).getCent();
                    cent = cent + new Money(skuPriceDetail.getSellPrice()).multiply(cartItem.getNumber()).getCent();
                    number = number + cartItem.getNumber();
                } else {
                    logger.warn("构建购物车时发现sku被删除" + cartItem.getSkuId());
                    cartRepository.deleteCartItemBySkuIdAndCartId(cartItem.getSkuId(), cartItem.getCartId());
                }
            }

            cart.setTotalPrice(Money.getMoneyString(cent));
            cart.setTotalNumber(number);
            cart.setTotalDiscountPrice(Money.getMoneyString(discountPrice));
        }
        return cart;
    }

    @Override
    @Transactional
    public void createCartItem(CartItem cartItem) {
        cartRepository.createCartItem(cartItem);
    }

    @Override
    @Transactional
    public void updateCartItem(CartItem cartItem) {
        cartRepository.updateCartItem(cartItem);
    }

    @Override
    @Transactional
    public void deleteCartItemBySkuIdAndCartId(long skuId, long cartId) {
        cartRepository.deleteCartItemBySkuIdAndCartId(skuId, cartId);
    }

    @Override
    @Transactional
    public void deleteCartItemByCartId(long cartId) {
        cartRepository.deleteCartItemByCartId(cartId);
    }


}
