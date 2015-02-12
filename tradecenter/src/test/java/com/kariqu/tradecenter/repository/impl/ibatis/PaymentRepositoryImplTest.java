package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.OrderState;
import com.kariqu.tradecenter.domain.PaymentEvent;
import com.kariqu.tradecenter.repository.PaymentRepository;
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
public class PaymentRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("paymentRepository")
    private PaymentRepository paymentRepository;

    @Test
    public void testPaymentRepository() {

        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setDate(new Date());
        paymentEvent.setEventInfo("支付成功");
        paymentEvent.setOperator("中国银行");
        paymentEvent.setOrderId(1);
        paymentEvent.setBeforeState(OrderState.Create);
        paymentEvent.setAfterState(OrderState.Pay);
        paymentRepository.createPaymentEvent(paymentEvent);

        assertEquals(1, paymentRepository.queryPaymentEvents(1).size());
    }
}

