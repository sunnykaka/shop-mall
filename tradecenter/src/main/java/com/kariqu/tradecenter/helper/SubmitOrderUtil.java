package com.kariqu.tradecenter.helper;

import com.kariqu.productcenter.domain.Money;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.domain.ProductStorage;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.service.SupplierService;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.OrderNoTransactionalException;
import com.kariqu.tradecenter.service.CouponService;
import com.kariqu.tradecenter.service.IntegralService;
import com.kariqu.usercenter.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * 准备提交订单的数据工具类
 *
 * @author Athens(刘杰)
 */
public class SubmitOrderUtil {

    private static final Log LOGGER = LogFactory.getLog(SubmitOrderUtil.class);

    /**
     * 检查积分和现金券, 并计算整个价格(先现金券, 后积分)
     *
     * @param oldTotalPrice 原价(整个订单项总价, 而非单个订单的所有订单项)
     * @param coupon        现金券
     * @param integral      积分分
     * @param user          用户
     * @return 新价格
     * @throws OrderNoTransactionalException
     */
    public static long reducePriceForCouponLaterIntegral(long oldTotalPrice, Coupon coupon, long integral, User user,
                                                         CouponService couponService, IntegralService integralService) throws OrderNoTransactionalException {
        long newTotalPrice = couponService.reducePriceForCoupon(coupon, oldTotalPrice);
        // 当使用 某类特殊的(联动优势等) 现金券, 导致价格为 0 或负数时, 则不需要再计算积分了
        if ((newTotalPrice <= 0)) {
            if (coupon != null) {
                LOGGER.warn("用户(" + user.getUserName() + ")使用完优惠券(" + coupon.getCode() + ")后(优惠券金额是:"
                        + Money.getMoneyString(coupon.getPrice()) + ")出现非正数! 此时的订单总价是:"
                        + Money.getMoneyString(oldTotalPrice) + ". PS: 还未生成订单, 无法记录订单号");
            }

            newTotalPrice = 0;
        } else {
            newTotalPrice = integralService.reducePriceForIntegral(integral, user, newTotalPrice);
            // 计算完积分后若结果小于等于 0 则订单总价也是 0
            if (newTotalPrice < 0) {
                newTotalPrice = 0;
            }
        }
        return newTotalPrice;
    }

    /**
     * 分摊订单项上的总价. <span style="color:red;">分摊是基于单个订单项而非拆分好的订单!</span><br/>
     * 多个订单项组成订单, 如果仓库不同, 还可以拆分成多个订单, 积分和优惠券作用在整个订单项而非单个订单上<br/>
     * 订单拆分后, 并平摊回现金券和积分后, 为了保证 显示订单、付款、取消及退货 时数据正确
     *
     * @param oldItemTotal    原价格
     * @param newItemTotal    新价格
     * @param orderItemList   订单项
     * @param coupon          优惠券
     * @param integral        积分 最小积分单位
     * @param integralService 积分服务类
     * @throws OrderNoTransactionalException
     */
    public static void equallyOrderItem(long oldItemTotal, long newItemTotal, List<OrderItem> orderItemList, Coupon coupon,
                                        long integral, IntegralService integralService) throws OrderNoTransactionalException {
        // 若价格未发生变化, 则不需要分摊
        if (oldItemTotal <= newItemTotal) return;

        // 分摊后的优惠券价格(在订单项上记录是为了避免后面在订单上计算时出现回加时的误差)
        long couponApportion = 0, couponProbability = 0;
        // 分摊后的积分点数
        long integralApportion = 0, integralProbability = 0;
        // 分摊后的订单项价格, 计算方式: 订单项价格(单价乘以数量) - 优惠券对应的价格 - 积分对应的价格
        long itemTotalPrice, probability;

        // 以 订单总价 从小到大排序
        Collections.sort(orderItemList);

        int i = 0;
        boolean hasZero = false;
        for (OrderItem orderItem : orderItemList) {
            if ((i + 1) == orderItemList.size()) {
                // 最后一个订单项 优惠券平摊数据优惠券价格减去前面的和
                if (coupon != null)
                    couponApportion = coupon.getPrice() - couponProbability;
                if (integral > 0)
                    integralApportion = integral - integralProbability;
            } else {
                // 订单项在整个订单列表中所占的比例
                probability = orderItem.totalPrice() * 100 / oldItemTotal;
                // 分摊到此订单项的优惠券价格
                if (coupon != null)
                    couponApportion = coupon.getPrice() * probability / 100;
                // 分摊到此订单项的积分点数
                if (integral > 0)
                    integralApportion = integral * probability / 100;

                couponProbability += couponApportion;
                integralProbability += integralApportion;
            }
            // 订单项平摊回的价格: 订单项价格(单价乘以数量) - 优惠券对应的价格 - 积分对应的价格
            itemTotalPrice = orderItem.totalPrice() - couponApportion - integralService.calculateIntegral(integralApportion);

            // 按比例来平摊, 如果付款价格过小, 会出现负数.
            if (itemTotalPrice < 0) {
                LOGGER.warn("订单项(" + orderItem + ")平摊时价格出现负数! 现金券信息(" + (coupon != null ? coupon : "无")
                        + "), 积分(" + com.kariqu.usercenter.domain.Currency.IntegralToCurrency(integral) + "), 订单列(" + orderItemList + ")");
                hasZero = true;
                break;
            }
            // 记录处理后的总价
            orderItem.setCouponApportion(couponApportion);
            orderItem.setIntegralApportion(integralApportion);
            orderItem.setItemTotalPrice(itemTotalPrice);
            i++;
        }

        // 按照 比例(百分比) 来平摊价格时, 若需要付款的实际价格过小, 最后的订单项会出现负数
        // 此时, 保证 前面的订单项(非最后一个) 达到这一点: 单价 * 数量 = 平摊回的优惠券价格 + 积分对应的价格
        if (hasZero) {
            i = 0;
            couponApportion = 0;
            couponProbability = 0;
            integralApportion = 0;
            integralProbability = 0;

            // 如果条件允许, 尽量让每个订单项最少保持有 1 分钱
            int mischief = newItemTotal >= orderItemList.size() ? 1 : 0;

            for (OrderItem orderItem : orderItemList) {
                // 前面的订单项将其价格都置为 0
                if ((i + 1) == orderItemList.size()) {
                    // 最后一个订单项 优惠券平摊数据优惠券价格减去前面的和
                    if (coupon != null)
                        couponApportion = coupon.getPrice() - couponProbability;
                    if (integral > 0)
                        integralApportion = integral - integralProbability;
                } else {
                    // 订单项在整个订单列表中所占的比例
                    probability = orderItem.totalPrice() * 100 / oldItemTotal;
                    // 分摊到此订单项的优惠券价格
                    if (coupon != null)
                        couponApportion = coupon.getPrice() * probability / 100;
                    // 分摊到此订单项的积分点数
                    if (integral > 0)
                        integralApportion = orderItem.totalPrice() - couponApportion - mischief;
                    else
                        couponApportion = orderItem.totalPrice() - mischief;

                    couponProbability += couponApportion;
                    integralProbability += integralApportion;
                }
                // 订单项平摊回的价格: 订单项价格(单价乘以数量) - 优惠券对应的价格 - 积分对应的价格
                itemTotalPrice = orderItem.totalPrice() - couponApportion - integralService.calculateIntegral(integralApportion);

                if (itemTotalPrice < 0) {
                    if (orderItemList.size() > 1)
                        throw new OrderNoTransactionalException("使用现金券或积分时出现错误! 请您联系客服人员进行处理.");

                    // 如果只有一个订单项, 则将价格置为 0
                    integralApportion = integral;
                    couponApportion = orderItem.totalPrice() - integralService.calculateIntegral(integralApportion);
                    itemTotalPrice = 0;
                }
                // 记录处理后的总价
                orderItem.setCouponApportion(couponApportion);
                orderItem.setIntegralApportion(integralApportion);
                orderItem.setItemTotalPrice(itemTotalPrice);
                i++;
            }
        }
    }

