package com.kariqu.tradecenter.repository;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.Logistics;
import com.kariqu.tradecenter.domain.LogisticsEvent;
import com.kariqu.tradecenter.domain.LogisticsRedundancy;

import java.util.List;

/**
 * 物流仓库
 * User: Asion
 * Date: 11-10-14
 * Time: 下午2:47
 */
public interface LogisticsRepository {

    void createLogistics(Logistics logistics);

    void updateLogistics(Logistics logistics);

    Logistics getLogistics(long id);

    Logistics getLogisticsByOrderId(long orderId);

    /**
     * 通过物流号查询对应的订单编号(最新的那个)
     *
     * @param waybillNumber 物流编号
     * @return 订单编号
     */
    long getOrderIdByLogisticsId(String waybillNumber);

    /**
     * 通过物流编号查询对应的所有订单编号
     *
     * @param waybillNumber
     * @return
     */
    List<Long> getOrderIdsByLogisticsId(String waybillNumber);

    /**
     * 修改订单物流单号
     * @param orderId
     * @param waybillNumber
     */
    void updateWaybillNumberByOrderId(long orderId, String waybillNumber);

    void deleteLogistics(long id);

    void createLogisticsEvent(LogisticsEvent logisticsEvent);

    List<LogisticsEvent> queryLogisticsEvents(long logisticsId);

    void deleteLogisticsEvents(long logisticsId);

    void deleteLogisticsByOrderId(long orderId);

    void updateLogisticsRedundancy(LogisticsRedundancy logisticsRedundancy);

    LogisticsRedundancy queryLogisticsRedundancy(long id);

    /**
     * 更改物流表中订单的物流公司信息
     *
     * @param orderId 订单编号
     * @param type    要更改的物流公司
     */
    void updateLogisticsDeliveryType(long orderId, DeliveryInfo.DeliveryType type);
}
