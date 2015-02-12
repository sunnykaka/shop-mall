package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.productcenter.domain.Html;
import com.kariqu.productcenter.repository.HtmlRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-9-8
 * Time: 上午10:42
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class HtmlRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("htmlRepository")
    private HtmlRepository htmlRepository;

    @Test
    public void testHtmlRepository() {
        Html html = new Html();
        html.setProductId(3);
        html.setName("name");
        html.setContent("content");
        htmlRepository.createHtml(html);
        assertEquals(1, htmlRepository.queryAllHtml().size());
        assertEquals(1, htmlRepository.queryByProductId(3).size());
        html.setContent("xxxxxx");
        htmlRepository.updateHtml(html);
        htmlRepository.deleteHtmlByProductId(3);
        assertEquals(0, htmlRepository.queryAllHtml().size());

        htmlRepository.createHtml(html);
        htmlRepository.deleteHtmlByProductIdAndName(3, "name");
        assertEquals(0, htmlRepository.queryAllHtml().size());
    }


}
