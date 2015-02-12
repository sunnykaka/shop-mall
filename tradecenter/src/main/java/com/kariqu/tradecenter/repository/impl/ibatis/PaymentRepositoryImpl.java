package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.PaymentEvent;
import com.kariqu.tradecenter.repository.PaymentRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

/**
 * User: Asion
 * Date: 11-10-14
 * Time: 下午2:54
 */
public class PaymentRepositoryImpl extends SqlMapClientDaoSupport implements PaymentRepository {


    @Override
    public void createPaymentEvent(PaymentEvent paymentEvent) {
        getSqlMapClientTemplate().insert("insertPaymentEvent", paymentEvent);
    }

    @Override
    public List<PaymentEvent> queryPaymentEvents(long id) {
        return getSqlMapClientTemplate().queryForList("selectPaymentEvent", id);
    }

    @Override
    public void deletePaymentEvents(long paymentId) {
        getSqlMapClientTemplate().delete("deletePaymentEvents", paymentId);
    }
}
