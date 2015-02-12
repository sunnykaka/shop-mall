package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.AttentionInfo;
import com.kariqu.productcenter.repository.AttentionInfoRepository;
import com.kariqu.productcenter.service.AttentionInfoService;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-6-27
 * Time: 上午10:55
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class AttentionInfoRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("attentionInfoRepository")
    private AttentionInfoRepository attentionInfoRepository;

    @SpringBean("attentionInfoService")
    private AttentionInfoService attentionInfoService;


    @Test
    public void testProductRepository() {
        AttentionInfo attentionInfo=new AttentionInfo();
        attentionInfo.setInfo("允许出锅冷却再洗涤。");
        attentionInfo.setType(AttentionInfo.Type.Use);
        attentionInfo.setProductId(1);
        attentionInfoRepository.createAttention(attentionInfo);
        assertEquals(1,attentionInfoRepository.queryAttentionInfo(1,AttentionInfo.Type.Use.name()).size());
        assertEquals(0,attentionInfoRepository.queryAttentionInfo(1,AttentionInfo.Type.Maintenance.name()).size());

        attentionInfo.setInfo("aaaa");
        attentionInfoRepository.updateAttention(attentionInfo);
        assertEquals("aaaa",attentionInfoRepository.queryAttentionInfo(1,AttentionInfo.Type.Use.name()).get(0).getInfo());

        attentionInfoRepository.deleteAttentionById(1);
        assertEquals(0,attentionInfoRepository.queryAttentionInfo(1,AttentionInfo.Type.Use.name()).size());
    }

    @Test
    public void testAttentionService() {
        AttentionInfo attentionInfo=new AttentionInfo();
        attentionInfo.setInfo("允许出锅冷却再洗涤。");
        attentionInfo.setType(AttentionInfo.Type.Maintenance);
        attentionInfo.setProductId(1);
        attentionInfoService.createAttention(attentionInfo);
        assertEquals(1,attentionInfoService.queryAllMaintenanceByProductId(1).size());
        assertEquals(0,attentionInfoService.queryAllUseByProductId(1).size());
    }
}
