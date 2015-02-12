package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.designcenter.domain.model.prototype.ResourceType;
import com.kariqu.designcenter.domain.model.prototype.TemplateResource;
import com.kariqu.designcenter.repository.TemplateResourceRepository;
import junit.framework.Assert;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

/**
 * @author Asion
 * @since 2011-5-1 上午01:07:59
 * @version 1.0.0
 */
public class TemplateResourceRepositoryTest extends IbatisBaseSqlTest {
    
    @SpringBean("templateResourceRepository")
    TemplateResourceRepository templateResourceRepository;
    
    @Test
    public void testTemplateResourceRepository() {
        TemplateResource r1 = new TemplateResource();
        r1.setContent("content");
        r1.setDescription("desc");
        r1.setName("name");
        r1.setResourceKey("resourceKey");
        r1.setResourceType(ResourceType.css);
        r1.setTemplateVersionId(1);
        TemplateResource r2 = new TemplateResource();
        r2.setContent("content");
        r2.setDescription("desc");
        r2.setName("name2");
        r2.setResourceKey("resourceKey");
        r2.setResourceType(ResourceType.css);
        r2.setTemplateVersionId(1);
        templateResourceRepository.createTemplateResource(r1);
        templateResourceRepository.createTemplateResource(r2);
        Assert.assertEquals(2, templateResourceRepository.queryAllTemplateResources().size());
        Assert.assertEquals(2, templateResourceRepository.queryTemplateResourcesByTemplateVersionId(1).size());
        Assert.assertEquals(2, templateResourceRepository.queryTemplateResourcesByResourceType(1,ResourceType.css).size());
        
        Page<TemplateResource> page = new Page<TemplateResource>(1,2);
        Assert.assertEquals(2,templateResourceRepository.queryTemplateResourcesByTemplateVersionIdAndPage(1, page).getTotalCount());
        Assert.assertEquals(2,templateResourceRepository.queryTemplateResourcesByTemplateVersionIdAndPage(1, page).getResult().size());
        
        r2.setContent("contgewgent");
        r2.setDescription("dgewgwesc");
        r2.setName("namegewgwe");
        r2.setResourceKey("resourcegewgwegewKey");
        r2.setResourceType(ResourceType.js);
        r2.setTemplateVersionId(100);
        templateResourceRepository.updateTemplateResource(r2);

        templateResourceRepository.deleteTemplateResourceById(r1.getId());
        templateResourceRepository.deleteTemplateResourceById(r2.getId());
        Assert.assertEquals(0, templateResourceRepository.queryAllTemplateResources().size());
    }

}
