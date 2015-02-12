package com.kariqu.tradecenter.client.impl;

import com.google.common.collect.Lists;
import com.kariqu.common.DateUtils;
import com.kariqu.common.encrypt.BCryptUtil;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.SkuPriceDetail;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.service.ProductActivityService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.client.TradeCenterBossClient;
import com.kariqu.tradecenter.client.TradeCenterSupplierClient;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.*;
import com.kariqu.tradecenter.service.*;
import com.kariqu.usercenter.domain.AccountType;
import com.kariqu.usercenter.domain.User;
import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.service.UserPointService;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Tiger
 * @version 1.0
 * @since 13-1-25 上午10:10
 */
public class TradeCenterClientBossImpl implements TradeCenterBossClient {

    private static final Logger LOGGER = Logger.getLogger(TradeCenterClientBossImpl.class);

    @Autowired
    private OrderWriteService orderWriteService;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private BackGoodsWriteService backGoodsWriteService;

    @Autowired
    private BackGoodsQueryService backGoodsQueryService;

    @Autowired
    private UserPointService userPointService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ProductActivityService productActivityService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    @Autowired
    private TradeCenterSupplierClient tradeCenterSupplierClient;

    @Autowired
    private UserService userService;

    @Autowired
    private SkuService skuService;

    @Override
    @Transactional
    public void updateLogisticsRedundancy(LogisticsRedundancy logisticsRedundancy) {
        logisticsService.updateLogisticsRedundancy(logisticsRedundancy);
    }

    @Override
    public Logistics queryLogisticsByOrderId(long orderId) {
        return logisticsService.getLogisticsByOrderId(orderId);
    }

    @Override
    @Transactional
    public void updateOrderInvoiceInfoRedundancy(long orderId, InvoiceInfo invoiceInfo) {
        orderWriteService.updateOrderInvoiceInfoRedundancy(orderId, invoiceInfo);
    }

    @Override
    @Transactional
    public void updateOrderDeliveryType(long orderId, DeliveryInfo.DeliveryType deliveryType) {
        orderWriteService.updateOrderDeliveryType(orderId, deliveryType);
    }

    @Override
    public void updateWaybillNumberByOrderId(long orderId, String waybillNumber) {
        logisticsService.updateWaybillNumberByOrderId(orderId, waybillNumber);
    }

    @Override
    @Transactional
    public void appendOrderMessage(OrderMessage orderMessage) {
        orderMessage.setUserType(OrderMessage.UserType.Server);
        orderWriteService.updateOrderMessage(orderMessage);
    }

