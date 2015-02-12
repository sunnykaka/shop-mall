package com.kariqu.productcenter.service;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * User: alec
 * Date: 13-8-1
 * Time: 下午3:56
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class BrowsingHistoryServiceTest extends UnitilsJUnit4 {

    @SpringBean("browsHistoryService")
    private BrowsingHistoryService browsHistoryService;

    @Test
    public void testService() {

        browsHistoryService.addBrowsHistory(1, "11111111-4x4c-4c37-bf4f-f1189d171xxx", 10);
        browsHistoryService.addBrowsHistory(1, "11111111-4x4c-4c37-bf4f-f1189d171xxx", 2);

        //------------未登录时---------------
        String trackId = "x2x5d60e-4x4c-4c37-bf4f-f1189d171xxx";
        browsHistoryService.addBrowsHistory(0, trackId, 1);
        browsHistoryService.addBrowsHistory(0, trackId, 2);
        browsHistoryService.addBrowsHistory(0, trackId, 3);
        browsHistoryService.addBrowsHistory(0, trackId, 4);
        browsHistoryService.addBrowsHistory(0, trackId, 5);
        browsHistoryService.addBrowsHistory(0, trackId, 6);

        //非已浏览的商品详情页
        assertEquals(6, browsHistoryService.queryBrowsHistoryByTrackIdWithOutProductId(trackId, 0, 10).size());

        //商品详情页
        assertEquals(5, browsHistoryService.queryBrowsHistoryByTrackIdWithOutProductId(trackId, 1, 10).size());

        //------------用户登录时---------------
        //合并浏览历史
        browsHistoryService.syncBrowsHistoryWhenLogin(1, trackId);

        assertEquals(6, browsHistoryService.queryBrowsHistoryByTrackIdWithOutProductId(trackId, 0, 10).size());

        assertEquals(7, browsHistoryService.queryBrowsHistoryByUserIdWithOutProductId(1, 0, 10).size());

        //------------用户登录后---------------
        //登录后加入相同的浏览商品
        browsHistoryService.addBrowsHistory(1, trackId, 1);
        assertEquals(6, browsHistoryService.queryBrowsHistoryByTrackIdWithOutProductId(trackId, 0, 10).size());
        //不同商品
        browsHistoryService.addBrowsHistory(1, trackId, 7);
        assertEquals(7, browsHistoryService.queryBrowsHistoryByTrackIdWithOutProductId(trackId, 0, 10).size());
        assertEquals(8, browsHistoryService.queryBrowsHistoryByUserIdWithOutProductId(1, 0, 10).size());

    }
}
