package com.kariqu.productcenter.repository;

import com.kariqu.productcenter.domain.BrowsingHistory;

import java.util.List;

/**
 * User: Alec
 * Date: 13-8-1
 * Time: 上午11:04
 */
public interface BrowsingHistoryRepository {

    void insertBrowsHistory(BrowsingHistory browsingHistory);

    void updateBrowsHistoryCreateDate(BrowsingHistory browsingHistory);

    boolean checkBrowsHistoryByUserIdAndProductId(int userId, int productId);

    boolean checkBrowsHistoryByTrackIdAndProductId(String trackId, int productId);

    void syncBrowsHistoryWhenLogin(int userId, String trackId);

    List<BrowsingHistory> queryTrackBrowsHistory(String trackId, int productId, int limit);

    /**
     * 根据用户Id 查询浏览历史(排除指定的商品Id, 若不需要排除, 使用 0)
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

    void deleteBrowsHistoryById(long id);

    void delBrowsHistorynByUserId(int userId);

    void deleteBrowsHistoryByTrackIdAndProductId(String trackId, int productId);

    void deleteBrowsHistoryByUserIdAndProductId(int userId, int productId);
}
