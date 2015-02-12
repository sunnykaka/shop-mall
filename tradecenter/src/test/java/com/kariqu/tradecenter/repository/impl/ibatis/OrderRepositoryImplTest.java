package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.ProductActivityType;
import com.kariqu.productcenter.domain.StoreStrategy;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.repository.OrderRepository;
import com.kariqu.tradecenter.service.OrderQuery;
import com.kariqu.usercenter.domain.AccountType;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.Date;
import java.util.List;

import static com.kariqu.common.lib.Collections4.list;
import static junit.framework.Assert.*;

/**
 * User: Asion
 * Date: 11-10-14
 * Time: 下午4:22
 */
@SpringApplicationContext({"classpath:tradeContext.xml"})
public class OrderRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("orderRepository")
    private OrderRepository orderRepository;

    @Test
    public void testOrderRepository() {

        VirtualOrder virtualOrder = new VirtualOrder();
        virtualOrder.setCreateDate(new Date());
        virtualOrder.setAccountType(AccountType.KRQ);
        virtualOrder.setVirtualState(VirtualOrder.VirtualState.WaitingForPay);
        virtualOrder.setUserId(123);
        virtualOrder.setPayBank(PayBank.BJBANK);
        virtualOrder.setTotalPrice(1l);
        virtualOrder.setPayPrice(2l);
        orderRepository.createVirtualOrder(virtualOrder);
        virtualOrder.setUserId(234);
        virtualOrder.setCancelDate(new Date());
        virtualOrder.setEndDate(new Date());
        assertFalse(orderRepository.checkVirtualOrderIfPaySuccessful(virtualOrder.getId()));
        virtualOrder.setVirtualState(VirtualOrder.VirtualState.PayCompleted);
        virtualOrder.setPayPrice(3l);
        orderRepository.updateVirtualOrder(virtualOrder);
        assertEquals(3l,orderRepository.queryVirtualOrderById(virtualOrder.getId()).getPayPrice());
        assertTrue(orderRepository.checkVirtualOrderIfPaySuccessful(virtualOrder.getId()));


        Order order = new Order();
        order.setValid(true);
        order.setUserId(11);
        order.setUserName("test");
        order.setPayType(PayType.OnLine);
        order.setPayBank(PayBank.BJBANK);
        order.setTotalPrice("22.00");
        order.setAccountType(AccountType.KRQ);
        order.setOrderState(OrderState.Create);
        order.setCreateDate(new Date(System.currentTimeMillis() - 2 * 3600 * 1000));
        order.setVirtualOrderId(virtualOrder.getId());
        order.setCustomerId(1);
        order.setStorageId(1);

        InvoiceInfo invoiceInfo = new InvoiceInfo();
        invoiceInfo.setInvoice(true);
        invoiceInfo.setCompanyName("易居尚");
        invoiceInfo.setInvoiceTitle(InvoiceInfo.InvoiceTitle.individual);
        invoiceInfo.setInvoiceContent("办公用品");
        order.setInvoiceInfo(invoiceInfo);
        order.setOrderState(OrderState.Pay);

        orderRepository.createOrder(order);
        assertEquals(1, orderRepository.queryCountForNoProcessOrder(11));

        invoiceInfo.setInvoiceTitle(InvoiceInfo.InvoiceTitle.individual);
        invoiceInfo.setInvoiceContent("明细");
        invoiceInfo.setCompanyName("卡日曲网络科技有限公司");
        InvoiceInfo invoiceInfo2 = orderRepository.queryOrderInvoiceInfoRedundancy(order.getId());
        assertEquals(null, invoiceInfo2.getCompanyNameRewrite());
        invoiceInfo2.setInvoiceTypeRewrite("普通发票");
        orderRepository.updateOrderInvoiceInfoRedundancy(order.getId(),invoiceInfo2);
        invoiceInfo2 = orderRepository.queryOrderInvoiceInfoRedundancy(order.getId());
        assertEquals(null, invoiceInfo2.getCompanyNameRewrite());
        assertEquals("普通发票", invoiceInfo2.getInvoiceTypeRewrite());



        assertEquals("22.00",orderRepository.getOrderPriceByOrderNo(order.getOrderNo()));

        OrderQuery orderQuery=new OrderQuery();
        orderQuery.setLimit(10);
        assertEquals(1, orderRepository.searchOrderByQuery(orderQuery).getResult().size());

        assertEquals(OrderState.Pay, orderRepository.getOrderById(order.getId()).getOrderState());

        assertEquals(0, orderRepository.queryNotPayOrder(1).size());

        OrderStateHistory orderStateHistory = new OrderStateHistory();
        orderStateHistory.setOrderState(OrderState.Pay);
        orderStateHistory.setOrderId(order.getId());
        orderRepository.createOrderStateHistory(orderStateHistory);
        assertEquals(1, orderRepository.queryUserModeOrderStateHistory(order.getId()).size());
        orderRepository.updateOrderMutexStateHistory(order.getId(), OrderState.Confirm);
        assertEquals(1, orderRepository.queryUserModeOrderStateHistory(order.getId()).size());

        //orderStateHistory.setOverlay(true);
        //orderStateHistory.setDebugMode(true);
        orderStateHistory.setOrderState(OrderState.Create);
        orderRepository.createOrderStateHistory(orderStateHistory);
        assertEquals(2, orderRepository.queryUserModeOrderStateHistory(order.getId()).size());
        assertEquals(2, orderRepository.queryAllOrderStateHistory(order.getId()).size());
        assertEquals(0, orderRepository.queryNotConfirmSuccessOrder(35).size());

        assertEquals(11, orderRepository.getOrderById(order.getId()).getUserId());

        assertEquals(true, orderRepository.getOrderById(order.getId()).isValid());
        assertEquals(AccountType.KRQ, orderRepository.getOrderById(order.getId()).getAccountType());
        assertEquals(1, orderRepository.queryOrderByUserId(11, AccountType.KRQ).size());

        order.setOrderState(OrderState.Cancel);
        order.setValid(false);
        order.setTotalPrice("3333");
        order.setUserId(3);
        order.setAccountType(AccountType.QQ);
        orderRepository.updateOrder(order);

        assertEquals(1, orderRepository.queryOrderByUserId(3, AccountType.QQ).size());
        assertEquals(OrderState.Cancel, orderRepository.getOrderById(order.getId()).getOrderState());


        OrderItem orderItem = new OrderItem();
        orderItem.setNumber(2);
        orderItem.setOrderId(order.getId());
        orderItem.setSkuId(3);
        orderItem.setStoreStrategy(StoreStrategy.NormalStrategy);
        orderItem.setSkuName("铁锅");
        orderItem.setSkuExplain("颜色：红色");
        orderItem.setSkuMainPicture("www.123.cpm");
        orderItem.setBarCode("123");
        orderItem.setItemNo("23");
        orderItem.setOrderState(OrderState.Verify);
        orderRepository.createOrderItem(orderItem);

        orderItem.setNumber(3);
        orderItem.setStoreStrategy(StoreStrategy.PayStrategy);
        orderRepository.updateOrderItem(orderItem);

        List<OrderItem> itemList = orderRepository.queryOrderItemsByOrderId(order.getId());
        assertEquals(3, itemList.get(0).getNumber());
        assertEquals(1, itemList.size());


        SystemEvent systemEvent = new SystemEvent();
        systemEvent.setDate(new Date());
        systemEvent.setEventInfo("订单入库");
        systemEvent.setOperator("客户");
        systemEvent.setOrderId(order.getId());
        systemEvent.setBeforeState(OrderState.Create);
        systemEvent.setAfterState(OrderState.Pay);
        orderRepository.createSystemEvent(systemEvent);

        assertEquals(1, orderRepository.querySystemEvents(order.getId()).size());
        assertEquals(1, orderRepository.queryOrderItemsByOrderId(order.getId()).size());
        orderRepository.deleteSystemEvents(order.getId());
        orderRepository.deleteOrder(order.getId());
        orderRepository.deleteSystemEvents(order.getId());

    }

    @Test
    public void testSelectCountOrderItemByMarketingId() throws Exception {
        List<Long> squIds = list(1L, 2L, 3L);
        Integer skuMarketingId = 20;
        int count = orderRepository.selectCountOrderItemByMarketingId(squIds, ProductActivityType.IntegralConversion, skuMarketingId);
        assertEquals(0, count);
    }

    @Test
    public void testSelectUserJoinSepecialActivityCount() throws Exception {
        List<Long> squIds = list(2337L);
        Integer skuMarketingId = 1;
        int userId = 3;
        int count = orderRepository.selectUserJoinSepecialActivityCount(userId, squIds, ProductActivityType.IntegralConversion, skuMarketingId);
        assertEquals(0, count);

    }
}
