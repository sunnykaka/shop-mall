package com.kariqu.designcenter.domain.model.prototype;

import com.kariqu.designcenter.domain.model.Module;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: Asion
 * Date: 12-7-13
 * Time: 下午2:32
 */
public class ModuleTest {

    @Test
    public void testModuleTypeCheck() {
        Module module = new Module("1", new TemplateModule());
        assertTrue(module.isTemplateModule());
        module = new Module("1", new CommonModule());
        assertFalse(module.isTemplateModule());
    }
}