    @Override
    @Transactional
    public void assignIntegralAndCoupon(User user, long point, String couponInfo) {
        if (point > 0) {
            // 送积分
            UserPoint userPoint = new UserPoint();
            userPoint.setUserId(user.getId());
            userPoint.setPoint(Math.abs(point));
            userPoint.setType(point > 0 ? UserPoint.PointType.InComing : UserPoint.PointType.OutComing);
            userPoint.setInOutComingType(UserPoint.InOutComingType.Order);
            userPoint.setDescription(point > 0 ? "赠送积分" : "扣减积分");
            userPointService.createUsePoint(userPoint);
        }
        if (StringUtils.isNotBlank(couponInfo)) {
            // 送现金券
            String[] coupon = couponInfo.split(",");

            int price, mini, expireDay;
            if (coupon.length < 3) {
                price = 5000;
                mini = 25000;
                expireDay = 30;
            } else {
                price = NumberUtils.toInt(coupon[0]) * 100;
                mini = NumberUtils.toInt(coupon[1]) * 100;
                expireDay = NumberUtils.toInt(coupon[2]);
            }

            Calendar calendar = Calendar.getInstance();
            Date today = DateUtils.parseDate(DateUtils.formatDate(calendar.getTime(), DateUtils.DateFormatType.SIMPLE_DATE_FORMAT_STR)
                    + " 00:00:01", DateUtils.DateFormatType.DATE_FORMAT_STR);
            calendar.add(Calendar.DAY_OF_YEAR, expireDay);
            Date month = DateUtils.parseDate(DateUtils.formatDate(calendar.getTime(), DateUtils.DateFormatType.SIMPLE_DATE_FORMAT_STR)
                    + " 23:23:59", DateUtils.DateFormatType.DATE_FORMAT_STR);

            Coupon cp = couponService.generateCoupon(price, mini, today, month);
            if (cp != null) {
                cp.setUserId(user.getId());
                couponService.createCoupon(cp);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public void approvalOrderOfPaySuccess(long orderId, String operator) throws OrderBaseException {
        orderWriteService.approvalOrderOfPaySuccess(orderId, operator);
    }

    @Override
    public int queryCountOfOrderWaitForApproval() {
        return orderQueryService.queryOrderCountByWaitApproval();
    }

    @Override
    public Page<Order> searchOrderByQuery(OrderQuery orderQuery) {
        return orderQueryService.searchOrderByQuery(orderQuery);
    }

    @Override
    public List<List<Order>> searchOrderListByQuery(OrderQuery orderQuery) {
        return orderQueryService.searchOrderListByQuery(orderQuery);
    }

    @Override
    public Date querySuccessDate(long orderId) {
        OrderStateHistory history = orderQueryService.queryHistoryByState(orderId, OrderState.Success);
        if (history != null)
            return history.getDate();

        return null;
    }

    @Override
    public Order queryOrderById(long orderId) {
        return orderQueryService.getDetailsOrder(orderId);
    }

    @Override
    public Order queryOrderByOrderNo(long orderNo) {
        return orderQueryService.getSimpleOrderByOrderNo(orderNo);
    }

    @Override
    public InvoiceInfo queryOrderInvoiceInfoRedundancy(long orderId) {
        return orderQueryService.queryOrderInvoiceInfoRedundancy(orderId);
    }

    @Override
    public List<OrderMessage> queryAllMessage(long orderId) {
        return orderQueryService.queryAllOrderMessage(orderId);
    }

    @Override
    public OrderMessage queryUserMessage(long orderId) {
        return orderQueryService.queryUserOrderMessage(orderId);
    }

    @Override
    public OrderMessage queryServerMessage(long orderId) {
        return orderQueryService.queryServerOrderMessage(orderId);
    }

    @Override
    public List<ProgressDetail> getProgressDetail(long orderId) {
        return orderQueryService.queryProgressDetail(orderId, 0);
    }

    // =============== 退货 ===============
    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void agreeDirectRefund(long backId, String userName, String remark) throws BackGoodsBaseException {
        backGoodsWriteService.verifyNoSendBackGoods(backId, userName, remark);
    }

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void agreeHasSendRefund(long backId, String userName, String remark) throws BackGoodsBaseException {
        backGoodsWriteService.verifyYetSendBackGoods(backId, userName, remark);
    }

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void confirmBackGoodHasArrivalAtEJS(long backId, String userName, String expressNo) throws BackGoodsBaseException {
        backGoodsWriteService.receiveGoods(backId, userName, expressNo);
    }

    @Override
    @Transactional(rollbackFor = BackGoodsTransactionalException.class)
    public void cancelBackGoods(long backId, String userName, String remark) throws BackGoodsBaseException {
        backGoodsWriteService.cancelBackGoodsForCustomerServiceStaff(backId, userName, remark);
    }

    @Override
    @Transactional
    public List<BackGoods> triggerRefundSuccess(String batchNo, String successNum, String resultDetails) {
        // 操作交易
        List<Long> backIdList = tradeService.triggerRefundSuccess(batchNo, successNum, resultDetails);

        List<BackGoods> backGoodsList = new ArrayList<BackGoods>();
        for (Long backGoodsId : backIdList) {
            // 下面两个地方有异常不能影响上面的交易
            try {
                // 更新退货单.
                BackGoods backGoods = backGoodsWriteService.refundsGoodsForFinance(backGoodsId, "财务");

                // 更新 订单项 及 订单状态.
                orderWriteService.triggerOperaOrderWhenBackSuccess(backGoods);

                // 发送短信
                backGoodsList.add(backGoods);
            } catch (BackGoodsBaseException e) {
                LOGGER.error("退货单[" + backGoodsId + "]在退款成功后, 更新 [退货单]状态 时异常: (" + e.getMessage() + ")");
            } catch (OrderBaseException e) {
                LOGGER.error("退货单[" + backGoodsId + "]在退款成功后, 更新 [订单]状态 时异常: (" + e.getMessage() + ")");
            } catch (Exception e) {
                LOGGER.error("退货单[" + backGoodsId + "]退款成功后, 更新 [退货单] 及 [订单]状态 时异常: (" + e.getMessage() + ")");
            }
        }
        return backGoodsList;
    }

    @Override
    public List<BackGoodsItem> queryBackGoodItemsByBackGoodsId(long backGoodsId) {
        return backGoodsQueryService.queryBackGoodsItemByBackGoodsId(backGoodsId);
    }

    @Override
    public BackGoods queryBackGoodsById(long backId) {
        return backGoodsQueryService.queryBackGoodsById(backId);
    }

    @Override
    public int queryBackGoodsCountForWaitingToAudit() {
        return backGoodsQueryService.queryBackGoodsCountForWaitingToAudit();
    }

    @Override
    public List<BackGoods> queryBackGoodsForWaitingToAudit() {
        return backGoodsQueryService.queryBackGoodsByState(BackGoodsState.Create);
    }

    @Override
    public List<BackGoods> queryBackGoodsWithBackGoodsState(BackGoodsState backState) {
        return backGoodsQueryService.queryBackGoodsByState(backState);
    }

    @Override
    public int queryWaitPayBackGoodsCountForFinance() {
        return backGoodsQueryService.queryBackGoodsCountForFinance();
    }

    @Override
    public List<BackGoods> queryWaitPayBackGoodsForFinance() {
        return backGoodsQueryService.queryBackGoodsForFinance();
    }

    @Override
    public List<BackGoods> queryPayBackGoodsForFinance() {
        return queryBackGoodsWithBackGoodsState(BackGoodsState.Success);
    }

    @Override
    public LogisticsRedundancy queryLogisticsRedundancy(long logisticsId) {
        return logisticsService.queryLogisticsRedundancy(logisticsId);
    }

    // ============================ common ============================
    @Override
    public List<OrderItem> queryOrderItemWithoutBackingNumberByOrderId(long orderId) {
        return orderQueryService.queryRealOrderItemNumberByOrderId(orderId);
    }


    private static final String PASSWORD = BCryptUtil.encryptPassword("x123456");
    private static final String REGISTER_DATE = DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR);

    @Override
    @Transactional
    public void automaticOrder(String order) throws OrderBaseException {
        String[] or = order.split(",");
        if (or.length != 8) {
            throw new OrderNoTransactionalException("长度要有 8 位! 用户名,sku|sku:数量,收货人姓名,电话号码,收货省,市,区,地址");
        }

        SubmitOrderInfo submitOrderInfo = new SubmitOrderInfo();
        String userName = or[0].trim(), skuAndNum = or[1].trim(), name = or[2].trim(), phone = or[3].trim(),
                province = or[4].trim(), city = or[5].trim(), area = or[6].trim(), add = or[7].trim();

        List<TradeItem> tradeItems = Lists.newArrayList();
        for (String skuNum : skuAndNum.split("\\|")) {
            if (StringUtils.isBlank(skuNum)) continue;

            String[] sn = skuNum.split(":");
            if (sn.length == 0) throw new OrderTransactionalException("sku 及购买数量没设置对!");

            TradeItem tradeItem = new TradeItem();
            long skuId = NumberUtils.toLong(sn[0].trim());
            int number = 1;
            if (sn.length > 1)
                number = NumberUtils.toInt(sn[1].trim());
            if (skuId > 0) {
                tradeItem.setSkuId(skuId);
                tradeItem.setNumber(number == 0 ? 1 : number);

                tradeItem.setTradePriceStrategy(new TradePriceStrategy() {
                    @Override
                    public void apply(OrderItem orderItem) {
                        StockKeepingUnit sku = skuService.getStockKeepingUnit(orderItem.getSkuId());
                        if (sku == null) {
                            throw new RuntimeException("没有此 sku : " + orderItem.getSkuId());
                        }
                        // 商品如果有参加活动, 则记录活动价格
                        SkuPriceDetail skuPriceDetail = productActivityService.getSkuMarketingPrice(sku);
                        // 如果有活动则使用活动价格
                        String price = skuPriceDetail.getSellPrice();
                        orderItem.setUnitPrice(com.kariqu.common.lib.Money.YuanToCent(price));
                        if (skuPriceDetail.getActivityType() != null) {
                            orderItem.setSkuMarketingId(skuPriceDetail.getMarketingId());
                            orderItem.setSkuActivityType(skuPriceDetail.getActivityType());
                        }
                    }
                });
            }
            tradeItems.add(tradeItem);
        }
        User user = userService.getUserByUserName(userName);
        if (user == null) {
            user = new User();
            user.setUserName(userName.trim());
            user.setPassword(PASSWORD);
            user.setRegisterDate(REGISTER_DATE);
            user.setRegisterIP("127.0.0.1");
            user.setActive(true);
            userService.createUser(user);
        }

        Address address = new Address();
        address.setUserId(user.getId());
        address.setMobile(phone);
        address.setProvince(province + "," + city + "," + area);
        address.setDefaultAddress(true);
        address.setName(name);
        address.setLocation(add);
        address.setZipCode("");
        addressService.createAddress(address);
        Logistics logistics = new Logistics();
        logistics.injectBackUpAddress(address);
        logistics.setDeliveryInfo(new DeliveryInfo());
        logistics.setAddressId(address.getId());
        logistics.setAddressOwner(userName);

        submitOrderInfo.setLogistics(logistics);
        submitOrderInfo.setPayBank(PayBank.Alipay);
        submitOrderInfo.setPayType(PayType.OnLine);
        submitOrderInfo.setTradeItemList(tradeItems);
        submitOrderInfo.setAccountType(AccountType.KRQ);
        submitOrderInfo.setUser(user.getId());
        submitOrderInfo.setUserName(userName);
        InvoiceInfo invoiceInfo = new InvoiceInfo();
        invoiceInfo.setCompanyName(StringUtils.EMPTY);
        submitOrderInfo.setInvoiceInfo(invoiceInfo);

        List<Order> orderList = tradeCenterUserClient.submitOrder(submitOrderInfo);
        for (Order orderInfo : orderList) {
            orderWriteService.brushOrder(orderInfo.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = OrderTransactionalException.class)
    public int confirmNotTrueOrder(int day, int limit, String userNameRegex) throws OrderBaseException {
        List<Long> orderList = orderQueryService.queryOrderByCanConfirm(day, limit, userNameRegex);
        int count = 0;
        for (Long orderId : orderList) {
            approvalOrderOfPaySuccess(orderId, "客服");
            tradeCenterSupplierClient.printOrderForSupplier(orderId, "ejushang");
            tradeCenterSupplierClient.validateOrderForSupplier(orderId, "ejushang");
            tradeCenterSupplierClient.deliveryOrderForSupplier(orderId, "ejushang");
            tradeCenterUserClient.confirmOrderSuccess(orderId);
            count++;
        }
        return count;
    }

}
