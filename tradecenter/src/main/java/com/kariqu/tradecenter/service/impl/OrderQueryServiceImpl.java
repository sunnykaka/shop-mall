package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.DateUtils;
import com.kariqu.common.json.JsonUtil;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.domain.payment.SkuTradeResult;
import com.kariqu.tradecenter.helper.Utils;
import com.kariqu.tradecenter.repository.OrderRepository;
import com.kariqu.tradecenter.repository.PaymentRepository;
import com.kariqu.tradecenter.repository.SkuTradeResultRepository;
import com.kariqu.tradecenter.service.*;
import com.kariqu.usercenter.domain.AccountType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-4
 *        Time: 下午2:41
 */
public class OrderQueryServiceImpl implements OrderQueryService {

    private final Log logger = LogFactory.getLog(OrderQueryServiceImpl.class);

    @Autowired
    private BackGoodsQueryService backGoodsQueryService;

    @Autowired
    private OperateLogisticsService operateLogisticsService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SkuTradeResultRepository skuTradeResultRepository;

    @Override
    public int queryCountForNoProcessOrder(int userId) {
        return orderRepository.queryCountForNoProcessOrder(userId);
    }

    @Override
    public int queryOrderCountByWaitApproval() {
        return orderRepository.queryOrderCountByWaitApproval();
    }

    @Override
    public List<Order> queryOrderByVirtualIdExcludeState(long virtualOrderId, OrderState orderState) {
        return orderRepository.queryOrderByVirtualIdExcludeState(virtualOrderId, orderState);
    }

    @Override
    public String getOrderPriceByOrderNo(long orderNo) {
        return orderRepository.getOrderPriceByOrderNo(orderNo);
    }

    @Override
    public Logistics getLogisticsByOrderId(long orderId) {
        return logisticsService.getLogisticsByOrderId(orderId);
    }

    @Override
    public Page<Order> searchOrderByQuery(OrderQuery orderQuery) {
        return orderRepository.searchOrderByQuery(orderQuery);
    }

    @Override
    public List<List<Order>> searchOrderListByQuery(OrderQuery orderQuery) {
        List<List<Order>> orders = orderRepository.searchOrderListByQuery(orderQuery);
        for (List<Order> orderList : orders) {
            for (Order order : orderList) {
                buildOrder(order);
            }
        }
        return orders;
    }

    @Override
    public Page<Order> searchQuery(Query query, int customerId, Page<Order> page) {
        Map map = accessMap(query, customerId);
        return orderRepository.searchQuery(map, page);
    }

    @Override
    public List<Order> searchQueryList(Query query, int customerId) {
        Map map = accessMap(query, customerId);
        return orderRepository.searchQueryList(map);
    }


    @Override
    public List<Long> queryNotPayOrder(int delay) {
        return orderRepository.queryNotPayOrder(delay);
    }

    @Override
    public List<Long> queryNotConfirmSuccessOrder(int delay) {
        return orderRepository.queryNotConfirmSuccessOrder(delay);
    }

    @Override
    public List<OrderStateHistory> queryUserModeOrderStateHistory(long orderId) {
        return orderRepository.queryUserModeOrderStateHistory(orderId);
    }


    @Override
    public List<Order> queryOrderByUserId(int userId, AccountType accountType) {
        List<Order> orders = orderRepository.queryOrderByUserId(userId, accountType);
        for (Order order : orders) {
            buildOrder(order);
        }
        return orders;
    }

    @Override
    public List<Order> queryOrderByCustomerId(int customerId, OrderState orderState) {
        return orderRepository.queryOrderByCustomerId(customerId, orderState);
    }

    /**
     * 加入订单商品、物流信息
     */
    @Override
    public Page<Order> queryOrderByUserIdPage(int userId, Integer orderState, Page<Order> page) {
        Page<Order> orderPage = orderRepository.queryOrderByUserIdPage(userId, orderState, page);
        for (Order order : orderPage.getResult()) {
            buildOrder(order);
            checkTotalPrice(order);
        }
        return page;
    }

