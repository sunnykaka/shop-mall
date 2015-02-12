package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.prototype.TemplateVersion;
import com.kariqu.designcenter.domain.model.prototype.VersionState;
import com.kariqu.designcenter.repository.TemplateVersionRepository;
import junit.framework.Assert;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

public class TemplateVersionRepositoryTest extends IbatisBaseSqlTest {
    
    @SpringBean("templateVersionRepository")
    private TemplateVersionRepository templateVersionRepository;
    
    @Test
    public void testTemplateVersionRepository() {
        TemplateVersion version = new TemplateVersion();
        version.setFootConfigContent("foot config");
        version.setFootContent("foot content");
        version.setGlobalCssId(1);
        version.setGlobalJsId(3);
        version.setDefaultStyleId(2);
        version.setHeadConfigContent("head config");
        version.setHeadContent("heand conent");
        version.setState(VersionState.debug);
        version.setTemplateId(1);
        version.setVersion("1.0");
        TemplateVersion version1 = new TemplateVersion();
        version1.setFootConfigContent("foot config");
        version1.setFootContent("foot content");
        version1.setGlobalCssId(2);
        version1.setGlobalJsId(4);
        version1.setDefaultStyleId(3);
        version1.setHeadConfigContent("head config");
        version1.setHeadContent("heand conent");
        version1.setState(VersionState.debug);
        version1.setTemplateId(1);
        version1.setVersion("1.0");
        templateVersionRepository.createTemplateVersion(version);
        templateVersionRepository.createTemplateVersion(version1);
        Assert.assertEquals("1.0",templateVersionRepository.getTemplateVersionById(version.getId()).getVersion());
        Assert.assertEquals(2, templateVersionRepository.queryAllTemplateVersions().size());
        Assert.assertEquals(2, templateVersionRepository.queryTemplateVersionsByTemplateId(1).size());
        version1.setFootConfigContent("foot gewgconfig");
        version1.setFootContent("foot wegwgwgwcontent");
        version1.setGlobalCssId(2);
        version1.setHeadConfigContent("heagegwd config");
        version1.setHeadContent("heand cgewgwgwonent");
        version1.setState(VersionState.released);
        version1.setTemplateId(3);
        version1.setVersion("1gewgwgwe.0");
        version1.setGlobalJsId(100);
        version1.setDefaultStyleId(4);
        templateVersionRepository.updateTemplateVersion(version1);
        templateVersionRepository.deleteTemplateVersionById(version.getId());
        templateVersionRepository.deleteTemplateVersionById(version1.getId());
        Assert.assertEquals(0, templateVersionRepository.queryAllTemplateVersions().size());
    }

}
