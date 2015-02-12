package com.kariqu.tradecenter.service;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.Logistics;
import com.kariqu.tradecenter.domain.LogisticsEvent;
import com.kariqu.tradecenter.domain.LogisticsRedundancy;

import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-01-17 15:10
 * @since 1.0.0
 */
public interface LogisticsService {

    void createLogistics(Logistics logistics);

    void updateLogisticsRedundancy(LogisticsRedundancy logisticsRedundancy);

    void updateWaybillNumberByOrderId(long orderId, String waybillNumber);

    void createLogisticsEvent(LogisticsEvent logisticsEvent);

    List<LogisticsEvent> queryLogisticsEvents(long logisticsId);

    void deleteLogistics(long id);

    Logistics getLogistics(long id);

    void updateLogistics(Logistics logistics);

    LogisticsRedundancy queryLogisticsRedundancy(long id);

    void deleteLogisticsEvents(long logisticsId);

    void updateLogisticsDeliveryType(long orderId, DeliveryInfo.DeliveryType type);

    long getOrderIdByLogisticsId(String waybillNumber);

    /**
     * 通过物流编号查询对应的所有订单编号
     *
     * @param waybillNumber
     * @return
     */
    List<Long> getOrderIdsByLogisticsId(String waybillNumber);

    void deleteLogisticsByOrderId(long orderId);

    Logistics getLogisticsByOrderId(long orderId);

}
