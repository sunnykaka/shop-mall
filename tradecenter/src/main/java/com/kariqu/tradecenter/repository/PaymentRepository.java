package com.kariqu.tradecenter.repository;

import com.kariqu.tradecenter.domain.PaymentEvent;

import java.util.List;

/**
 * 支付订单仓库
 * User: Asion
 * Date: 11-10-14
 * Time: 下午2:50
 */
public interface PaymentRepository {

    void createPaymentEvent(PaymentEvent paymentEvent);

    List<PaymentEvent> queryPaymentEvents(long id);

    void deletePaymentEvents(long paymentId);
}
