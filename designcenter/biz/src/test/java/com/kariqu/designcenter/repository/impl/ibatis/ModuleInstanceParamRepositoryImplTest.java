package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.ModuleInstanceParam;
import com.kariqu.designcenter.domain.model.ParamType;
import com.kariqu.designcenter.repository.ModuleInstanceParamRepository;
import org.junit.Assert;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

/**
 * @author Asion
 * @version 1.0.0
 * @since 2011-5-1 上午01:58:45
 */
public class ModuleInstanceParamRepositoryImplTest extends IbatisBaseSqlTest {

    @SpringBean("moduleInstanceParamRepository")
    private ModuleInstanceParamRepository moduleInstanceParamRepository;

    @Test
    public void testModuleInstanceParamRepository() {

        ModuleInstanceParam mip = new ModuleInstanceParam();
        mip.setModuleInstanceId("100");
        mip.setModulePrototypeId(1);
        mip.setPageId(2);
        mip.setParamName("name");
        mip.setParamValue("value");
        mip.setParamType(ParamType.FOOT);
        mip.setShopId(1);


        ModuleInstanceParam headfoot = new ModuleInstanceParam();
        headfoot.setModuleInstanceId("102");
        headfoot.setModulePrototypeId(1);
        headfoot.setParamName("name");
        headfoot.setParamValue("value");
        headfoot.setParamType(ParamType.FOOT);
        headfoot.setShopId(1);

        moduleInstanceParamRepository.createModuleInstanceParam(mip);
        moduleInstanceParamRepository.createModuleInstanceParam(headfoot);

        Assert.assertEquals(1, moduleInstanceParamRepository.queryModuleParamsByModuleInstanceId("100").size());
        Assert.assertEquals(1, moduleInstanceParamRepository.queryModuleParamsByModuleInstanceId("102").size());
        Assert.assertEquals("value", moduleInstanceParamRepository.getModuleInstanceParamById(mip.getId()).getParamValue());
        Assert.assertEquals(ParamType.FOOT, moduleInstanceParamRepository.queryModuleParamsByModuleInstanceId("100").get(0).getParamType());
        mip.setModuleInstanceId("101");
        mip.setModulePrototypeId(2);
        mip.setPageId(3);
        mip.setShopId(1);
        mip.setParamName("kkk");
        mip.setParamValue("vvvvv");
        moduleInstanceParamRepository.updateModuleInstanceParam(mip);
        Assert.assertEquals("vvvvv", moduleInstanceParamRepository.getModuleInstanceParamById(mip.getId()).getParamValue());
        Assert.assertEquals(1, moduleInstanceParamRepository.queryModuleParamsByModuleInstanceId("101").size());

        moduleInstanceParamRepository.deleteModuleInstanceParamOfSingleModule("101");
        moduleInstanceParamRepository.deletePageParamsByPageId(3);
        moduleInstanceParamRepository.deleteShopHeadAndFootParamByShopId(1);
        moduleInstanceParamRepository.deleteShopFootParamByShopId(1);
    }

}