    /**
     * 构建拆分订单.
     *
     * @param orderItemList   订单项
     * @param submitOrderInfo 构建订单时的相关数据
     * @param user            用户信息
     * @param integralPercent 交易成功后的积分返回比例
     * @return 拆分好的订单
     * @throws OrderNoTransactionalException
     */
    public static List<Order> buildSplitOrder(List<OrderItem> orderItemList, SubmitOrderInfo submitOrderInfo,
                                              User user, SupplierService supplierService,
                                              double integralPercent) throws OrderNoTransactionalException {
        //按照仓库将orderItem分组
        Map<ProductStorage, List<OrderItem>> orderItemsSplitMap = new HashMap<ProductStorage, List<OrderItem>>();

        for (OrderItem orderItem : orderItemList) {
            ProductStorage storage = supplierService.queryProductStorageById(orderItem.getStorageId());
            if (orderItemsSplitMap.get(storage) == null) {
                LinkedList<OrderItem> value = new LinkedList<OrderItem>();
                orderItemsSplitMap.put(storage, value);
                value.add(orderItem);
            } else {
                orderItemsSplitMap.get(storage).add(orderItem);
            }
        }

        List<Order> orderList = new LinkedList<Order>();

        //根据订单项分组构建Order
        for (Map.Entry<ProductStorage, List<OrderItem>> productStorageListEntry : orderItemsSplitMap.entrySet()) {
            ProductStorage productStorage = productStorageListEntry.getKey();
            // 商家Id 及 商家名
            Supplier supplier = supplierService.queryCustomerById(productStorage.getCustomerId());
            // 默认的物流信息
            DeliveryInfo deliveryInfo = new DeliveryInfo();
            if (supplier != null) {
                deliveryInfo.setDeliveryType(supplier.getDefaultLogistics());
            }

            Logistics logistics = submitOrderInfo.getLogistics();
            logistics.setDeliveryInfo(deliveryInfo);

            //创建订单
            Order order = TradeObjectFactory.createOrder(user, productStorageListEntry.getValue(), productStorage, supplier,
                    logistics, submitOrderInfo.getInvoiceInfo(), submitOrderInfo.getPayType(),
                    submitOrderInfo.getCoupon(), submitOrderInfo.getPayBank(),
                    submitOrderInfo.getIntegral(), orderItemsSplitMap.size(), integralPercent);
            orderList.add(order);
        }

        return orderList;
    }

}
