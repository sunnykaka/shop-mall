package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.BackGoodsLog;
import com.kariqu.tradecenter.domain.BackGoodsState;
import com.kariqu.tradecenter.repository.BackGoodsLogRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackGoodsLogRepositoryImpl extends SqlMapClientDaoSupport implements BackGoodsLogRepository {

	public void insert(BackGoodsLog record) {
		getSqlMapClientTemplate().insert("insertBackGoodsLog", record);
	}

	public int update(BackGoodsLog record) {
		return getSqlMapClientTemplate().update("updateBackGoodsLogSelective", record);
	}

	public List<BackGoodsLog> selectByBackId(Long backId) {
        Map map = new HashMap();
        map.put("backGoodsId", backId);
		return getSqlMapClientTemplate().queryForList("selectBackGoodsLogByBackId", map);
	}

    public BackGoodsLog selectByState(Long backId, BackGoodsState backState) {
        Map map = new HashMap();
        map.put("backGoodsId", backId);
        map.put("backState", backState);
        return (BackGoodsLog) getSqlMapClientTemplate().queryForObject("selectBackGoodsLogByState", map);
    }

    public BackGoodsLog selectRecentLogByOrderId(Long orderId) {
        return (BackGoodsLog) getSqlMapClientTemplate().queryForObject("selectRecentLogByOrderIds", orderId);
    }

	public int deleteByBackId(Long backId) {
		return getSqlMapClientTemplate().delete("deleteByBackId", backId);
	}

}