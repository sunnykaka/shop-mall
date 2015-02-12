package com.kariqu.om;

import com.kariqu.om.domain.Seo;
import com.kariqu.om.domain.SeoType;
import com.kariqu.om.repository.SeoRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.assertEquals;

/**
 * User: Alec
 * Date: 13-10-9
 * Time: 下午3:06
 */
@ContextConfiguration(locations = {"/omCenter.xml"})
public class SeoRepositoryImplTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private SeoRepository seoRepository;

    @Test
    @Rollback(false)
    public void testSeo() {
        Seo seo = new Seo();
        seo.setSeoObjectId("1");
        seo.setSeoType(SeoType.CATEGORY);
        seo.setTitle("刀具-频道");
        seo.setDescription("yijushang刀具");
        seo.setKeywords("刀，刀，刀");

        seoRepository.insertSeo(seo);

        assertEquals(1,seo.getId());

        seo.setTitle("title");
        seo.setKeywords("keywords");

        seoRepository.updateSeo(seo);

        assertEquals("title",seoRepository.querySeoById(seo.getId()).getTitle());

        assertEquals("keywords",seoRepository.querySeoByObjIdAndType("1",SeoType.CATEGORY).getKeywords());

    }

}
