package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.TemplateModule;
import com.kariqu.designcenter.repository.TemplateModuleRepository;
import junit.framework.Assert;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

/**
 * @author Asion
 * @since 2011-4-30 下午11:04:30
 * @version 1.0.0
 */
public class TemplateModuleRepositoryTest extends IbatisBaseSqlTest {
    
    @SpringBean("templateModuleRepository")
    private TemplateModuleRepository templateModuleRepository;
    
    @Test
    public void testTemplateModuleRepository() {
        TemplateModule templateModule = new TemplateModule();
        templateModule.setLanguage("vm");
        templateModule.setModuleConfig("config");
        templateModule.setModuleConfigKey("key");
        templateModule.setModuleContent("content");
        templateModule.setModuleCssContent("css");
        templateModule.setName("modulename");
        templateModule.setTemplateVersionId(1);
        templateModule.setVersion("version");
        templateModule.setConfig("isCacheable=false");
        TemplateModule templateModule1 = new TemplateModule();
        templateModule1.setLanguage("vm");
        templateModule1.setModuleConfig("config");
        templateModule1.setModuleConfigKey("key");
        templateModule1.setModuleContent("content");
        templateModule1.setModuleCssContent("css");
        templateModule1.setName("modulename2");
        templateModule1.setTemplateVersionId(1);
        templateModule1.setVersion("version");
        templateModule1.setConfig("ddds");
        this.templateModuleRepository.createTemplateModule(templateModule);
        this.templateModuleRepository.createTemplateModule(templateModule1);
        Assert.assertEquals("vm", templateModuleRepository.getTemplateModuleById(templateModule.getId()).getLanguage());
        Assert.assertEquals(false, templateModuleRepository.getTemplateModuleById(templateModule.getId()).isCacheable());
        Assert.assertEquals(true, templateModuleRepository.getTemplateModuleById(templateModule1.getId()).isCacheable());
        Assert.assertEquals(2, templateModuleRepository.queryAllTemplateModules().size());
        Assert.assertEquals(2, templateModuleRepository.queryTemplateModulesByTemplateVersionId(1).size());
        Page<TemplateModule> page = new Page<TemplateModule>(1,2);
        templateModuleRepository.queryTemplateModulesByTemplateVersionIdAndPage(1, page);
        Assert.assertEquals(2,page.getTotalCount());
        Assert.assertEquals(2,page.getResult().size());
        templateModule.setLanguage("vmgdwg");
        templateModule.setModuleConfig("conegwgfig");
        templateModule.setModuleConfigKey("kgewgwey");
        templateModule.setModuleContent("contgewgwent");
        templateModule.setModuleCssContent("cgwegewss");
        templateModule.setName("modulgweweename3");
        templateModule.setTemplateVersionId(2);
        templateModule.setVersion("gewgewgwegw");
        templateModuleRepository.updateTemplateModule(templateModule);
        templateModuleRepository.deleteTemplateModuleById(templateModule.getId());
        templateModuleRepository.deleteTemplateModuleById(templateModule1.getId());
        Assert.assertEquals(0, templateModuleRepository.queryAllTemplateModules().size());
    }

}
