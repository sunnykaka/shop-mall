package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.TemplateStyle;
import com.kariqu.designcenter.repository.TemplateStyleRepository;
import junit.framework.Assert;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

/**
 * @author Asion
 * @since 2011-5-1 上午01:21:01
 * @version 1.0.0
 */
public class TemplateStyleRepositoryImplTest extends IbatisBaseSqlTest {
    
    @SpringBean("templateStyleRepository")
    private TemplateStyleRepository templateStyleRepository;
    
    @Test
    public void testTemplateStyleRepository() {
        TemplateStyle s1 = new TemplateStyle();
        s1.setDescription("desc");
        s1.setName("name");
        s1.setStyleResourceId(1);
        s1.setTemplateVersionId(1);
        TemplateStyle s2 = new TemplateStyle();
        s2.setDescription("desc");
        s2.setName("name2");
        s2.setStyleResourceId(1);
        s2.setTemplateVersionId(1);
        templateStyleRepository.createTemplateStyle(s1);
        templateStyleRepository.createTemplateStyle(s2);
        Assert.assertEquals(2, templateStyleRepository.queryAllTemplateStyles().size());
        Assert.assertEquals(2, templateStyleRepository.queryTemplateStylesByTemplateVersionId(1).size());
        
        Page<TemplateStyle> page = new Page<TemplateStyle>(1,2);
        Assert.assertEquals(2,templateStyleRepository.queryTemplateStylesByTemplateVersionIdAndPage(1, page).getTotalCount());
        Assert.assertEquals(2,templateStyleRepository.queryTemplateStylesByTemplateVersionIdAndPage(1, page).getResult().size());
        
        s2.setDescription("desgewgc");
        s2.setName("naggwegwme");
        s2.setStyleResourceId(100);
        s2.setTemplateVersionId(100);
        templateStyleRepository.updateTemplateStyle(s2);
        
        templateStyleRepository.deleteTemplateStyleById(s1.getId());
        templateStyleRepository.deleteTemplateStyleById(s2.getId());
        Assert.assertEquals(0, templateStyleRepository.queryAllTemplateStyles().size());
        
    }

}
