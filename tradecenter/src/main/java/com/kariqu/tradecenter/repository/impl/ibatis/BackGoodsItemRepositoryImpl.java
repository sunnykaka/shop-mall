package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.BackGoodsItem;
import com.kariqu.tradecenter.domain.OrderItem;
import com.kariqu.tradecenter.repository.BackGoodsItemRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackGoodsItemRepositoryImpl extends SqlMapClientDaoSupport implements BackGoodsItemRepository {

	public void insert(BackGoodsItem record) {
		getSqlMapClientTemplate().insert("insertBackGoodsItem", record);
	}

	public int updateBackGoodsItem(BackGoodsItem record) {
		return getSqlMapClientTemplate().update("updateBackGoodsItemSelective", record);
	}

	public List<BackGoodsItem> selectByBackGoodsId(Long backGoodsId) {
		return getSqlMapClientTemplate().queryForList("selectBackGoodsItemByBackGoodsId", backGoodsId);
	}

    public int selectByBackOrderItemId(Long backGoodsId, Long orderItemId) {
        Map map = new HashMap();
        map.put("orderItemId", orderItemId);
        map.put("backGoodsId", backGoodsId);
        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountByOrderItemIdAndBackGoodsId", map);
    }

	public int deleteByBackGoodsId(Long backId) {
		return getSqlMapClientTemplate().update("updateBackGoodsItemForDel", backId);
	}

}