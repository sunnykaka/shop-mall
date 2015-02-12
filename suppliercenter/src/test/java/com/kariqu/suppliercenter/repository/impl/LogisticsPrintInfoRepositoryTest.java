package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.domain.LogisticsPrintInfo;
import com.kariqu.suppliercenter.repository.LogisticsPrintInfoRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.assertEquals;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-29
 * Time: 下午3:16
 */
@ContextConfiguration(locations = {"/supplierCenter.xml"})
public class LogisticsPrintInfoRepositoryTest  extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private LogisticsPrintInfoRepository logisticsPrintInfoRepository;

    @Test
    @Rollback(false)
    public void testLogisticsPrintInfoRepository() {
        LogisticsPrintInfo logisticsPrintInfo=new LogisticsPrintInfo();
        logisticsPrintInfo.setLaw(2);
        logisticsPrintInfo.setCustomerId(1);
        logisticsPrintInfo.setName(DeliveryInfo.DeliveryType.ems);
        logisticsPrintInfo.setPrintHtml("fdd");
        logisticsPrintInfoRepository.createLogisticsPrintInfo(logisticsPrintInfo);

        assertEquals("fdd",logisticsPrintInfoRepository.queryLogisticsPrintInfoByNameAndCustomerId(logisticsPrintInfo.getName(),logisticsPrintInfo.getCustomerId()).getPrintHtml());

        logisticsPrintInfo.setPrintHtml("LODOP");

        logisticsPrintInfoRepository.updateLogisticsPrintInfo(logisticsPrintInfo);
        assertEquals("LODOP",logisticsPrintInfoRepository.queryLogisticsPrintInfoByNameAndCustomerId(logisticsPrintInfo.getName(),logisticsPrintInfo.getCustomerId()).getPrintHtml());
        logisticsPrintInfoRepository.createLogisticsPrintInfo(logisticsPrintInfo);
        logisticsPrintInfoRepository.createLogisticsPrintInfo(logisticsPrintInfo);
        assertEquals(3,logisticsPrintInfoRepository.queryAllLogisticsPrintInfoByCustomerId(logisticsPrintInfo.getCustomerId()).size());
        logisticsPrintInfoRepository.deleteLogisticsPrintInfoById(logisticsPrintInfo.getId());
        assertEquals(2,logisticsPrintInfoRepository.queryAllLogisticsPrintInfoByCustomerId(logisticsPrintInfo.getCustomerId()).size());
    }

}
