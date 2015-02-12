package com.kariqu.productcenter.service;

import com.kariqu.productcenter.domain.BrowsingHistory;

import java.util.List;

/**
 * User: Alec
 * Date: 13-8-1
 * Time: 上午11:28
 */
public interface BrowsingHistoryService {
    /**
     * 添加浏览历史
     */
    void addBrowsHistory(int userId, String trackId, int productId);

    /**
     * 查询浏览历史记录(跟踪Id), 除了指定的 商品Id, 若指定的商品Id 为 0 则忽略
     *
     * @param trackId
     * @param limit
     * @return
     */
    List<BrowsingHistory> queryBrowsHistoryByTrackIdWithOutProductId(String trackId, int productId, int limit);

    /**
     * 查询指定用户的浏览历史. 除了指定的商品Id, 若指定的商品Id 为 0 则忽略
     *
     * @param userId
     * @param productId
     * @param limit
     * @return
     */
    List<BrowsingHistory> queryBrowsHistoryByUserIdWithOutProductId(int userId, int productId, int limit);

    /**
     * 查询出用户指定时间以内的浏览记录
     * @param userId
     * @return
     */
    List<BrowsingHistory> queryRecentBrowsHistoryByUserId(int userId, int limit);

    /**
     * 用户登录后同步浏览历史记录
     *
     * @param userId
     * @param trackId
     */
    void syncBrowsHistoryWhenLogin(int userId, String trackId);

    void delBrowsHistoryn(long id);

    /**
     * 删除用户下所有浏览记录
     * @param userId
     */
    void delBrowsHistorynByUserId(int userId);

}
