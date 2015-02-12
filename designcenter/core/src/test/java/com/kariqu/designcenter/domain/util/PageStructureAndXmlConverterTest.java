package com.kariqu.designcenter.domain.util;

import com.kariqu.designcenter.domain.model.PageStructure;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Asion
 * @since 2011-4-10 下午08:56:25
 * @version 1.0.0
 */
public class PageStructureAndXmlConverterTest {
    
    @Test
    public void testConvertXmlToPageStructure() throws Exception {
        PageStructure ps = PageStructureAndXmlConverter.convertXmlToPageStructure(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("PageStructureDemo.xml")));
        assertEquals(6,ps.getAllModules().size());
        assertEquals(2,ps.getAllHeadModules().size());
        assertEquals(2,ps.getAllFootModules().size());
        assertEquals(2,ps.getAllFootModules().size());
        assertEquals("1000",ps.getModuleById("1000").getModuleInstanceId());
        assertEquals(1,ps.getModulesFromRegionOfBody("body_region").size());
    }
    
    @Test
    public void testConvertPageStructureToXml() throws Exception {
        PageStructure ps = PageStructureAndXmlConverter.convertXmlToPageStructure(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("PageStructureDemo.xml")));
        System.out.println(PageStructureAndXmlConverter.convertPageStructureToXml(ps));
    }

}
