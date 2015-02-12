package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.BrowsingHistory;
import com.kariqu.productcenter.repository.BrowsingHistoryRepository;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * User: alec
 * Date: 13-8-1
 * Time: 下午2:46
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class BrowsingHistoryRepositoryTest extends UnitilsJUnit4 {


    @SpringBean("browsHistoryRepository")
    private BrowsingHistoryRepository browsHistoryRepository;

    @Rollback(false)
    @Test
    public void testRepository() {
        int userId = 1, productId = 1;
        //游客身份
        String trackId = "x2x5d60e-4x4c-4c37-bf4f-f1189d171xxx";
        String trackUserId = "a2aaaaaa-4x4c-4c37-bf4f-f1189d171xxx";

        BrowsingHistory browsingHistory = new BrowsingHistory();
        browsingHistory.setProductId(productId);

        browsingHistory.setTrackId(trackId);
        browsHistoryRepository.insertBrowsHistory(browsingHistory);

        //更新创建日期
        browsHistoryRepository.updateBrowsHistoryCreateDate(browsingHistory);

        assertEquals(true, browsHistoryRepository.checkBrowsHistoryByTrackIdAndProductId(trackId, productId));

        browsingHistory.setTrackId(trackUserId);
        browsHistoryRepository.insertBrowsHistory(browsingHistory);
        assertEquals(1, browsHistoryRepository.queryTrackBrowsHistory(trackId, 0, 10).size());

        // 逻辑删除
        browsHistoryRepository.deleteBrowsHistoryById(browsingHistory.getId());
        // 物理删除
        browsHistoryRepository.deleteBrowsHistoryByTrackIdAndProductId(trackId, productId);
        assertEquals(false, browsHistoryRepository.checkBrowsHistoryByTrackIdAndProductId(trackId, productId));

        //用户登录
        int proId = 2;
        browsingHistory.setTrackId(trackUserId);
        browsingHistory.setProductId(proId);
        browsingHistory.setUserId(userId);
        browsHistoryRepository.insertBrowsHistory(browsingHistory);

        assertEquals(true, browsHistoryRepository.checkBrowsHistoryByUserIdAndProductId(userId, proId));

        browsHistoryRepository.syncBrowsHistoryWhenLogin(userId, trackUserId);

        assertEquals(1, browsHistoryRepository.queryBrowsHistoryByUserIdWithOutProductId(userId, 0, 10).size());
    }

}
