package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.prototype.AreaType;
import com.kariqu.designcenter.domain.model.prototype.PagePrototype;
import com.kariqu.designcenter.repository.PagePrototypeRepository;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 12-4-20
 * Time: 上午10:56
 */
public class PagePrototypeRepositoryImplTest extends IbatisBaseSqlTest {

    @SpringBean("pagePrototypeRepository")
    private PagePrototypeRepository pagePrototypeRepository;


    @Test
    public void testPagePrototypeRepository() {
        PagePrototype pagePrototype = new PagePrototype();
        pagePrototype.setName("test");
        pagePrototype.setDescription("desc");
        pagePrototype.setPageCode("page code");
        pagePrototype.setAreaType(AreaType.BODY);
        pagePrototype.setConfigContent("config");
        pagePrototypeRepository.createPagePrototype(pagePrototype);
        pagePrototype.setName("xxname");
        pagePrototype.setDescription("xxdesc");
        pagePrototype.setAreaType(AreaType.FOOT);
        pagePrototype.setPageCode("xxpagecode");
        pagePrototype.setConfigContent("xxconfig");
        pagePrototypeRepository.updatePagePrototype(pagePrototype);
        assertEquals(1, pagePrototypeRepository.queryAllPagePrototype().size());
        pagePrototypeRepository.deletePagePrototypeById(pagePrototype.getId());
        assertEquals(0, pagePrototypeRepository.queryAllPagePrototype().size());
    }

}
