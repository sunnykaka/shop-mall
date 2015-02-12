package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.Cart;
import com.kariqu.tradecenter.domain.CartItem;
import com.kariqu.tradecenter.repository.CartRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-10-13
 * Time: 上午10:02
 */
public class CartRepositoryImpl extends SqlMapClientDaoSupport implements CartRepository {

    @Override
    public void createCart(Cart cart) {
        getSqlMapClientTemplate().insert("insertCart", cart);
    }

    @Override
    public void updateCart(Cart cart) {
        getSqlMapClientTemplate().update("updateCart", cart);
    }

    @Override
    public void deleteCart(long cartId) {
        getSqlMapClientTemplate().delete("deleteCart", cartId);
        deleteCartItemByCartId(cartId);
    }

    @Override
    public Cart getCart(long cartId) {
        return (Cart) getSqlMapClientTemplate().queryForObject("selectCart", cartId);
    }

    @Override
    public Cart getCartByTrackId(String trackId) {
        return (Cart) getSqlMapClientTemplate().queryForObject("selectCartByTrackId", trackId);
    }

    @Override
    public Cart getCartByUserId(int userId) {
        return (Cart) getSqlMapClientTemplate().queryForObject("selectCartByUserId", userId);

    }

    @Override
    public void createCartItem(CartItem cartItem) {
        getSqlMapClientTemplate().insert("insertCartItem", cartItem);
    }

    @Override
    public void updateCartItem(CartItem cartItem) {
        getSqlMapClientTemplate().update("updateCartItem", cartItem);
    }

    @Override
    public void deleteCartItemBySkuIdAndCartId(long skuId,long cartId){
        Map map = new HashMap();
        map.put("skuId",skuId);
        map.put("cartId",cartId);
        getSqlMapClientTemplate().delete("deleteCartItemBySkuIdAndCartId", map);
    }

    @Override
    public void deleteCartItemByCartId(long cartId) {
        getSqlMapClientTemplate().delete("deleteCartItemByCartId", cartId);
    }

    @Override
    public List<CartItem> queryCarItemsByCartId(long cartId) {
        return getSqlMapClientTemplate().queryForList("selectCartItemByCartId",cartId);
    }

}
