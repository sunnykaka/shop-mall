package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.LogisticsInfo;
import com.kariqu.tradecenter.repository.OperaLogisticsRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * @author Athens(刘杰)
 * @Time 2012-09-25 15:25
 * @since 1.0.0
 */
public class OperaLogisticsRepositoryImpl extends SqlMapClientDaoSupport implements OperaLogisticsRepository {

    public LogisticsInfo select(String number) {
        return (LogisticsInfo) getSqlMapClientTemplate().queryForObject("selectLogisticsInfo", number);
    }

    public void insert(LogisticsInfo info) {
        getSqlMapClientTemplate().insert("insertLogisticsInfo", info);
    }

    public void update(LogisticsInfo info) {
        getSqlMapClientTemplate().update("updateLogisticsInfo", info);
    }

    public void delete(String number) {
        getSqlMapClientTemplate().delete("deleteLogisticsInfo", number);
    }

}
