package com.kariqu.tradecenter.service.impl;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.Logistics;
import com.kariqu.tradecenter.domain.LogisticsEvent;
import com.kariqu.tradecenter.domain.LogisticsRedundancy;
import com.kariqu.tradecenter.repository.LogisticsRepository;
import com.kariqu.tradecenter.service.LogisticsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-01-17 15:11
 * @since 1.0.0
 */
public class LogisticsServiceImpl implements LogisticsService {

    @Autowired
    private LogisticsRepository logisticsRepository;

    public void createLogistics(Logistics logistics) {
        logisticsRepository.createLogistics(logistics);
    }

    public void updateLogisticsRedundancy(LogisticsRedundancy logisticsRedundancy) {
        logisticsRepository.updateLogisticsRedundancy(logisticsRedundancy);
    }

    @Override
    public void updateWaybillNumberByOrderId(long orderId, String waybillNumber) {
        logisticsRepository.updateWaybillNumberByOrderId(orderId, waybillNumber);
    }

    public void createLogisticsEvent(LogisticsEvent logisticsEvent) {
        logisticsRepository.createLogisticsEvent(logisticsEvent);
    }

    public List<LogisticsEvent> queryLogisticsEvents(long logisticsId) {
        return logisticsRepository.queryLogisticsEvents(logisticsId);
    }

    public void deleteLogistics(long id) {
        logisticsRepository.deleteLogistics(id);
    }

    public Logistics getLogistics(long id) {
        return logisticsRepository.getLogistics(id);
    }

    public void updateLogistics(Logistics logistics) {
        logisticsRepository.updateLogistics(logistics);
    }

    public LogisticsRedundancy queryLogisticsRedundancy(long id) {
        return logisticsRepository.queryLogisticsRedundancy(id);
    }

    public void deleteLogisticsEvents(long logisticsId) {
        logisticsRepository.deleteLogisticsEvents(logisticsId);
    }

    public void updateLogisticsDeliveryType(long orderId, DeliveryInfo.DeliveryType type) {
        logisticsRepository.updateLogisticsDeliveryType(orderId, type);
    }

    public long getOrderIdByLogisticsId(String waybillNumber) {
        return logisticsRepository.getOrderIdByLogisticsId(waybillNumber);
    }

    public List<Long> getOrderIdsByLogisticsId(String waybillNumber) {
        return logisticsRepository.getOrderIdsByLogisticsId(waybillNumber);
    }

    public void deleteLogisticsByOrderId(long orderId) {
        logisticsRepository.deleteLogisticsByOrderId(orderId);
    }

    public Logistics getLogisticsByOrderId(long orderId) {
        return logisticsRepository.getLogisticsByOrderId(orderId);
    }
}
