package com.kariqu.tradecenter.repository.impl.ibatis;

import com.google.common.collect.Maps;
import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductActivityType;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.repository.OrderRepository;
import com.kariqu.tradecenter.service.OrderQuery;
import com.kariqu.usercenter.domain.AccountType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.kariqu.common.lib.Collections4.map;

/**
 * User: Asion
 * Date: 11-10-14
 * Time: 下午2:53
 */
public class OrderRepositoryImpl extends SqlMapClientDaoSupport implements OrderRepository {

    @Override
    public VirtualOrder queryVirtualOrderById(long virtualOrderId) {
        return (VirtualOrder) getSqlMapClientTemplate().queryForObject("queryVirtualOrderById", virtualOrderId);
    }

    @Override
    public void createVirtualOrder(VirtualOrder virtualOrder) {
        getSqlMapClientTemplate().insert("createVirtualOrder", virtualOrder);
    }

    @Override
    public boolean checkVirtualOrderIfPaySuccessful(long virtualOrderId) {
        Integer count = (Integer) getSqlMapClientTemplate().queryForObject("checkVirtualOrderIfPaySuccessful", virtualOrderId);
        return count == 1;
    }

    @Override
    public void updateVirtualOrder(VirtualOrder virtualOrder) {
        getSqlMapClientTemplate().update("updateVirtualOrder", virtualOrder);
    }

    @Override
    public List<Order> queryOrderByVirtualOrderId(long virtualOrderId) {
        return getSqlMapClientTemplate().queryForList("queryOrderByVirtualOrderId", virtualOrderId);
    }

