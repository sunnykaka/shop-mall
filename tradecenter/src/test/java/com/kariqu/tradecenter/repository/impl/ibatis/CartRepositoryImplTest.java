package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.Cart;
import com.kariqu.tradecenter.domain.CartItem;
import com.kariqu.tradecenter.repository.CartRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-10-13
 * Time: 上午10:41
 */
@SpringApplicationContext({"classpath:tradeContext.xml"})
public class CartRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("cartRepository")
    private CartRepository cartRepository;

    @Test
    public void testCartRepository() {
        Cart cart = new Cart();
        cart.setTrackId("gegwegweg52523523532532532fwegwegwegwegwg");
        cart.setUserId(2);
        cartRepository.createCart(cart);
        cart.setUserId(3);
        cartRepository.updateCart(cart);
        assertEquals(3,cartRepository.getCart(cart.getId()).getUserId());
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cart.getId());
        cartItem.setNumber(22);
        cartItem.setSkuId(1l);
        cartRepository.createCartItem(cartItem);
        cartItem.setSkuId(2l);
        cartRepository.createCartItem(cartItem);
        assertEquals(2, cartRepository.queryCarItemsByCartId(cart.getId()).size());
        assertEquals(cart.getTrackId(), cartRepository.getCart(cart.getId()).getTrackId());
        assertEquals(cart.getUserId(), cartRepository.getCart(cart.getId()).getUserId());
        assertEquals(cart.getTrackId(), cartRepository.getCartByUserId(cart.getUserId()).getTrackId());
        assertEquals(cart.getUserId(), cartRepository.getCart(cart.getId()).getUserId());
        assertEquals(cart.getTrackId(), cartRepository.getCartByTrackId(cart.getTrackId()).getTrackId());
        assertEquals(cart.getUserId(), cartRepository.getCart(cart.getId()).getUserId());

        CartItem cartItemForUpdate = new CartItem();
        cartItemForUpdate.setSkuId(1l);
        cartItemForUpdate.setCartId(cart.getId());
        cartItemForUpdate.setNumber(333);
        cartRepository.updateCartItem(cartItemForUpdate);
        cartRepository.deleteCartItemBySkuIdAndCartId(1l,cart.getId());
        cartRepository.deleteCart(cart.getId());
        cartRepository.deleteCartItemByCartId(cart.getId());
    }
}
