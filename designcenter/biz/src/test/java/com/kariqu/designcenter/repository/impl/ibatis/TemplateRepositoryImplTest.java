package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.prototype.Template;
import com.kariqu.designcenter.domain.model.prototype.TemplateType;
import com.kariqu.designcenter.repository.TemplateRepository;
import junit.framework.Assert;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

/**
 * @author Asion
 * @since 2011-4-30 下午04:18:25
 * @version 1.0.0
 */

public class TemplateRepositoryImplTest extends IbatisBaseSqlTest {
    
    @SpringBean("templateRepository")
    private TemplateRepository templateRepository;
    
    @Test
    public void testTemplateInsert() {
        Template t = new Template();
        t.setDescription("disc");
        t.setName("测试模板");
        t.setTemplateType(TemplateType.VM);
        templateRepository.createTemplate(t);
        Assert.assertNotSame(0, t.getId());
        Assert.assertEquals("disc", templateRepository.getTemplateById(t.getId()).getDescription());
        List<Template> ts = templateRepository.queryAllTemplates();
        Assert.assertEquals(1, ts.size());
        Template td = ts.get(0);
        td.setDescription("gegege");
        td.setName("kkkkkkkkkkk");
        td.setTemplateType(TemplateType.PHP);
        templateRepository.updateTemplate(td);
        templateRepository.deleteTemplateById(td.getId());
        Assert.assertEquals(0, templateRepository.queryAllTemplates().size());
    }

}