    @Override
    public List<Order> queryOrderByCustomerId(int customerId, OrderState orderState) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderState", orderState);
        map.put("customerId", customerId);
        return getSqlMapClientTemplate().queryForList("selectOrderByCustomerId", map);
    }

    public List<Order> queryOrderByVirtualIdExcludeState(long virtualOrderId, OrderState orderState) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("virtualOrderId", virtualOrderId);
        map.put("orderState", orderState);
        return getSqlMapClientTemplate().queryForList("queryOrderByVirtualIdExcludeState", map);
    }

    @Override
    public Page<VirtualOrder> queryVirtualOrderByPage(int userId, AccountType accountType,
                                                      String orderTime, String orderStatus, Page<VirtualOrder> page) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        map.put("accountType", accountType);
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());

        // 订单时间, 默认查询一个月内的订单
        map.put("orderTime", StringUtils.isEmpty(orderTime) ? "gt" : orderTime);
        // 订单状态
        map.put("orderStatus", StringUtils.isEmpty(orderStatus) ? "" : orderStatus);

        List<VirtualOrder> list = getSqlMapClientTemplate().queryForList("selectVirtualOrderByUserIdPage", map);
        page.setResult(list);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForVirtualOrderByUserId", map));
        // 判断当前页数, 防止因条件改变, 不存在此页数时的 bug
        if (page.getTotalPages() < page.getPageNo())
            page.setPageNo(1);
        return page;
    }

    @Override
    public int selectCountOrderItemByMarketingId(List<Long> squIds, ProductActivityType skuActivityType, Integer skuMarketingId) {
        Map<String,Object> param = map(
                "squIds", squIds,
                "skuActivityType", skuActivityType,
                "skuMarketingId", skuMarketingId
        );

        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountOrderItemByMarketingId", param);
    }

    @Override
    public int selectUserJoinSepecialActivityCount(int userId, List<Long> squIds, ProductActivityType skuActivityType, Integer skuMarketingId){
        Map<String,Object> param = map(
                "userId", userId,
                "squIds", squIds,
                "skuActivityType", skuActivityType,
                "skuMarketingId", skuMarketingId
        );

        Integer selectUserJoinSepecialActivityCount = (Integer) getSqlMapClientTemplate().queryForObject("selectUserJoinSepecialActivityCount", param);
        return selectUserJoinSepecialActivityCount;
    }

    @Override
    public void createOrder(Order order) {
        checkOrderNo();
        // 订单编号
        order.setOrderNo(OrderNumberUtil.getOrderNo());

        getSqlMapClientTemplate().insert("insertOrder", order);
    }

    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 检查订单编号. 若是初始值, 则需要去数据库查询并重新为初始值赋值.
     */
    private void checkOrderNo() {
        // 判断此时是否是初始值, 只在初始值时将数据库最大值进行重新赋值.
        if (OrderNumberUtil.isInitNum()) {
            LOCK.lock();
            try {
                // 双重检测...
                if (OrderNumberUtil.isInitNum()) {
                    Object obj = getSqlMapClientTemplate().queryForObject("selectMaxOrderNo");
                    if (obj != null && obj instanceof Long) {
                        long maxOrderNo = (Long) obj;
                        // 去掉前面的年月日
                        OrderNumberUtil.setIncrementNum(String.valueOf(maxOrderNo));
                    }
                }
            } finally {
                LOCK.unlock();
            }
        }
    }

    @Override
    public Order getOrderById(long orderId) {
        return (Order) getSqlMapClientTemplate().queryForObject("selectOrder", orderId);
    }

    @Override
    public Order getOrderByOrderNo(long orderNo) {
        return (Order) getSqlMapClientTemplate().queryForObject("selectOrderByOrderNo", orderNo);
    }

    @Override
    public Order getOrderByUserIdAndOrderNo(int userId, long orderNo) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        map.put("orderNo", orderNo);
        return (Order) getSqlMapClientTemplate().queryForObject("selectOrderByUserIdAndOrderNo", map);
    }


    @Override
    public int updateOrder(Order order) {
        return getSqlMapClientTemplate().update("updateOrder", order);
    }

    @Override
    public int updateOrderState(long orderId, OrderState orderState, OrderState previousState) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderId", orderId);
        map.put("orderState", orderState);
        map.put("previousState", previousState);
        return getSqlMapClientTemplate().update("updateOrderState", map);
    }

    @Override
    public int updateOrderStateToConfirm(long orderId) {
        return getSqlMapClientTemplate().update("updateOrderStateOfPayOnDelivery", orderId);
    }

    @Override
    public List<Order> queryOrderByState(OrderState orderState) {
        return getSqlMapClientTemplate().queryForList("OrderListByState", orderState);
    }

    @Override
    public List<Long> queryOrderByCanConfirm(int day, int limit, String userNameRegex) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("day", day);
        map.put("limit", limit);
        map.put("userNameRegex", userNameRegex);
        return getSqlMapClientTemplate().queryForList("queryOrderByCanConfirm", map);
    }

    @Override
    public List<Order> selectCashOnDeliveryOrder() {
        return getSqlMapClientTemplate().queryForList("selectCashOnDeliveryOrder");
    }

    @Override
    public void deleteOrder(long orderId) {
        getSqlMapClientTemplate().delete("deleteOrder", orderId);
    }

    @Override
    public void createOrderItem(OrderItem orderItem) {
        getSqlMapClientTemplate().insert("insertOrderItem", orderItem);
    }

    @Override
    public List<Long> queryNotPayOrder(int delay) {
        final long delayMillisecond = delay * 60 * 1000;
        final long current = System.currentTimeMillis();
        return getSqlMapClientTemplate().queryForList("queryNotPayOrder", new HashMap() {{
            put("delayMillisecond", delayMillisecond);
            put("current", current);
        }});
    }

    @Override
    public List<Long> queryNotConfirmSuccessOrder(int delay) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderState", OrderState.Send);
        map.put("delay", delay);
        return getSqlMapClientTemplate().queryForList("queryNotConfirmSuccessOrderId", map);
    }

    @Override
    public void createOrderStateHistory(OrderStateHistory orderStateHistory) {
        getSqlMapClientTemplate().insert("insertOrderStateHistory", orderStateHistory);
    }

    @Override
    public void updateOrderStateHistoryToVisibleWithUser(long orderId, OrderState orderState) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderId", orderId);
        map.put("orderState", orderState);
        getSqlMapClientTemplate().update("updateOrderStateHistoryToVisibleWithUser", map);
    }

    @Override
    public void updateOrderMutexStateHistory(final long orderId, final OrderState orderState) {
        getSqlMapClientTemplate().update("updateOrderMutexStateHistory", new HashMap() {{
            put("orderId", orderId);
            //put("stateLevel", orderState.getLevel());
        }});
    }

    @Override
    public List<OrderStateHistory> queryUserModeOrderStateHistory(long orderId) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderId", orderId);
        map.put("orderStateList", OrderState.userState());
        return getSqlMapClientTemplate().queryForList("queryUserModeOrderStateHistory", map);
    }

    @Override
    public List<OrderStateHistory> queryAllOrderStateHistory(long orderId) {
        return getSqlMapClientTemplate().queryForList("queryAllOrderStateHistory", orderId);
    }

    @Override
    public List<Order> queryOrderByUserId(int userId, AccountType accountType) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        map.put("accountType", accountType);
        return getSqlMapClientTemplate().queryForList("selectOrderByUserId", map);
    }

    @Override
    public Page<Order> queryOrderByUserIdPage(int userId, Integer orderState, Page<Order> page) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForOrderByUserId_" + orderState, map));
        List<Order> list = getSqlMapClientTemplate().queryForList("selectOrderByUserIdPage_" + orderState, map);
        page.setResult(list);
        return page;
    }

    public Page<Order> getNotCancelOrderPageByUserId(int userId, Page<Order> page) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForNotCancelOrderByUserId", map));
        List<Order> list = getSqlMapClientTemplate().queryForList("selectNotCancelOrderByUserId", map);
        page.setResult(list);
        return page;
    }

    /**
     * 按用户id查询申请退单列表
     * @param userId
     * @param page
     * @return
     */
    @Override
    public Page<Order> queryBackGoodsApplyByUserIdPage(int userId, Page<Order> page) {
        int totalCount = (Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForBackGoodsApplyByUserIdPage", userId);
        //如果页面输入的页数超过了总页数，则将页数设置为第一页
        if(page.getStart() >= totalCount){
            page.setPageNo(1);
        }
        page.setTotalCount(totalCount);
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        List<Order> list = getSqlMapClientTemplate().queryForList("selectBackGoodsApplyByUserIdPage" , map);
        page.setResult(list);
        return page;
    }

    public int queryOrderCountByUserIdAndState(int userId, Integer orderState) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        return (Integer) this.getSqlMapClientTemplate().queryForObject("selectCountForOrderByUserId_" + orderState, map);
    }

    @Override
    public int queryCountForNoProcessOrder(int userId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountForNoProcessOrder", userId);
    }

    @Override
    public List<OrderItem> queryCanBackOrderItemByOrderNo(long orderNo) {
        return (List<OrderItem>)getSqlMapClientTemplate().queryForList("selectCanBackOrderItemByOrderNo", orderNo);
    }

    @Override
    public Map<String, Object> queryValuationCountByUserId(int userId, int appraise) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        map.put("appraise", appraise);
        return (Map) getSqlMapClientTemplate().queryForObject("selectValuationCountByUserIdAndAppraise", map);
    }

    @Override
    public Page<OrderItem> queryWaitValuationByUserId(int userId, int appraise, Page<OrderItem> page) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        map.put("appraise", appraise);
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());

        page.setTotalCount(NumberUtils.toInt(queryValuationCountByUserId(userId, appraise).get("count").toString()));
        page.setResult(getSqlMapClientTemplate().queryForList("selectValuationByUserIdAndAppraise", map));
        return page;
    }

    @Override
    public Page<Order> queryRecentOrderByUserId(int userId, Page<Order> page) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());

        page.setTotalCount(queryRecentOrderCountByUserId(userId));
        page.setResult(getSqlMapClientTemplate().queryForList("selectRecentOrderByUserId", map));
        return page;
    }

    @Override
    public int queryRecentOrderCountByUserId(int userId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectRecentOrderCountByUserId", userId);
    }

    @Override
    public int queryWaitPayOrderCountByUserId(int userId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectWaitPayOrderCountByUserId", userId);
    }

    @Override
    public Map<String, Object> queryOrderCountByUserIdAndOrderState(int userId, OrderState orderState) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("userId", userId);
        map.put("orderState", orderState);
        return (HashMap) getSqlMapClientTemplate().queryForObject("selectOrderCountByUserIdAndState", map);
    }

    @Override
    public List<OrderItem> queryOrderItemsByOrderId(long orderId) {
        return getSqlMapClientTemplate().queryForList("selectOrderItemByOrderId", orderId);
    }




    @Override
    public OrderItem queryOrderItemById(long orderItemId) {
        return (OrderItem) getSqlMapClientTemplate().queryForObject("selectOrderItemById", orderItemId);
    }

    public int queryOrderItemCountByOrderId(long orderId) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderId", orderId);

        return queryOrderItemCount(map);
    }

    private int queryOrderItemCount(Map map) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountOrderItemByOrderIdAndState", map);
    }

    public int queryOrderItemCountByOrderIdAndState(long orderId, OrderState state) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderId", orderId);
        map.put("orderState", state);

        return queryOrderItemCount(map);
    }

    @Override
    public Page<Order> searchOrderByQuery(OrderQuery orderQuery) {
        Map<String, Object> map = Maps.newHashMap();

        if (StringUtils.isNotEmpty(orderQuery.getStartDate())) {
            Date start_date = DateUtils.parseDate((orderQuery.getStartDate()).replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR);
            map.put("startDate", start_date);
        }

        if (StringUtils.isNotEmpty(orderQuery.getEndDate())) {
            Date end_date = DateUtils.parseDate((orderQuery.getEndDate()).replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR);
            map.put("endDate", end_date);
        }

        Page<Order> page = Page.createFromStart(orderQuery.getStart(), orderQuery.getLimit());
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());

        map.put("orderNo", orderQuery.getOrderNo());
        if (orderQuery.getOrderState() != null && orderQuery.getOrderState().length > 0)
            map.put("orderState", orderQuery.orderState());

        map.put("customerId", orderQuery.getCustomerId());
        map.put("storageId", orderQuery.getStorageId());
        map.put("consignee", orderQuery.getConsignee());
        map.put("mobile", orderQuery.getMobile());
        map.put("orderOwner", orderQuery.getOrderOwner());
        map.put("userId", NumberUtils.toInt(orderQuery.getUserId()));
        map.put("skuName", orderQuery.getSkuName());
        map.put("appraise", orderQuery.getAppraise());
        if (orderQuery.getBrush() != null)
            map.put("brush", orderQuery.getBrush());
        if (StringUtils.isNotBlank(orderQuery.getSkuName()) || StringUtils.isNotBlank(orderQuery.getAppraise()))
            map.put("skuNameOrAppraise", true);
        List<Order> list = getSqlMapClientTemplate().queryForList("searchOrderList", map);
        page.setResult(list);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("searchOrderCount", map));
        return page;
    }

    @Override
    public List<List<Order>> searchOrderListByQuery(OrderQuery orderQuery) {
        Map<String, Object> map = Maps.newHashMap();

        if (StringUtils.isNotEmpty(orderQuery.getStartDate())) {
            Date start_date = DateUtils.parseDate((orderQuery.getStartDate()).replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR);
            map.put("startDate", start_date);
        }
        if (StringUtils.isNotEmpty(orderQuery.getEndDate())) {
            Date end_date = DateUtils.parseDate((orderQuery.getEndDate()).replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR);
            map.put("endDate", end_date);
        }

        map.put("orderNo", orderQuery.getOrderNo());
        map.put("customerId", orderQuery.getCustomerId());
        map.put("storageId", orderQuery.getStorageId());
        map.put("consignee", orderQuery.getConsignee());
        map.put("mobile", orderQuery.getMobile());
        map.put("orderOwner", orderQuery.getOrderOwner());
        map.put("userId", NumberUtils.toInt(orderQuery.getUserId()));
        map.put("skuName", orderQuery.getSkuName());
        map.put("appraise", orderQuery.getAppraise());
        if (StringUtils.isNotBlank(orderQuery.getSkuName()) || StringUtils.isNotBlank(orderQuery.getAppraise()))
            map.put("skuNameOrAppraise", true);
        if (orderQuery.getBrush() != null)
            map.put("brush", orderQuery.getBrush());
        map.put("noLimit", true);

        List<List<Order>> orderListArray = new ArrayList<List<Order>>();
        if (orderQuery.getOrderState() != null && orderQuery.getOrderState().length > 0) {
            for (OrderState orderState : orderQuery.getOrderState()) {
                map.put("orderState", "'" + orderState + "'");
                orderListArray.add(getSqlMapClientTemplate().queryForList("searchOrderList", map));
            }
        } else {
            orderListArray.add(getSqlMapClientTemplate().queryForList("searchOrderList", map));
        }
        return orderListArray;
    }

    @Override
    public List<SystemEvent> querySystemEvents(long orderId) {
        return getSqlMapClientTemplate().queryForList("selectSystemEvent", orderId);
    }

    @Override
    public void createSystemEvent(SystemEvent systemEvent) {
        getSqlMapClientTemplate().insert("insertSystemEvent", systemEvent);
    }

    @Override
    public void deleteSystemEvents(long orderId) {
        getSqlMapClientTemplate().delete("deleteSystemEvents", orderId);
    }

    public Page<Order> searchQuery(Map param, Page<Order> page) {
        param.put("start", (page.getPageNo() - 1) * page.getPageSize());
        param.put("limit", page.getPageSize());
        List<Order> list = getSqlMapClientTemplate().queryForList("searchOrderForCustomer", param);
        page.setResult(list);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("searchOrderForCustomerCount", param));
        return page;
    }

    @Override
    public List<Order> searchQueryList(Map param) {
        return getSqlMapClientTemplate().queryForList("searchOrderListForCustomer", param);
    }

    @Override
    public int updateOrderItem(OrderItem orderItem) {
        return getSqlMapClientTemplate().update("updateOrderItemById", orderItem);
    }

    @Override
    public int updateOrderItemState(long orderItemId, OrderState orderState, OrderState previousState) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderItemId", orderItemId);
        map.put("orderState", orderState);
        map.put("previousState", previousState);
        return getSqlMapClientTemplate().update("updateOrderItemState", map);
    }

    @Override
    public int updateOrderItemAppraise(long orderItemId) {
        return getSqlMapClientTemplate().update("updateOrderItemAppraise", orderItemId);
    }

    @Override
    public String getOrderPriceByOrderNo(long orderNo) {
        return (String) getSqlMapClientTemplate().queryForObject("getOrderPrice", orderNo);
    }

    public void updateOrderPayBank(long orderNo, PayBank bank) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderNo", orderNo);
        map.put("payBank", bank);
        getSqlMapClientTemplate().update("updateOrderPayBank", map);
    }

    public void updateOrderDeliveryType(long orderId, DeliveryInfo.DeliveryType type) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderId", orderId);
        map.put("deliveryType", type);
        getSqlMapClientTemplate().update("updateOrderDeliveryType", map);
    }

    public OrderStateHistory queryHistoryWithOutState(long orderId, OrderState state) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderId", orderId);
        map.put("orderState", state);
        return (OrderStateHistory) getSqlMapClientTemplate().queryForObject("queryOrderStateHistoryWithOutState", map);
    }

    public OrderStateHistory queryHistoryByState(long orderId, OrderState orderState) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderId", orderId);
        map.put("orderState", orderState);
        return (OrderStateHistory) getSqlMapClientTemplate().queryForObject("queryHistoryWithState", map);
    }

    public int queryOrderCountByState(OrderState state) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectOrderCountByState", state);
    }

    public int queryOrderCountByWaitApproval() {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectOrderCountByWaitApproval");
    }

    @Override
    public InvoiceInfo queryOrderInvoiceInfoRedundancy(long id) {
        return (InvoiceInfo) getSqlMapClientTemplate().queryForObject("queryOrderInvoiceInfoRedundancy", id);
    }

    @Override
    public void updateOrderInvoiceInfoRedundancy(long orderId, InvoiceInfo invoiceInfo) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", orderId);
        map.put("invoiceTypeRewrite", invoiceInfo.getInvoiceTypeRewrite());
        map.put("invoiceTitleRewrite", invoiceInfo.getInvoiceTitleRewrite());
        map.put("invoiceContentRewrite", invoiceInfo.getInvoiceContentRewrite());
        map.put("companyNameRewrite", invoiceInfo.getCompanyNameRewrite());
        map.put("editor", invoiceInfo.getEditor());
        getSqlMapClientTemplate().update("updateOrderInvoiceInfoRedundancy", map);
    }

    @Override
    public List<OrderStateHistory> queryOrderStateHistoryDistinct(long orderId) {
        return getSqlMapClientTemplate().queryForList("queryOrderStateHistoryDistinct", orderId);
    }

    @Override
    public void createOrderMessage(OrderMessage orderMessage) {
        getSqlMapClientTemplate().insert("insertOrderMessage", orderMessage);
    }

    @Override
    public List<OrderMessage> getOrderMessage(long orderId) {
        return getSqlMapClientTemplate().queryForList("queryOrderMessage", orderId);
    }

    @Override
    public OrderMessage getOrderMessageByIdAndType(long orderId, OrderMessage.UserType userType) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("orderId", orderId);
        map.put("userType", userType);

        return (OrderMessage) getSqlMapClientTemplate().queryForObject("queryOrderMessageByUserType", map);
    }

    @Override
    public List<Map<String, Object>> getCountByCouponIdWithoutId(int couponId, long orderId) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("couponId", couponId);
        map.put("id", orderId);

        return getSqlMapClientTemplate().queryForList("selectOrderByCouponIdWithOutId", map);
    }

    @Override
    public int brush(long orderId) {
        return getSqlMapClientTemplate().update("brushOrder", orderId);
    }

}
