package com.kariqu.productcenter.service.impl;

import com.kariqu.productcenter.domain.BrowsingHistory;
import com.kariqu.productcenter.repository.BrowsingHistoryRepository;
import com.kariqu.productcenter.service.BrowsingHistoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * User: Alec
 * Date: 13-8-1
 * Time: 上午11:56
 */
public class BrowsingHistoryServiceImpl implements BrowsingHistoryService {

    @Autowired
    private BrowsingHistoryRepository browsHistoryRepository;

    @Override
    public void addBrowsHistory(int userId, String trackId, int productId) {
        if (StringUtils.isEmpty(trackId) || productId < 1) return;

        browsHistoryRepository.insertBrowsHistory(new BrowsingHistory(userId, trackId, productId));
    }

    @Override
    public List<BrowsingHistory> queryBrowsHistoryByTrackIdWithOutProductId(String trackId, int productId, int limit) {
        if (StringUtils.isEmpty(trackId)) return Collections.emptyList();

        return browsHistoryRepository.queryTrackBrowsHistory(trackId, productId, limit);
    }

    @Override
    public List<BrowsingHistory> queryBrowsHistoryByUserIdWithOutProductId(int userId, int productId, int limit) {
        if (userId < 1) return Collections.emptyList();

        return browsHistoryRepository.queryBrowsHistoryByUserIdWithOutProductId(userId, productId, limit);
    }

    @Override
    public List<BrowsingHistory> queryRecentBrowsHistoryByUserId(int userId, int limit) {
        if (userId < 1) return Collections.emptyList();

        return browsHistoryRepository.queryRecentBrowsHistoryByUserId(userId, limit);
    }

    @Override
    public void syncBrowsHistoryWhenLogin(int userId, String trackId) {
        if (userId < 1 || StringUtils.isEmpty(trackId)) return;

        //同步浏览历史到登录状态
        browsHistoryRepository.syncBrowsHistoryWhenLogin(userId, trackId);
    }

    @Override
    public void delBrowsHistoryn(long id) {
        browsHistoryRepository.deleteBrowsHistoryById(id);
    }

    @Override
    public void delBrowsHistorynByUserId(int userId) {
        browsHistoryRepository.delBrowsHistorynByUserId(userId);
    }

}
