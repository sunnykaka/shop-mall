package com.kariqu.designcenter.domain.util;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author: Tiger
 * @Since: 11-6-11 下午1:37
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public class VersionUtilTest extends TestCase {

    public void testUpgradeVersion() throws Exception {
        assertEquals("1.0.1",VersionUtil.upgradeVersion("1.0.0"));
        assertEquals("1.0.0",VersionUtil.upgradeVersion("9.9"));
    }

    public void testDegradeVersion() throws Exception {
        assertEquals("1.0.0",VersionUtil.degradeVersion("1.0.1"));
        assertEquals("9.9",VersionUtil.degradeVersion("1.0.0"));
    }
}
