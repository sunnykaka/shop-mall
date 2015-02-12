package com.kariqu.buyer.web.helper;

import com.kariqu.tradecenter.domain.Order;

/**
 * 订单的显示适配类
 * User: Asion
 * Date: 12-6-5
 * Time: 下午1:06
 */
public class OrderView {

    /**
     * 订单领域模型
     */
    private Order order;

    private String customer;

    private String storeName;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
