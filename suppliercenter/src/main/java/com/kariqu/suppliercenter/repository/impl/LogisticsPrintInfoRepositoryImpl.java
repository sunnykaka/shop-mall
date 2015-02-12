package com.kariqu.suppliercenter.repository.impl;

import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.domain.LogisticsPrintInfo;
import com.kariqu.suppliercenter.repository.LogisticsPrintInfoRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:Wendy
 * @since:1.0.0 Date: 12-11-29
 * Time: 下午2:58
 */
public class LogisticsPrintInfoRepositoryImpl extends SqlMapClientDaoSupport implements LogisticsPrintInfoRepository{

    @Override
    public List<LogisticsPrintInfo> queryAllLogisticsPrintInfoByCustomerId(int customerId) {
        return getSqlMapClientTemplate().queryForList("selectAllLogisticsPrintInfoByCustomerId",customerId);
    }

    @Override
    public LogisticsPrintInfo queryLogisticsPrintInfoByNameAndCustomerId(DeliveryInfo.DeliveryType name,int customerId) {
        Map map=new HashMap();
        map.put("name",name);
        map.put("customerId",customerId);
        return (LogisticsPrintInfo) getSqlMapClientTemplate().queryForObject("selectLogisticsPrintInfoByNameAndCustomerId",map);
    }

    @Override
    public void updateLogisticsPrintInfo(LogisticsPrintInfo logisticsPrintInfo) {
        getSqlMapClientTemplate().update("updateLogisticsPrintInfo", logisticsPrintInfo);
    }

    @Override
    public void createLogisticsPrintInfo(LogisticsPrintInfo logisticsPrintInfo) {
        getSqlMapClientTemplate().insert("insertLogisticsPrintInfo", logisticsPrintInfo);
    }

    @Override
    public LogisticsPrintInfo queryLogisticsPrintInfoById(int id) {
        return (LogisticsPrintInfo) getSqlMapClientTemplate().queryForObject("selectLogisticsPrintInfoById", id);
    }

    @Override
    public void deleteLogisticsPrintInfoById(int id) {
        getSqlMapClientTemplate().delete("deleteLogisticsPrintInfoById",id);
    }
}
