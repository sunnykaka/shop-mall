package com.kariqu.designcenter.domain.util;

import com.kariqu.designcenter.domain.exception.ModuleConfigException;
import com.kariqu.designcenter.domain.model.prototype.DataType;
import com.kariqu.designcenter.domain.model.prototype.FormType;
import com.kariqu.designcenter.domain.model.prototype.Parameter;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Asion
 * @since 2011-4-14 上午10:48:56
 * @version 1.0.0
 */
public class ModuleParamAndXmlConverterTest {

    @Test(expected = ModuleConfigException.class)
    public void testErrorFile() throws IOException, ModuleConfigException {
        ModuleParamAndXmlConverter.convertXmlToParameters(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("error/module.xml")));
    }
    
    @Test
    public void testConvertXmlToParameters() throws Exception {
        List<Parameter> params = ModuleParamAndXmlConverter.convertXmlToParameters(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("module.xml"),"UTF-8"));
        assertEquals(8,params.size());
        Parameter parameter = params.get(0);
        assertEquals("p1",parameter.getName());
        assertEquals("Home",parameter.getValue());
        assertEquals("导航1",parameter.getLabel());
        assertEquals(DataType.String,parameter.getDataType());
        assertEquals(FormType.text,parameter.getFormType());
        assertEquals("",parameter.getDescription());

        Parameter parameter2 = params.get(7);
        assertEquals(4,parameter2.getOptions().size());
        assertEquals("saab",parameter2.getValue());
    }
    
    @Test
    public void testConfig() throws ModuleConfigException, IOException {
        assertEquals("isCacheable=true\nisEdit=true\nisDelete=true\n", ModuleParamAndXmlConverter.readModuleConfig(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("module.xml"),"UTF-8")));
    }

    @Test
    public void testCDATA() throws IOException, ModuleConfigException {
        List<Parameter> params = ModuleParamAndXmlConverter.convertXmlToParameters(IOUtils.toString(getClass().getClassLoader().getResourceAsStream("CdataConfig.xml"),"UTF-8"));
        System.out.println(params);

    }

}
