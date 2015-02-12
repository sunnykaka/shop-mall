package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.tradecenter.domain.Logistics;
import com.kariqu.tradecenter.domain.LogisticsEvent;
import com.kariqu.tradecenter.domain.LogisticsRedundancy;
import com.kariqu.tradecenter.repository.LogisticsRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-10-14
 * Time: 下午2:53
 */
public class LogisticsRepositoryImpl extends SqlMapClientDaoSupport implements LogisticsRepository {
    @Override
    public void createLogistics(Logistics logistics) {
        getSqlMapClientTemplate().insert("insertLogistics", logistics);
    }

    @Override
    public Logistics getLogistics(long id) {
        return (Logistics) getSqlMapClientTemplate().queryForObject("selectLogistics", id);
    }

    @Override
    public void updateLogistics(Logistics logistics) {
        getSqlMapClientTemplate().update("updateLogistics", logistics);
    }

    @Override
    public Logistics getLogisticsByOrderId(long orderId) {
        return (Logistics) getSqlMapClientTemplate().queryForObject("selectLogisticsByOrderId", orderId);
    }

    @Override
    public long getOrderIdByLogisticsId(String waybillNumber) {
        Object obj = getSqlMapClientTemplate().queryForObject("selectOrderIdByLogisticsId", waybillNumber);
        // 避免类型转换时 NullPointException
        long orderId = 0l;
        if (obj != null)
            orderId = (Long) obj;
        return orderId;
    }

    @Override
    public List<Long> getOrderIdsByLogisticsId(String waybillNumber) {
        return (List<Long>) getSqlMapClientTemplate().queryForList("selectOrderIdsByLogisticsId", waybillNumber);
    }

    @Override
    public void updateWaybillNumberByOrderId(long orderId, String waybillNumber) {
        Map param = new HashMap();
        param.put("orderId", orderId);
        param.put("waybillNumber", waybillNumber);
        getSqlMapClientTemplate().update("updateWaybillNumberByOrderId", param);
    }

    @Override
    public void deleteLogistics(long id) {
        getSqlMapClientTemplate().delete("deleteLogistics", id);
    }

    @Override
    public void createLogisticsEvent(LogisticsEvent logisticsEvent) {
        getSqlMapClientTemplate().insert("insertLogisticsEvent", logisticsEvent);
    }

    @Override
    public List<LogisticsEvent> queryLogisticsEvents(long logisticsId) {
        return getSqlMapClientTemplate().queryForList("selectLogisticsEvent", logisticsId);
    }

    @Override
    public void deleteLogisticsEvents(long logisticsId) {
        getSqlMapClientTemplate().delete("deleteLogisticsEvents", logisticsId);
    }

    @Override
    public void deleteLogisticsByOrderId(long orderId) {
        getSqlMapClientTemplate().delete("deleteLogisticsByOrderId", orderId);
    }

    @Override
    public void updateLogisticsRedundancy(LogisticsRedundancy logisticsRedundancy) {
        getSqlMapClientTemplate().update("updateLogisticsRedundancy", logisticsRedundancy);
    }

    @Override
    public LogisticsRedundancy queryLogisticsRedundancy(long id) {
        return (LogisticsRedundancy) getSqlMapClientTemplate().queryForObject("queryLogisticsRedundancy", id);
    }

    public void updateLogisticsDeliveryType(long orderId, DeliveryInfo.DeliveryType type) {
        Map map = new HashMap();
        map.put("orderId", orderId);
        map.put("deliveryType", type);
        getSqlMapClientTemplate().update("updateLogisticsDeliveryType", map);
    }
}
