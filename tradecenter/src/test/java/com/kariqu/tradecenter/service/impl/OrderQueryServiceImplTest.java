package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.json.JsonUtil;
import com.kariqu.tradecenter.domain.Logistics;
import com.kariqu.tradecenter.domain.LogisticsInfo;
import com.kariqu.tradecenter.domain.OrderStateHistory;
import com.kariqu.tradecenter.repository.OrderRepository;
import com.kariqu.tradecenter.service.LogisticsService;
import com.kariqu.tradecenter.service.OperateLogisticsService;
import com.kariqu.tradecenter.service.OrderQueryService;
import org.unitils.UnitilsJUnit3;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.io.annotation.FileContent;
import org.unitils.mock.Mock;

import java.util.Arrays;
import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-02-27 13:57
 * @since 1.0.0
 */
public class OrderQueryServiceImplTest extends UnitilsJUnit3 {

    @TestedObject
    private OrderQueryService orderQueryService = new OrderQueryServiceImpl();

    @InjectIntoByType
    private Mock<OrderRepository> orderRepository;

    @InjectIntoByType
    private Mock<OperateLogisticsService> operateLogisticsService;

    @InjectIntoByType
    private Mock<LogisticsService> logisticsService;


    // 需要准备的数据
    @FileContent(value = "history.js", encoding = "UTF-8")
    private String historyDb;
    @FileContent(value = "logistics.js", encoding = "UTF-8")
    private String logisticsDb;
    @FileContent(value = "logisticsInfo.js", encoding = "UTF-8")
    private String logisticsInfoDb;

    private long orderId = 188;
    private List<OrderStateHistory> historyList;
    private Logistics logistics;
    private LogisticsInfo logisticsInfo;


    @Override
    public void setUp() {
        historyList = Arrays.asList(JsonUtil.json2Object(historyDb, OrderStateHistory[].class));
        logistics = JsonUtil.json2Object(logisticsDb, Logistics.class);
        logisticsInfo = JsonUtil.json2Object(logisticsInfoDb, LogisticsInfo.class);
    }

    public void testLogisticsInfo() {
        orderRepository.returns(historyList).queryUserModeOrderStateHistory(orderId);
        logisticsService.returns(logistics).getLogisticsByOrderId(orderId);
        operateLogisticsService.returns(logisticsInfo).queryLogistics(logistics.getDeliveryInfo().getWaybillNumber());

        System.out.println("\nprogress: " + orderQueryService.queryProgressDetail(orderId, 0));
    }

}
