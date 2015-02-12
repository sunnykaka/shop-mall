package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.BackGoods;
import com.kariqu.tradecenter.domain.BackGoodsState;
import com.kariqu.tradecenter.repository.BackGoodsRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackGoodsRepositoryImpl extends SqlMapClientDaoSupport implements BackGoodsRepository {

    public void insert(BackGoods record) {
        getSqlMapClientTemplate().insert("insertBackGoods", record);
    }

    public int update(BackGoods record) {
        return getSqlMapClientTemplate().update("updateBackGoodsSelective", record);
    }

    public int selectCountByState(BackGoodsState state) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountForBackGoods", state);
    }

    public int selectCountByFinance() {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectCountForFinance");
    }

    public List<BackGoods> selectForFinance() {
        return getSqlMapClientTemplate().queryForList("selectForFinance");
    }

    public BackGoods select(Long backGoodsId) {
        return (BackGoods) getSqlMapClientTemplate().queryForObject("selectBackGoodsById", backGoodsId);
    }

    public BackGoods select(Integer userId, Long backGoodsId){
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("id", backGoodsId);
        return (BackGoods) getSqlMapClientTemplate().queryForObject("selectBackGoodsByIdAndUserId", map);
    }

    public List<BackGoods> selectByUserId(Integer userId) {
        return getSqlMapClientTemplate().queryForList("selectBackGoodsByUserId", userId);
    }

    public List<BackGoods> selectByOrderId(long orderId) {
        return getSqlMapClientTemplate().queryForList("selectBackGoodsByOrderId", orderId);
    }

    public List<BackGoods> selectByOrderNo(long orderNo, int userId) {
        Map map = new HashMap();
        map.put("orderNo", orderNo);
        map.put("userId", userId);
        return getSqlMapClientTemplate().queryForList("selectBackGoodsByOrderNo", map);
    }

    public List<Long> selectIdByOrderNo(long orderNo, int userId) {
        Map map = new HashMap();
        map.put("orderNo", orderNo);
        map.put("userId", userId);
        return getSqlMapClientTemplate().queryForList("selectBackGoodsIdByOrderNo", map);
    }

    public Page<BackGoods> queryBackGoodsByUserIdPage(Integer userId, Page<BackGoods> page) {
        int totalCount = (Integer) this.getSqlMapClientTemplate().queryForObject("queryCountBackGoodsByUserIdPage", userId);
        if(page.getStart() >= totalCount){
           page.setPageNo(1);
        }
        page.setTotalCount(totalCount);
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("start", page.getPageFirst());
        map.put("limit", page.getPageSize());
        List<BackGoods> list = getSqlMapClientTemplate().queryForList("queryBackGoodsByUserIdPage", map);
        page.setResult(list);
        return page;
    }

    public List<BackGoods> selectByState(BackGoodsState state) {
        return getSqlMapClientTemplate().queryForList("selectBackGoodsByState", state);
    }

    public int delete(Long backGoodsId) {
        // deleteBackGoods
        return getSqlMapClientTemplate().update("updateBackGoodsForDelete", backGoodsId);
    }

}