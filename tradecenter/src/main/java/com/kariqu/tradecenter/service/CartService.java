package com.kariqu.tradecenter.service;

import com.kariqu.tradecenter.domain.Cart;
import com.kariqu.tradecenter.domain.CartItem;

/**
 * User: Asion
 * Date: 11-10-11
 * Time: 上午11:28
 */
public interface CartService {

    void addSkuToCart(long skuId, Cart cart, int number);

    /**
     * 初始化购物车，一般是在用户点击某个商品的时候调用
     *
     * @param trackString 跟踪ID
     * @param skuId       商品
     */
    void initCartByTrackId(String trackString, long skuId, int number);

    void initCartByUserId(int userId, long skuId, int number);


    void createCart(Cart cart);

    void updateCart(Cart cart);

    void deleteCart(long cartId);

    //获取购物车
    Cart getCart(long cartId);

    Cart getCartByTrackId(String trackId);

    Cart getCartByUserId(int userId);


    void createCartItem(CartItem cartItem);

    void updateCartItem(CartItem cartItem);

    void deleteCartItemBySkuIdAndCartId(long skuId,long cartId);

    void deleteCartItemByCartId(long cartId);


}
