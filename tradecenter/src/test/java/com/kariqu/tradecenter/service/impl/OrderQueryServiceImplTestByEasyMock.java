package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.json.JsonUtil;
import com.kariqu.tradecenter.domain.Logistics;
import com.kariqu.tradecenter.domain.LogisticsInfo;
import com.kariqu.tradecenter.domain.OrderStateHistory;
import com.kariqu.tradecenter.domain.ProgressDetail;
import com.kariqu.tradecenter.repository.OrderRepository;
import com.kariqu.tradecenter.service.LogisticsService;
import com.kariqu.tradecenter.service.OperateLogisticsService;
import com.kariqu.tradecenter.service.OrderQueryService;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.io.annotation.FileContent;

import java.util.Arrays;
import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-02-27 13:57
 * @since 1.0.0
 */
public class OrderQueryServiceImplTestByEasyMock extends UnitilsJUnit4 {

    @TestedObject
    private OrderQueryService orderQueryService = new OrderQueryServiceImpl();

    @Mock
    @InjectIntoByType
    private OrderRepository orderRepository;

    @Mock
    @InjectIntoByType
    private OperateLogisticsService operateLogisticsService;

    @Mock
    @InjectIntoByType
    private LogisticsService logisticsService;


    @FileContent(value = "history.js", encoding = "UTF-8")
    private String historyDb;
    @FileContent(value = "logistics.js", encoding = "UTF-8")
    private String logisticsDb;
    @FileContent(value = "logisticsInfo.js", encoding = "UTF-8")
    private String logisticsInfoDb;

    // 供测试多个方法使用
    private long orderId = 188;
    private List<OrderStateHistory> historyList;
    private Logistics logistics;
    private LogisticsInfo logisticsInfo;


    @Before
    public void init() {
        historyList = Arrays.asList(JsonUtil.json2Object(historyDb, OrderStateHistory[].class));
        logistics = JsonUtil.json2Object(logisticsDb, Logistics.class);
        logisticsInfo = JsonUtil.json2Object(logisticsInfoDb, LogisticsInfo.class);
    }

    @Test
    public void testLogisticsInfo() {
        // 只调用一次, 不指定会隐式执行 once() 方法
        EasyMock.expect(orderRepository.queryUserModeOrderStateHistory(orderId)).andReturn(historyList);
        // 只能调用 1 次
        EasyMock.expect(logisticsService.getLogisticsByOrderId(orderId)).andReturn(logistics).times(1);
        // 可以调用 任意次
        EasyMock.expect(operateLogisticsService.queryLogistics(logistics.getDeliveryInfo().getWaybillNumber())).andReturn(logisticsInfo).anyTimes();

        // 准备好需要的数据
        EasyMock.replay(orderRepository, logisticsService);
        EasyMock.replay(operateLogisticsService);

        // 调用
        List<ProgressDetail> detailList = orderQueryService.queryProgressDetail(orderId, 0);

        System.out.println("\nprogress: " + detailList);

        // 检查. 若要求其调用两次, 却只被调用一次, 则会有错误.
        EasyMock.verify(orderRepository, logisticsService, operateLogisticsService);
    }

}
