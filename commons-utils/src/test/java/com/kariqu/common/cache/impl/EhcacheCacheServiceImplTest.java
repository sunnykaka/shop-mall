package com.kariqu.common.cache.impl;

import com.kariqu.common.cache.CacheService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * User: Asion
 * Date: 11-5-14
 * Time: 上午1:47
 */


@ContextConfiguration(locations = {"classpath:ehcache-context.xml"})
public class EhcacheCacheServiceImplTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private CacheService cacheService;

    @Before
    public void setUp() {
        cacheService.put("testkey", "value");
    }


    @Test
    public void testGet() throws Exception {
        Assert.assertEquals("value", cacheService.get("testkey"));
    }

    @Test
    public void testPut() throws Exception {
        cacheService.put("test", "myValue");
        Assert.assertEquals("myValue", cacheService.get("test"));
    }

    @Test
    public void testRemove() throws Exception {
        cacheService.remove("testkey");
        Assert.assertEquals(null, cacheService.get("testkey"));
    }
}
