package com.kariqu.designcenter.domain.model;

import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Tiger
 * @version 1.0
 * @since 12-12-26 下午3:22
 */
public class ModuleInstanceIdFactoryTest {

    @Test
    public void testAnalyzeModuleInstanceParam() throws Exception {
        ModuleInstanceParamDetail detail = ModuleInstanceIdFactory.analyzeModuleInstanceParam("100_1_10");
        assertTrue(detail.isHead());
        assertFalse(detail.isFoot());
        assertFalse(detail.isBody());
        assertFalse(detail.isGlobal());
        assertEquals(1, detail.getShopId().intValue());

        detail = ModuleInstanceIdFactory.analyzeModuleInstanceParam("200_1_10");
        assertTrue(detail.isFoot());
        assertFalse(detail.isHead());
        assertFalse(detail.isBody());
        assertFalse(detail.isGlobal());
        assertEquals(1, detail.getShopId().intValue());

        detail = ModuleInstanceIdFactory.analyzeModuleInstanceParam("300_1_10_20");
        assertTrue(detail.isBody());
        assertFalse(detail.isHead());
        assertFalse(detail.isFoot());
        assertFalse(detail.isGlobal());
        assertEquals(1, detail.getShopId().intValue());
        assertEquals(10, detail.getPageId().intValue());

        detail = ModuleInstanceIdFactory.analyzeModuleInstanceParam("400_1_10");
        assertTrue(detail.isGlobal());
        assertFalse(detail.isHead());
        assertFalse(detail.isBody());
        assertFalse(detail.isFoot());
        assertEquals(1, detail.getShopId().intValue());
        assertEquals(10, detail.getPrototypeId().intValue());
    }


}