    public Page<Order> getNotCancelOrderPageByUserId(int userId, Page<Order> page) {
        Page<Order> orderPage = orderRepository.getNotCancelOrderPageByUserId(userId, page);
        for (Order order : orderPage.getResult()) {
            buildOrder(order);
            checkTotalPrice(order);
        }
        return page;
    }

    /**
     * 按用户id查询申请退单的列表
     *
     * @param userId
     * @param page
     * @return
     */
    @Override
    public Page<Order> queryBackGoodsApplyByUserId(int userId, Page<Order> page) {
        Page<Order> orderPage = orderRepository.queryBackGoodsApplyByUserIdPage(userId, page);
        //存放此用户的不能退单的订单集合
        List<Order> removeOrderList = new ArrayList<Order>();
        for (Order order : orderPage.getResult()) {
            try {
//                检查此用户的订单是否能退款，不能退款则会抛出异常，则将其移除
                backGoodsQueryService.queryCanBackOrderItemsOfUser(order.getOrderNo(), userId);
                buildOrder(order);
                if (order.getOrderItemList() == null || order.getOrderItemList().isEmpty()) {
                    removeOrderList.add(order);
                    continue;
                }
                //得到此订单能退单的订单项
                List<OrderItem> orderItemList = orderRepository.queryCanBackOrderItemByOrderNo(order.getOrderNo());
                //存放此订单不能退单的订单项集合
                List<OrderItem> removeOrderItemList = new ArrayList<OrderItem>();
                for (OrderItem orderItem : order.getOrderItemList()) {
                    for (int i = 0; i < orderItemList.size(); i++) {
                        if (orderItem.getId() == orderItemList.get(i).getId()) {
                            continue;
                        }
                        if (i == orderItemList.size() - 1) {
                            removeOrderItemList.add(orderItem);
                        }
                    }
                }
                //将此订单不能退单的订单项移除
                order.getOrderItemList().removeAll(removeOrderItemList);
                checkTotalPrice(order);
            } catch (Exception e) {
                removeOrderList.add(order);
            }
        }
        orderPage.getResult().removeAll(removeOrderList);
        return page;
    }

    @Override
    public int queryOrderCountByUserIdAndState(int userId, Integer orderState) {
        return orderRepository.queryOrderCountByUserIdAndState(userId, orderState);
    }

    @Override
    public Map<String, Object> getValuationCountByUserIdAndAppraise(int userId, int appraise) {
        return orderRepository.queryValuationCountByUserId(userId, appraise);
    }

    @Override
    public List<OrderItem> queryCanBackOrderItemByOrderNo(long orderNo) {
        return orderRepository.queryCanBackOrderItemByOrderNo(orderNo);
    }

    @Override
    public Page<OrderItem> queryValuationByUserIdAndAppraise(int userId, int appraise, Page<OrderItem> page) {
        return orderRepository.queryWaitValuationByUserId(userId, appraise, page);
    }

    @Override
    public Page<Order> queryRecentOrderByUserId(int userId, Page<Order> page) {
        Page<Order> orderPage = orderRepository.queryRecentOrderByUserId(userId, page);
        for (Order order : orderPage.getResult()) {
            buildOrder(order);
        }
        return page;
    }

    @Override
    public int queryRecentOrderCountByUserId(int userId) {
        return orderRepository.queryRecentOrderCountByUserId(userId);
    }

    @Override
    public int queryWaitPayOrderCountByUserId(int userId) {
        return orderRepository.queryWaitPayOrderCountByUserId(userId);
    }

    @Override
    public Map<String, Object> queryOrderCountByUserIdAndOrderState(int userId, OrderState orderState) {
        return orderRepository.queryOrderCountByUserIdAndOrderState(userId, orderState);
    }

