package com.kariqu.tradecenter.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 购物车对象
 * 车中是SKU对象，比如红色XL,绿色XLL，对应不同的购物记录
 * 购物车可以在用户没有登录的时候创建，跟踪这个购物车使用客户端的cookie，当用户登陆之后会合并到用户的ID进行跟踪
 * User: Asion
 * Date: 11-10-11
 * Time: 上午9:43
 */
public class Cart {

    private long id;

    //跟踪，比如用cookie
    private String trackId = "";

    //谁的购物车
    private int userId;

    //创建时间，配合客户端的cookie过期时间可以用于清除无用数据
    private Date createDate = new Date();

    //商品条目
    private List<CartItem> cartItemList;

    //购物车的总价，在加载车的时候动态计算，不需要入库
    private String totalPrice;

    //购物车的总商品数，在加载车的时候动态计算，不需要入库
    private int totalNumber;

    //购物车的总促销活动优惠的价格
    private String totalDiscountPrice;

    public Cart() {
    }

    public Cart(int userId) {
        this.userId = userId;
    }

    public Cart(String trackId) {
        this.trackId = trackId;
    }

    public List<OrderItem> convertToOrderItemList() {
        if (cartItemList != null) {
            List<OrderItem> orderItemList = new ArrayList<OrderItem>(cartItemList.size());
            for (CartItem cartItem : cartItemList) {
                OrderItem orderItem = new OrderItem();
                orderItem.setSkuId(cartItem.getSkuId());
                orderItem.setNumber(cartItem.getNumber());
                orderItemList.add(orderItem);
            }
            return orderItemList;
        } else {
            return Collections.EMPTY_LIST;
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getTotalDiscountPrice() {
        return totalDiscountPrice;
    }

    public void setTotalDiscountPrice(String totalDiscountPrice) {
        this.totalDiscountPrice = totalDiscountPrice;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
