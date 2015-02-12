package com.kariqu.tradecenter.helper;

import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.tradecenter.excepiton.TradeFailException;
import com.kariqu.usercenter.domain.AccountType;
import com.kariqu.usercenter.domain.User;

import java.util.List;

/**
 * User: Asion
 * Date: 13-5-30
 * Time: 下午5:23
 */
public class TradeObjectFactory {

    /**
     * 工厂创建orderItem
     *
     * @param tradeItem
     * @return
     */
    public static OrderItem createOrderItem(TradeItem tradeItem) throws OrderNoTransactionalException {
        if (tradeItem.getNumber() <= 0) {
            throw new OrderNoTransactionalException("商品的购买数量有误!");
        }
        // 交易价格策略一定不能为空
        if (tradeItem.getTradePriceStrategy() == null) {
            throw new TradeFailException("没有设置订单项交易价格和活动数据的策略对象");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setSkuId(tradeItem.getSkuId());
        orderItem.setNumber(tradeItem.getNumber());

        // 执行策略, 也是在创建 orderItem 属性
        tradeItem.getTradePriceStrategy().apply(orderItem);

        return orderItem;
    }

    /**
     * 创建订单对象
     */
    public static Order createOrder(User user, List<OrderItem> orderItemList, ProductStorage storage,
                                    Supplier supplier, Logistics logistics, InvoiceInfo invoiceInfo,
                                    PayType payType, Coupon coupon, PayBank payBank,
                                    long integral, int orderListSize, double integralPercent) throws OrderNoTransactionalException {
        if (storage == null) {
            throw new TradeFailException("创建订单时找不到仓库信息");
        }
        if (supplier == null) {
            throw new TradeFailException("创建订单时找不到商家信息");
        }

        Order order = new Order();

        // 仓库信息
        order.setStorageId(storage.getId());
        order.setStorageName(storage.getName());
        // 商家信息
        order.setCustomerId(supplier.getId());
        order.setSupplierName(supplier.getName());

        order.setOrderItemList(orderItemList);

        order.setUserId(user.getId());
        order.setUserName(user.getUserName());
        AccountType accountType = (user.getAccountType() == null) ? AccountType.KRQ : user.getAccountType();
        order.setAccountType(accountType);

        order.setInvoiceInfo(invoiceInfo);
        order.setLogistics(logistics);
        order.setPayBank(payBank);
        order.setPayType(payType);
        order.setDeliveryType(logistics.getDeliveryInfo().getDeliveryType());

        order.setMilliDate(System.currentTimeMillis());
        order.setCoupon(coupon);

        order.calculateTotalPrice(coupon, integral, orderListSize);

        // 积分返回比例
        order.setIntegralPercent(integralPercent);

        return order;
    }

}
