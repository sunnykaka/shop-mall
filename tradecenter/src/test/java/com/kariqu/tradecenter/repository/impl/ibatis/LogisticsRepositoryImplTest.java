package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.repository.LogisticsRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-10-14
 * Time: 下午5:31
 */
@SpringApplicationContext({"classpath:tradeContext.xml"})
public class LogisticsRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("logisticsRepository")
    private LogisticsRepository logisticsRepository;

    @Test
    public void testLogisticsRepository() {
        Logistics logistics = new Logistics();
        logistics.setOrderId(1);
        Address address = new Address();
        address.setName("谢中生");
        address.setEmail("vsmysee@gmail.com");
        address.setLocation("沁雅花园8栋2单元602");
        address.setMobile("333333333");
        address.setTelephone("53525252");
        address.setProvince("浙江省杭州市西湖区");
        address.setZipCode("3334343");
        logistics.setAddressId(1);
        logistics.setAddressOwner("owner");
        logistics.setMobile("33333");
        logistics.setName("name");
        logistics.setZipCode("zipcode");
        logistics.setEmail("geg@geg.com");
        logistics.setProvince("浙江");
        logistics.setLocation("location");
        logistics.setTelephone("telephone");

        DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setCost("22");
        deliveryInfo.setDeliveryTime(DeliveryInfo.DeliveryTime.Holiday);
        deliveryInfo.setDeliveryType(DeliveryInfo.DeliveryType.shentong);
        logistics.setDeliveryInfo(deliveryInfo);

        logisticsRepository.createLogistics(logistics);


        logistics = logisticsRepository.getLogistics(logistics.getId());
        logistics = logisticsRepository.getLogisticsByOrderId(1);

        LogisticsEvent logisticsEvent = new LogisticsEvent();
        logisticsEvent.setLogisticsId(logistics.getId());
        logisticsEvent.setDate(new Date());
        logisticsEvent.setOperator("客户");
        logisticsEvent.setEventInfo("货物到达杭州站");
        logisticsEvent.setBeforeState(OrderState.Create);
        logisticsEvent.setAfterState(OrderState.Pay);
        logisticsRepository.createLogisticsEvent(logisticsEvent);

        assertEquals(1, logisticsRepository.queryLogisticsEvents(logistics.getId()).size());

        LogisticsRedundancy logisticsRedundancy = new LogisticsRedundancy();
        logisticsRedundancy.setLocationRewrite("深圳");
        logisticsRedundancy.setId(logistics.getId());
        logisticsRepository.updateLogisticsRedundancy(logisticsRedundancy);
        logisticsRedundancy = logisticsRepository.queryLogisticsRedundancy(logistics.getId());
        assertEquals("深圳", logisticsRedundancy.getLocationRewrite());
        assertEquals(null, logisticsRedundancy.getProvinceRewrite());

        logistics = logisticsRepository.getLogistics(logistics.getId());
        logistics.setOrderId(2);
        logistics.setAddressId(2);
        logisticsRepository.updateLogistics(logistics);
        logisticsRepository.deleteLogisticsByOrderId(logistics.getOrderId());
        logisticsRepository.deleteLogistics(logistics.getId());
        logisticsRepository.deleteLogisticsEvents(logistics.getId());
    }
}