    /**
     * 核对订单价格
     *
     * @param order
     */
    private void checkTotalPrice(Order order) {
        long realPrice = new Money(order.getTotalPrice()).getCent();
        Money money = new Money();
        for (OrderItem orderItem : order.getOrderItemList()) {
            Money toAdd = new Money(orderItem.getSubtotalPrice());
            money = money.add(toAdd);
        }
        long totalPrice = money.getCent();
        //if (totalPrice != realPrice) {
        //    logger.warn("订单：" + order.getOrderNo() + "价格异常：记录总价为：" + realPrice + " 实际价格为：" + totalPrice);
        //}
    }

    @Override
    public List<SystemEvent> querySystemEvents(long orderId) {
        return orderRepository.querySystemEvents(orderId);
    }


    @Override
    public List<OrderItem> queryOrderItemsByOrderId(long orderId) {
        return orderRepository.queryOrderItemsByOrderId(orderId);
    }

    @Override
    public Logistics getLogistics(long id) {
        return logisticsService.getLogistics(id);
    }

    @Override
    public List<LogisticsEvent> queryLogisticsEvents(long logisticsId) {
        return logisticsService.queryLogisticsEvents(logisticsId);
    }

    @Override
    public List<PaymentEvent> queryPaymentEvents(long id) {
        return paymentRepository.queryPaymentEvents(id);
    }

    /**
     * 获取查询条件放入map
     *
     * @param query
     * @param customerId
     * @return
     */
    private Map accessMap(Query query, long customerId) {
        Map map = new HashMap();

        // 查询条件
        map.put(query.getQueryOption(), query.getQueryValue());
        // 排序
        map.put("sortValue", Utils.checkSqlValue(query.getSortValue()) ? "" : query.getSortValue());
        map.put("sortMode", Utils.checkSqlValue(query.getSortMode()) ? "" : query.getSortMode());

        if (StringUtils.isBlank(query.getStorageId())) {
            map.put("storageId", 0);
        } else {
            map.put("storageId", query.getStorageId());
        }
        map.put("customerId", customerId);
        map.put("orderState", query.getOrderState());
        map.put("deliveryType", query.getDeliveryType());

        //如果需要根据日期进行查询，则设置查询的类型
        if (searchWithDate(query)) {
            map.put("searchDateType", query.getDateType());
            //如果要根据日期条件查询，且结束日期为空，则设置结束日期为Now
            String endDate = StringUtils.isEmpty(query.getEndDate()) ? DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR) : query.getEndDate();
            map.put("startDate", query.getStartDate());
            map.put("endDate", endDate);
        }


