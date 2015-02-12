package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.repository.CommonModuleRepository;
import junit.framework.Assert;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-30 下午07:45:27
 */
public class CommonModuleRepositoryImplTest extends IbatisBaseSqlTest {

    @SpringBean("commonModuleRepository")
    private CommonModuleRepository commonModuleRepository;

    @Test
    public void testCommonModuleRepository() {
        CommonModule cm = new CommonModule();
        cm.setLanguage("english");
        cm.setModuleConfig("config");
        cm.setModuleConfigKey("key");
        cm.setModuleContent("module contesnt");
        cm.setModuleCssContent("css");
        cm.setName("name");
        cm.setVersion("vserion1");
        cm.setFormContent("form edit");
        cm.setLogicCode("goovyy");
        cm.setConfig("config");
        CommonModule cm1 = new CommonModule();
        cm1.setLanguage("english");
        cm1.setModuleConfig("config");
        cm1.setModuleConfigKey("key");
        cm1.setModuleContent("module contesnt");
        cm1.setModuleCssContent("css");
        cm1.setName("name2");
        cm1.setConfig("config");
        cm1.setVersion("vserion2");
        commonModuleRepository.createCommonModule(cm);
        commonModuleRepository.createCommonModule(cm1);
        Assert.assertEquals("english", commonModuleRepository.getCommonModuleById(cm.getId()).getLanguage());
        Assert.assertEquals("vserion1", commonModuleRepository.getCommonModuleById(cm.getId()).getVersion());
        Assert.assertEquals("vserion2", commonModuleRepository.queryCommonModuleByNameAndVersion("name2", "vserion2").getVersion());
        List<CommonModule> cts = commonModuleRepository.queryAllCommonModules();
        Assert.assertEquals(2, cts.size());
        List<Integer> ids = new ArrayList<Integer>();
        for (CommonModule c : cts)
            ids.add(c.getId());
        assertEquals(2, commonModuleRepository.queryCommonModuleByIds(ids).size());
        CommonModule cmd = cts.get(1);
        cmd.setLanguage("geg");
        cmd.setModuleConfig("gege");
        cmd.setModuleConfigKey("kekkk");
        cmd.setModuleContent("cccccccc");
        cmd.setModuleCssContent("cssdfsdfcs");
        cmd.setFormContent("form edit");
        cmd.setLogicCode("goovyy");
        cmd.setName("naananan");
        cmd.setVersion("vvvvvvvvvv");
        commonModuleRepository.updateCommonModule(cmd);
        CommonModule commonModule = commonModuleRepository.queryCommonModuleByName("name");
        assertEquals("name", commonModule.getName());
        commonModuleRepository.deleteCommonModuleById(cm.getId());
        commonModuleRepository.deleteCommonModuleById(cm1.getId());
        assertEquals(0, commonModuleRepository.queryAllCommonModules().size());
    }

}
