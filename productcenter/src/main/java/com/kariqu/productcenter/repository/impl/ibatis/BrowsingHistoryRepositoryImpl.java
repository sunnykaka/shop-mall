package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.BrowsingHistory;
import com.kariqu.productcenter.repository.BrowsingHistoryRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Alec
 * Date: 13-8-1
 * Time: 上午11:18
 */
public class BrowsingHistoryRepositoryImpl extends SqlMapClientDaoSupport implements BrowsingHistoryRepository {
    @Override
    public void insertBrowsHistory(BrowsingHistory browsingHistory) {
        this.getSqlMapClientTemplate().insert("insertBrowsHistory", browsingHistory);
    }

    @Override
    public void updateBrowsHistoryCreateDate(BrowsingHistory browsingHistory) {
        this.getSqlMapClientTemplate().update("updateBrowsHistoryCreateDate", browsingHistory);
    }

    @Override
    public boolean checkBrowsHistoryByUserIdAndProductId(int userId, int productId) {
        Map params = new HashMap();
        params.put("userId", userId);
        params.put("productId", productId);
        return null != this.getSqlMapClientTemplate().queryForObject("selectBrowsHistoryByUserIdAndProductId", params);
    }

    @Override
    public boolean checkBrowsHistoryByTrackIdAndProductId(String trackId, int productId) {
        Map params = new HashMap();
        params.put("trackId", trackId);
        params.put("productId", productId);
        return null != this.getSqlMapClientTemplate().queryForObject("selectBrowsHistoryByTrackIdAndProductId", params);
    }

    @Override
    public void syncBrowsHistoryWhenLogin(int userId, String trackId) {
        Map params = new HashMap();
        params.put("userId", userId);
        params.put("trackId", trackId);
        this.getSqlMapClientTemplate().update("syncBrowsHistoryWhenLogin", params);
    }

    @Override
    public List<BrowsingHistory> queryTrackBrowsHistory(String trackId, int productId, int limit) {
        Map params = new HashMap();
        params.put("trackId", trackId);
        params.put("limit", limit);
        params.put("productId", productId);
        return this.getSqlMapClientTemplate().queryForList("queryTrackBrowsHistory", params);
    }

    @Override
    public List<BrowsingHistory> queryBrowsHistoryByUserIdWithOutProductId(int userId, int productId, int limit) {
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("productId", productId);
        map.put("limit", limit);
        return getSqlMapClientTemplate().queryForList("queryBrowsHistoryByUserIdWithOutProductId", map);
    }

    @Override
    public List<BrowsingHistory> queryRecentBrowsHistoryByUserId(int userId, int limit) {
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("limit", limit);
        return getSqlMapClientTemplate().queryForList("queryRecentBrowsHistoryByUserId", map);
    }

    @Override
    public void deleteBrowsHistoryById(long id) {
        this.getSqlMapClientTemplate().delete("deleteBrowsHistoryById", id);
    }

    @Override
    public void delBrowsHistorynByUserId(int userId) {
        Map params = new HashMap();
        params.put("userId", userId);
        this.getSqlMapClientTemplate().delete("deleteBrowsHistoryByUserId", params);
    }

    /**
     * 未登录状态删除游客的历史记录  条件中userId为0
     *
     * @param trackId
     * @param productId
     */
    @Override
    public void deleteBrowsHistoryByTrackIdAndProductId(String trackId, int productId) {
        Map params = new HashMap();
        params.put("trackId", trackId);
        params.put("productId", productId);
        this.getSqlMapClientTemplate().delete("deleteBrowsHistoryByTrackIdAndProductId", params);
    }

    @Override
    public void deleteBrowsHistoryByUserIdAndProductId(int userId, int productId) {
        Map params = new HashMap();
        params.put("userId", userId);
        params.put("productId", productId);
        this.getSqlMapClientTemplate().delete("deleteBrowsHistoryByUserIdAndProductId", params);
    }

}
