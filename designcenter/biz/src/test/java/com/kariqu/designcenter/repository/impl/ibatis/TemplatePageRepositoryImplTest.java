package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.PageType;
import com.kariqu.designcenter.domain.model.prototype.TemplatePage;
import com.kariqu.designcenter.repository.TemplatePageRepository;
import org.junit.Assert;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

/**
 * @author Asion
 * @since 2011-4-30 下午08:32:34
 * @version 1.0.0
 */
public class TemplatePageRepositoryImplTest extends IbatisBaseSqlTest {
    
    @SpringBean("templatePageRepository")
    private TemplatePageRepository templatePageRepository;
    
    @Test
    public void testTemplatePageRepository() {
        TemplatePage tp = new TemplatePage();
        tp.setConfigContent("config conteng");
        tp.setConfigKey("key");
        tp.setPageContent("page content");
        tp.setPageName("pageName1");
        tp.setPageType(PageType.detail);
        tp.setTemplateVersionId(1);
        TemplatePage tp1 = new TemplatePage();
        tp1.setConfigContent("config conteng");
        tp1.setConfigKey("key");
        tp1.setPageContent("page content");
        tp1.setPageName("indexpageName");
        tp1.setPageType(PageType.index);
        tp1.setTemplateVersionId(1);
        templatePageRepository.createTemplatePage(tp);
        templatePageRepository.createTemplatePage(tp1);
        Assert.assertEquals("pageName1", templatePageRepository.queryTemplatePageByPageNameAndTemplateVersionId("pageName1", 1).getPageName());
        Assert.assertEquals("pageName1", templatePageRepository.getTemplatePageById(tp.getId()).getPageName());
        Assert.assertEquals(2, templatePageRepository.queryAllTemplatePages().size());
        Assert.assertEquals(2,templatePageRepository.queryTemplatePagesByTemplateVersionId(1).size());
        Assert.assertEquals("indexpageName",templatePageRepository.queryIndexTemplatePage(1).getPageName());
        
        Page<TemplatePage> page = new Page<TemplatePage>(1,2);
        Assert.assertEquals(2,templatePageRepository.queryTemplatePagesByTemplateVersionIdAndPage(1, page).getTotalCount());
        Assert.assertEquals(2,templatePageRepository.queryTemplatePagesByTemplateVersionIdAndPage(1, page).getResult().size());
        
        tp1.setConfigContent("config cwegwonteng");
        tp1.setConfigKey("keygewg");
        tp1.setPageContent("page congwegwtent");
        tp1.setPageName("indexpagegwegwegName");
        tp1.setPageType(PageType.other);
        tp1.setTemplateVersionId(2);
        templatePageRepository.updateTemplatePage(tp1);

        templatePageRepository.deleteTemplatePageById(tp.getId());
        templatePageRepository.deleteTemplatePageById(tp1.getId());
        Assert.assertEquals(0, templatePageRepository.queryAllTemplatePages().size());
        
    }

}
