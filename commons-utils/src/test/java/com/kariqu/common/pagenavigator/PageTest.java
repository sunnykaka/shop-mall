package com.kariqu.common.pagenavigator;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


/**
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-29 上午01:19:27
 */
public class PageTest {

    @Test
    public void testPage() {
        Page<Object> page = new Page<Object>();
        Assert.assertEquals(1, page.getPageNo());
        Assert.assertEquals(15, page.getPageSize());
        Assert.assertEquals(0, page.getPageFirst());

        Page<Object> page1 = new Page<Object>(2, 20);
        page1.setTotalCount(50);
        Assert.assertEquals(2, page1.getPageNo());
        Assert.assertEquals(20, page1.getPageSize());
        Assert.assertEquals(20, page1.getPageFirst());
        PageBar process = PageProcessor.process(page1);
        assertEquals(3, process.getLinkNums().length);
        page1.setTotalCount(0);
        process = PageProcessor.process(page1);
        assertEquals(0, process.getLinkNums().length);
    }

}
