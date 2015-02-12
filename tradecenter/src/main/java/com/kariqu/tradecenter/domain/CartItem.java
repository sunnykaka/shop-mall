package com.kariqu.tradecenter.domain;

/**
 * 购买条目，一个sku一个对应的物理商品
 * 表示一个购物项，如果没有cartId，则可能是直接购买
 * User: Asion
 * Date: 11-10-11
 * Time: 上午9:49
 */
public class CartItem extends TradeItem {

    private long cartId;


    public long getCartId() {
        return cartId;
    }

    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

}
