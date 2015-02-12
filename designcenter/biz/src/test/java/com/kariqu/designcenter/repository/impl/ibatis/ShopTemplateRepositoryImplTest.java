package com.kariqu.designcenter.repository.impl.ibatis;

import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.repository.ShopTemplateRepository;
import junit.framework.Assert;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

public class ShopTemplateRepositoryImplTest extends IbatisBaseSqlTest {
    
    @SpringBean("shopTemplateRepository")
    private ShopTemplateRepository shopTemplateRepository;
    
    @Test
    public void testShopTemplateRepository() {
        ShopTemplate st = new ShopTemplate();
        st.setEditFootConfigContent("edit foot config");
        st.setEditFootContent("edit foot content");
        st.setEditHeadConfigContent("edit head config");
        st.setEditHeadContent("edit head content");
        st.setProdFootConfigContent("prod foot config");
        st.setProdFootContent("prod foot content");
        st.setProdHeadConfigContent("prod head config");
        st.setProdHeadContent("prod head content");
        st.setProdGlobalCss("global css");
        st.setEditStyle("style css");
        st.setEditGlobalCss("global css");
        st.setEditGlobalJs("edit global js");
        st.setProdGlobalJs("global js");
        st.setProdStyle("global style");
        st.setGlobalModuleInfoConfig("global config");
        st.setShopId(1);
        st.setTemplateVersionId(2);
        
        ShopTemplate st1 = new ShopTemplate();
        st1.setEditFootConfigContent("edit foot config");
        st1.setEditFootContent("edit foot content");
        st1.setEditHeadConfigContent("edit head config");
        st1.setEditHeadContent("edit head content");
        st1.setProdFootConfigContent("prod foot config");
        st1.setProdFootContent("prod foot content");
        st1.setProdHeadConfigContent("prod head config");
        st1.setProdHeadContent("prod head content");
        st1.setProdGlobalCss("global css");
        st1.setShopId(2);
        st1.setTemplateVersionId(3);
        
        
        shopTemplateRepository.createShopTemplate(st);
        shopTemplateRepository.createShopTemplate(st1);
        
        Assert.assertEquals(2, shopTemplateRepository.queryAllShopTemplates().size());
        Assert.assertEquals(3, shopTemplateRepository.getShopTemplateByShopId(2).getTemplateVersionId());
        
        st1.setEditFootConfigContent("editagew foot config");
        st1.setEditFootContent("edit foot agagwcontent");
        st1.setEditHeadConfigContent("edit gewgwhead config");
        st1.setEditHeadContent("edit head cgawgwontent");
        st1.setProdFootConfigContent("prod gwgawgfoot config");
        st1.setProdFootContent("prod foot cewagwgwontent");
        st1.setProdHeadConfigContent("prod gewgaghead config");
        st1.setProdHeadContent("prod hgweawgwegwad content");
        st1.setProdGlobalCss("global wgwgwgwgwecss");
        st1.setShopId(20);
        st1.setTemplateVersionId(30);
        st1.setEditStyle("style css");
        st1.setEditGlobalCss("ggg css");
        st1.setEditGlobalJs("gggg js");
        st1.setProdGlobalJs("pro");
        st1.setProdStyle("pros");
        st1.setProdGlobalCss("dgegege");
        shopTemplateRepository.updateShopTemplate(st1);

        shopTemplateRepository.deleteShopTemplateById(st.getId());
        shopTemplateRepository.deleteShopTemplateById(st1.getId());
        Assert.assertEquals(0, shopTemplateRepository.queryAllShopTemplates().size());
        
    }

}
