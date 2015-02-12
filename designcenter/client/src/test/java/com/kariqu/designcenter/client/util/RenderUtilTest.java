package com.kariqu.designcenter.client.util;

import com.kariqu.designcenter.domain.util.RenderUtil;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Asion
 * @since 2011-5-6 下午09:02:32
 * @version 1.0.0
 */
public class RenderUtilTest {
    
    @Test
    public void testAnalyzeInTimeModuleIds() {
        String content = "wgewhwehwhwhwhw<inTimeRender moduleId=\"-44\"/>gewgwgwegwe<inTimeRender moduleId=\"4\"/>gegwwgg<inTimeRender moduleId=\"5\"/>";
        List<String> strings = RenderUtil.analyzeInTimeModuleIds(content);
        Assert.assertEquals(3,strings.size());
        Assert.assertEquals("-44", strings.get(0));
    }

}
