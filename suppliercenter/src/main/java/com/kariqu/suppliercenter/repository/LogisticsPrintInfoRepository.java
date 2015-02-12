package com.kariqu.suppliercenter.repository;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.domain.LogisticsPrintInfo;

import java.util.List;

/**
 * 物流公司打印
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-29
 * Time: 下午2:59
 */
public interface LogisticsPrintInfoRepository {

    List<LogisticsPrintInfo> queryAllLogisticsPrintInfoByCustomerId(int customerId);

    LogisticsPrintInfo queryLogisticsPrintInfoById(int id);

    LogisticsPrintInfo queryLogisticsPrintInfoByNameAndCustomerId(DeliveryInfo.DeliveryType name,int customerId);


    void updateLogisticsPrintInfo(LogisticsPrintInfo logisticsPrintInfo);

    void createLogisticsPrintInfo(LogisticsPrintInfo logisticsPrintInfo);

    void deleteLogisticsPrintInfoById(int logisticsPrintInfoId);
}
