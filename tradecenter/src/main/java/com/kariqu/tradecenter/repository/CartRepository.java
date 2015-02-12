package com.kariqu.tradecenter.repository;

import com.kariqu.tradecenter.domain.Cart;
import com.kariqu.tradecenter.domain.CartItem;

import java.util.List;

/**
 * 管理购物车和购物项的仓库
 * User: Asion
 * Date: 11-10-11
 * Time: 上午11:28
 */
public interface CartRepository {

    void createCart(Cart cart);

    void updateCart(Cart cart);

    void deleteCart(long cartId);

    Cart getCart(long cartId);

    Cart getCartByTrackId(String trackId);

    Cart getCartByUserId(int userId);

    void createCartItem(CartItem cartItem);

    void updateCartItem(CartItem cartItem);

    void deleteCartItemBySkuIdAndCartId(long skuId,long cartId);

    void deleteCartItemByCartId(long cartId);

    List<CartItem> queryCarItemsByCartId(long cartId);

}
