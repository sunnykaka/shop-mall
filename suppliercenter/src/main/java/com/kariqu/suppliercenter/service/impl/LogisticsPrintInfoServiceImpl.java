package com.kariqu.suppliercenter.service.impl;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.domain.LogisticsPrintInfo;
import com.kariqu.suppliercenter.repository.LogisticsPrintInfoRepository;
import com.kariqu.suppliercenter.service.LogisticsPrintInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-29
 * Time: 下午3:10
 */
public class LogisticsPrintInfoServiceImpl implements LogisticsPrintInfoService{
    @Autowired
    private LogisticsPrintInfoRepository logisticsPrintInfoRepository;

    @Override
    public List<LogisticsPrintInfo> queryAllLogisticsPrintInfoByCustomerId(int customerId) {
       return logisticsPrintInfoRepository.queryAllLogisticsPrintInfoByCustomerId(customerId);
    }

    @Override
    public LogisticsPrintInfo queryLogisticsPrintInfoByNameAndCustomerId(DeliveryInfo.DeliveryType name,int customerId) {
        return logisticsPrintInfoRepository.queryLogisticsPrintInfoByNameAndCustomerId(name,customerId);
    }

    @Override
    public LogisticsPrintInfo queryLogisticsPrintInfoById(int id) {
        return logisticsPrintInfoRepository.queryLogisticsPrintInfoById(id);
    }

    @Override
    public void updateLogisticsPrintInfo(LogisticsPrintInfo logisticsPrintInfo) {
       logisticsPrintInfoRepository.updateLogisticsPrintInfo(logisticsPrintInfo);
    }

    @Override
    public void createLogisticsPrintInfo(LogisticsPrintInfo logisticsPrintInfo) {
        if (queryLogisticsPrintInfoByNameAndCustomerId(logisticsPrintInfo.getName(), logisticsPrintInfo.getCustomerId()) != null) {
            throw new RuntimeException("添加过这个物流公司, 不需要重复添加");
        }
        logisticsPrintInfoRepository.createLogisticsPrintInfo(logisticsPrintInfo);
    }

    @Override
    public void deleteLogisticsPrintInfoById(int logisticsPrintInfoId) {
        logisticsPrintInfoRepository.deleteLogisticsPrintInfoById(logisticsPrintInfoId);
    }
}