        return map;
    }

    /**
     * 是否根据日期条件查询，也就是说至少选 了一个日期条件
     * 修改人：周美华，2013-10-14
     *
     * @param query
     * @return
     */
    private boolean searchWithDate(Query query) {
        return StringUtils.isNotEmpty(query.getStartDate()) || StringUtils.isNotEmpty(query.getEndDate());
    }

    @Override
    public OrderItem queryOrderItemsById(long orderItemId) {
        return orderRepository.queryOrderItemById(orderItemId);
    }

    @Override
    public LogisticsRedundancy queryLogisticsRedundancy(long id) {
        return logisticsService.queryLogisticsRedundancy(id);
    }

    @Override
    public InvoiceInfo queryOrderInvoiceInfoRedundancy(long orderId) {
        return orderRepository.queryOrderInvoiceInfoRedundancy(orderId);
    }

    @Override
    public List<OrderStateHistory> queryOrderStateHistoryDistinct(long orderId) {
        return orderRepository.queryOrderStateHistoryDistinct(orderId);
    }

    @Override
    public List<OrderItem> queryRealOrderItemNumberByOrderId(long orderId) {
        List<OrderItem> orderItems = orderRepository.queryOrderItemsByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            orderItem.setShipmentNum(orderItem.getNumber());
        }

        List<BackGoods> backGoodsList = backGoodsQueryService.queryBackGoodsByOrderId(orderId);
        if (backGoodsList != null && backGoodsList.size() != 0) {
            Map<Long, Integer> map = new HashMap<Long, Integer>();
            for (BackGoods backGoods : backGoodsList) {
                List<BackGoodsItem> backGoodsItems = backGoodsQueryService.queryBackGoodsItemByBackGoodsId(backGoods.getId());
                if (backGoodsItems != null && backGoodsItems.size() != 0) {
                    for (BackGoodsItem item : backGoodsItems) {
                        // 当存在多个 itemId 时则将数量进行累加(用于 orderItem 可以多次退货的场景)
                        if (map.containsKey(item.getOrderItemId()))
                            map.put(item.getOrderItemId(), map.get(item.getOrderItemId()) + item.getNumber());
                        else
                            map.put(item.getOrderItemId(), item.getNumber());
                    }
                }
            }

            for (OrderItem orderItem : orderItems) {
                if (map.containsKey(orderItem.getId())) {
                    // 实际发货数量
                    orderItem.setShipmentNum(orderItem.getNumber() - map.get(orderItem.getId()));
                    // 退货数量
                    orderItem.setBackNum(map.get(orderItem.getId()));
                }
            }
        }

        return orderItems;
    }

    @Override
    public List<ProgressDetail> queryProgressDetail(long orderId, int number) {
        List<ProgressDetail> progressDetailList = new ArrayList<ProgressDetail>();

        List<OrderStateHistory> orderStateHistoryList = queryUserModeOrderStateHistory(orderId);

        for (OrderStateHistory history : orderStateHistoryList) {
            // 如果是已发货, 则查询物流信息
            if (history.getOrderState() == OrderState.Send) {
                Logistics logistics = logisticsService.getLogisticsByOrderId(orderId);
                if (logistics != null) {
                    // 物流单号
                    String waybillNumber = logistics.getDeliveryInfo().getWaybillNumber();
                    // 物流信息
                    LogisticsInfo logisticsInfo = operateLogisticsService.queryLogistics(waybillNumber);

                    if (logisticsInfo != null && StringUtils.isNotBlank(logisticsInfo.getExpressValue())) {
                        BackMsg backMsg = JsonUtil.json2Object(logisticsInfo.getExpressValue(), BackMsg.class);
                        if (backMsg != null) {
                            List<LinkedHashMap<String, String>> detailList = backMsg.getLastResult().getData();
                            // 单条详情
                            int count = 0;
                            for (LinkedHashMap<String, String> map : detailList) {
                                ProgressDetail detail = new ProgressDetail();
                                detail.setDate(map.get("time"));
                                detail.setDetail(map.get("context"));

                                String operator = logistics.getDeliveryInfo().getDeliveryType().toDesc();
                                // 若已经确认收货且是物流信息的第一条记录则操作者显示为客户, 否则显示物流公司名
                                if (logisticsInfo.getStatus() == 1 && count == 0)
                                    operator = "客户";
                                count++;

                                detail.setOperator(operator);
                                progressDetailList.add(detail);
                            }
                        }
                    }
                }
            }

            ProgressDetail progressDetail = new ProgressDetail();
            progressDetail.setDate(DateUtils.formatDate(history.getDate(), DateUtils.DateFormatType.DATE_FORMAT_STR));
            progressDetail.setDetail(history.getDoWhat());
            progressDetail.setOperator(history.getOperator());
            progressDetailList.add(progressDetail);
        }

        if (progressDetailList.size() > number && number > 0) {
            List<ProgressDetail> cutList = new ArrayList<ProgressDetail>(number);
            for (int i = 0; i < number; i++) {
                cutList.add(progressDetailList.get(i));
            }
            return cutList;
        }
        return progressDetailList;
    }

    @Override
    public BackMsg queryLogisticsInfoByOrderId(long orderId) {
        BackMsg backMsg = null;
        Logistics logistics = logisticsService.getLogisticsByOrderId(orderId);
        if (logistics != null) {
            // 物流单号
            String waybillNumber = logistics.getDeliveryInfo().getWaybillNumber();
            // 物流信息
            LogisticsInfo logisticsInfo = operateLogisticsService.queryLogistics(waybillNumber);

            if (logisticsInfo != null) {
                backMsg = JsonUtil.json2Object(logisticsInfo.getExpressValue(), BackMsg.class);
            }
        }
        return backMsg;
    }

    @Override
    public Progress queryOrderProgress(long orderId) {
        Progress progress = new Progress();
        List<ProgressDetail> lightenProgressList = new ArrayList<ProgressDetail>();
        List<String> greyProgressList = new ArrayList<String>();

        Order order = getSimpleOrder(orderId);
        paddingProgress(orderId, order.getOrderState(), order.getPayType(), lightenProgressList, greyProgressList);

        progress.setLightenProgress(lightenProgressList);
        progress.setGreyProgress(greyProgressList);
        return progress;
    }

    /**
     * 组装进度信息.
     *
     * @param orderId
     * @param orderState
     * @param payType
     * @param lightenProgressList
     * @param greyProgressList
     */
    private void paddingProgress(long orderId, OrderState orderState, PayType payType,
                                 List<ProgressDetail> lightenProgressList, List<String> greyProgressList) {
        switch (orderState) {
            case Create: {
                lightenProgressList.add(getProgressDetailByOrderIdAndState(orderId, OrderState.Create, "提交订单"));

                ProgressDetail lightenWait = new ProgressDetail();
                String forWhat = (payType == PayType.OnLine) ? "付款" : "确认";
                lightenWait.setDetail("等待" + forWhat);
                lightenProgressList.add(lightenWait);

                greyProgressList.add("等待收货");
                greyProgressList.add("完成");
                break;
            }
            case Pay:
            case Confirm:
            case Print:
            case Verify: {
                lightenProgressList.add(getProgressDetailByOrderIdAndState(orderId, OrderState.Create, "提交订单"));

                // 若是在线支付, 则使用支付成功的历史记录, 否则(货到付款)使用确认完成的历史记录
                OrderState debateState = (payType == PayType.OnLine) ? OrderState.Pay : OrderState.Confirm;
                String debateDetail = (payType == PayType.OnLine) ? "已付款" : "已确认";
                lightenProgressList.add(getProgressDetailByOrderIdAndState(orderId, debateState, debateDetail));

                ProgressDetail lightenWait = new ProgressDetail();
                lightenWait.setDetail("等待发货");
                lightenProgressList.add(lightenWait);

                greyProgressList.add("完成");
                break;
            }
            case Send: {
                lightenProgressList.add(getProgressDetailByOrderIdAndState(orderId, OrderState.Create, "提交订单"));

                // 若是在线支付, 则使用支付成功的历史记录, 否则(货到付款)使用确认完成的历史记录
                OrderState debateState = (payType == PayType.OnLine) ? OrderState.Pay : OrderState.Confirm;
                String debateDetail = (payType == PayType.OnLine) ? "已付款" : "已确认";
                lightenProgressList.add(getProgressDetailByOrderIdAndState(orderId, debateState, debateDetail));

                lightenProgressList.add(getProgressDetailByOrderIdAndState(orderId, OrderState.Send, "等待收货"));

                greyProgressList.add("完成");
                break;
            }
            case Success: {
                lightenProgressList.add(getProgressDetailByOrderIdAndState(orderId, OrderState.Create, "提交订单"));

                // 若是在线支付, 则使用支付成功的历史记录, 否则(货到付款)使用确认完成的历史记录
                OrderState debateState = (payType == PayType.OnLine) ? OrderState.Pay : OrderState.Confirm;
                String debateDetail = (payType == PayType.OnLine) ? "已付款" : "已确认";
                lightenProgressList.add(getProgressDetailByOrderIdAndState(orderId, debateState, debateDetail));

                lightenProgressList.add(getProgressDetailByOrderIdAndState(orderId, OrderState.Send, "等待收货"));
                lightenProgressList.add(getProgressDetailByOrderIdAndState(orderId, OrderState.Success, "完成"));
                break;
            }
            case Back:
            case Close:
            case Cancel: {
                greyProgressList.add("提交订单");

                String forWhat = (payType == PayType.OnLine) ? "付款" : "确认";
                greyProgressList.add("等待" + forWhat);

                greyProgressList.add("等待收货");
                greyProgressList.add("交易完成");
                break;
            }
        }
    }

    private ProgressDetail getProgressDetailByOrderIdAndState(long orderId, OrderState orderState, String detail) {
        ProgressDetail lighten = new ProgressDetail();
        OrderStateHistory history = queryHistoryByState(orderId, orderState);
        if (history == null) {
            if (logger.isErrorEnabled())
                logger.error("订单(" + orderId + ")无历史记录");
        } else {
            lighten.setDate(DateUtils.formatDate(history.getDate(), DateUtils.DateFormatType.DATE_FORMAT_STR));
        }

        lighten.setDetail(detail);
        return lighten;
    }

    @Override
    public OrderStateHistory queryHistoryByState(long orderId, OrderState orderState) {
        return orderRepository.queryHistoryByState(orderId, orderState);
    }


    @Override
    public Order getDetailsOrder(long orderId) {
        Order order = getSimpleOrder(orderId);
        buildOrder(order);
        return order;
    }

    @Override
    public Order getSimpleOrder(long orderId) {
        return orderRepository.getOrderById(orderId);
    }

    @Override
    public Order getSimpleOrderByOrderNo(long orderNo) {
        return orderRepository.getOrderByOrderNo(orderNo);
    }

    @Override
    public Order getOrderByUserIdAndOrderNo(int userId, long orderNo) {
        Order order = orderRepository.getOrderByUserIdAndOrderNo(userId, orderNo);
        buildOrder(order);
        return order;
    }

    @Override
    public Order getOrderByOrderNo(long orderNo) {
        Order order = getSimpleOrderByOrderNo(orderNo);
        buildOrder(order);
        return order;
    }

    @Override
    public List<Order> queryOrderByState(OrderState orderState) {
        return orderRepository.queryOrderByState(orderState);
    }

    @Override
    public List<Long> queryOrderByCanConfirm(int day, int limit, String userNameRegex) {
        return orderRepository.queryOrderByCanConfirm(day, limit, userNameRegex);
    }

    @Override
    public List<Order> queryCashOnDeliveryOrder() {
        return orderRepository.selectCashOnDeliveryOrder();
    }

    @Override
    public SkuTradeResult querySkuTradeResultBySkuId(long skuId) {
        return skuTradeResultRepository.getBySkuId(skuId);
    }

    @Override
    public List<OrderMessage> queryAllOrderMessage(long orderId) {
        return orderRepository.getOrderMessage(orderId);
    }

    @Override
    public OrderMessage queryUserOrderMessage(long orderId) {
        return orderRepository.getOrderMessageByIdAndType(orderId, OrderMessage.UserType.User);
    }

    @Override
    public OrderMessage queryServerOrderMessage(long orderId) {
        return orderRepository.getOrderMessageByIdAndType(orderId, OrderMessage.UserType.Server);
    }

    @Override
    public List<Map<String, Object>> queryCountByCouponIdWithoutId(int couponId, long orderId) {
        return orderRepository.getCountByCouponIdWithoutId(couponId, orderId);
    }

    /**
     * 构建订单详情
     *
     * @param order
     */
    private void buildOrder(Order order) {
        if (order != null) {
            //加入订单商品
            order.setOrderItemList(this.queryRealOrderItemNumberByOrderId(order.getId()));
            //加入物流
            Logistics logistics = logisticsService.getLogisticsByOrderId(order.getId());
            if (logistics != null) {
                logistics.setLogisticsRedundancy(queryLogisticsRedundancy(logistics.getId()));
            }
            order.setLogistics(logistics);
            //加入发票
            order.setInvoiceInfo(queryOrderInvoiceInfoRedundancy(order.getId()));
        }
    }
}
