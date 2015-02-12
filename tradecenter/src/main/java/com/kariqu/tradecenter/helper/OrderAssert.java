package com.kariqu.tradecenter.helper;

import com.kariqu.tradecenter.domain.Order;
import com.kariqu.tradecenter.domain.OrderItem;
import com.kariqu.tradecenter.excepiton.TradeFailException;

/**
 * @author Athens(刘杰)
 * @Time 13-5-31 下午1:17
 */
public class OrderAssert {

    public static void assertOrder(Order order) {
        assertNumber(order.getUserId(), "创建订单时 [用户Id 为空!] " + order);
        assertStringNull(order.getUserName(), "创建订单时 [用户名为空!] " + order);
        assertNull(order.getAccountType(), "创建订单时 [账户类型为空!] " + order);
        assertNull(order.getPayType(), "创建订单时 [支付类型为空!] " + order);
        assertStringNull(order.getTotalPrice(), "创建订单时 [订单总价为空!] " + order);
        assertNull(order.getOrderState(), "创建订单时 [订单状态为空!] " + order);
    }

    public static void assertOrderItem(OrderItem orderItem, Order order) {
        assertNumber(orderItem.getOrderId(), "创建订单项时 [订单Id为空!] " + orderItem + ", 订单{" + order + "}");
        assertNumber(orderItem.getSkuId(), "创建订单项时 [skuId为空!] " + orderItem + ", 订单{" + order + "}");
        assertPrice(orderItem.getUnitPrice(), "创建订单项时 [订单项单价为空!] " + orderItem + ", 订单{" + order + "}");
        assertPrice(orderItem.getItemTotalPrice(), "创建订单项时 [订单项总价为空!] " + orderItem + ", 订单{" + order + "}");
        assertNumber(orderItem.getNumber(), "创建订单项时 [订单项数量为空!] " + orderItem + ", 订单{" + order + "}");
        assertNull(orderItem.getOrderState(), "创建订单项时 [订单项状态为空!] " + orderItem + ", 订单{" + order + "}");
    }

    private static void assertNumber(Number number, String message) {
        if (number.doubleValue() <= 0)
            fail(message);
    }

    private static void assertPrice(Number number, String message) {
        if (number.doubleValue() < 0)
            fail(message);
    }

    private static void assertNull(Object obj, String message) {
        if (obj == null)
            fail(message);
    }

    private static void assertStringNull(String filed, String message) {
        if (filed == null || "".equals(filed.trim()))
            fail(message);
    }

    private static void fail(String message) {
        throw new TradeFailException(message);
    }
}
