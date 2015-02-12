package com.kariqu.suppliercenter.service;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.domain.LogisticsPrintInfo;

import java.util.List;

/**
 * 打印物流信息对象
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-29
 * Time: 下午3:10
 */
public interface LogisticsPrintInfoService {

    List<LogisticsPrintInfo> queryAllLogisticsPrintInfoByCustomerId(int customerId);

    LogisticsPrintInfo queryLogisticsPrintInfoByNameAndCustomerId(DeliveryInfo.DeliveryType name,int customerId);

    LogisticsPrintInfo queryLogisticsPrintInfoById(int id);

    void updateLogisticsPrintInfo(LogisticsPrintInfo logisticsPrintInfo);

    void createLogisticsPrintInfo(LogisticsPrintInfo logisticsPrintInfo);

    void deleteLogisticsPrintInfoById(int logisticsPrintInfoId);
}
