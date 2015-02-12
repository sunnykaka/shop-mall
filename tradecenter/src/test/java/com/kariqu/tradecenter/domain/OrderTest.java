package com.kariqu.tradecenter.domain;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.helper.Utils;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Athens(刘杰)
 * @Time 2013-03-14 10:10
 * @since 1.0.0
 */
public class OrderTest extends UnitilsJUnit4 {

    @Test
    public void testCalculateTotalPrice() throws Exception {
        OrderItem oi1 = new OrderItem();
        oi1.setUnitPrice(174800);
        oi1.setNumber(1);
        oi1.setStorageId(100);
        oi1.setItemTotalPrice(oi1.getUnitPrice() * oi1.getNumber());

        OrderItem oi2 = new OrderItem();
        oi2.setUnitPrice(72800);
        oi2.setNumber(3);
        oi2.setStorageId(200);
        oi2.setItemTotalPrice(oi2.getUnitPrice() * oi2.getNumber());

        OrderItem oi3 = new OrderItem();
        oi3.setUnitPrice(27900);
        oi3.setNumber(11);
        oi3.setStorageId(200);
        oi3.setItemTotalPrice(oi3.getUnitPrice() * oi3.getNumber());

        OrderItem oi4 = new OrderItem();
        oi4.setUnitPrice(29600);
        oi4.setNumber(13);
        oi4.setStorageId(100);
        oi4.setItemTotalPrice(oi4.getUnitPrice() * oi4.getNumber());

        OrderItem oi5 = new OrderItem();
        oi5.setUnitPrice(49800);
        oi5.setNumber(7);
        oi5.setStorageId(100);
        oi5.setItemTotalPrice(oi5.getUnitPrice() * oi5.getNumber());

        OrderItem oi6 = new OrderItem();
        oi6.setUnitPrice(1);
        oi6.setNumber(10);
        oi6.setStorageId(100);
        oi6.setItemTotalPrice(oi6.getUnitPrice() * oi6.getNumber());

        List<OrderItem> orderItemList = new ArrayList<OrderItem>();
        orderItemList.add(oi1);
        orderItemList.add(oi2);
        orderItemList.add(oi3);
        orderItemList.add(oi4);
        orderItemList.add(oi5);
        orderItemList.add(oi6);

        Map<Integer, Order> orderMap = new LinkedHashMap<Integer, Order>();
        for (OrderItem orderItem : orderItemList) {
            Order order = orderMap.get(orderItem.getStorageId());
            if (order == null) {
                order = new Order();
                order.setId(orderItem.getStorageId());
                orderMap.put(orderItem.getStorageId(), order);
            }
            order.addOrderItem(orderItem);
        }

        System.out.println("更新价格前 >>");
        for (OrderItem orderItem : orderItemList) {
            System.out.println("仓库: " + orderItem.getStorageId() + ", 价格: " + Money.getMoneyString(orderItem.getItemTotalPrice()));
        }
        System.out.println();
        for (Map.Entry<Integer, Order> orderEntry : orderMap.entrySet()) {
            Order order = orderEntry.getValue();
            order.calculateTotalPrice();
            System.out.println("\t订单: " + order.getId());
            for (OrderItem orderItem : order.getOrderItemList()) {
                System.out.println("\t\t仓库: " + orderItem.getStorageId() + ", 价格: " + Money.getMoneyString(orderItem.getItemTotalPrice()));
            }
            System.out.println("\t订单总价: " + order.getTotalPrice() + "\n");
        }

        for (OrderItem orderItem : orderItemList) {
            orderItem.setItemTotalPrice(orderItem.getItemTotalPrice() - 4);
        }

        System.out.println("\n\n更新价格后 >>");
        for (OrderItem orderItem : orderItemList) {
            System.out.println("仓库: " + orderItem.getStorageId() + ", 价格: " + Money.getMoneyString(orderItem.getItemTotalPrice()));
        }
        System.out.println();
        for (Map.Entry<Integer, Order> orderEntry : orderMap.entrySet()) {
            Order order = orderEntry.getValue();
            order.calculateTotalPrice();
            System.out.println("\t订单: " + order.getId());
            for (OrderItem orderItem : order.getOrderItemList()) {
                System.out.println("\t\t仓库: " + orderItem.getStorageId() + ", 价格: " + Money.getMoneyString(orderItem.getItemTotalPrice()));
            }
            System.out.println("\t订单总价: " + order.getTotalPrice() + "\n");
        }

        Collections.sort(orderItemList);
        for (OrderItem orderItem : orderItemList) {
            orderItem.setItemTotalPrice(orderItem.getItemTotalPrice() - 4);
        }

        System.out.println("\n\n排序后更新价格 >>");
        for (OrderItem orderItem : orderItemList) {
            System.out.println("仓库: " + orderItem.getStorageId() + ", 价格: " + Money.getMoneyString(orderItem.getItemTotalPrice()));
        }

        System.out.println();
        for (Map.Entry<Integer, Order> orderEntry : orderMap.entrySet()) {
            Order order = orderEntry.getValue();
            order.calculateTotalPrice();
            System.out.println("\t订单: " + order.getId());
            for (OrderItem orderItem : order.getOrderItemList()) {
                System.out.println("\t\t仓库: " + orderItem.getStorageId() + ", 价格: " + Money.getMoneyString(orderItem.getItemTotalPrice()));
            }
            System.out.println("\t订单总价: " + order.getTotalPrice() + "\n");
        }
    }

}
