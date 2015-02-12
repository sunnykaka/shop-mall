package com.kariqu.categorycenter.domain.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-6-28
 * Time: 上午12:36
 */
public class PropertyValueUtilTest {
    @Test
    public void test() throws Exception {
        long l = PropertyValueUtil.mergePidVidToLong(1, 10656);
        System.out.println(l);
        PropertyValueUtil.PV pv = PropertyValueUtil.parseLongToPidVid(l);
        assertEquals(1, pv.pid);
        assertEquals(10656, pv.vid);
        PropertyValueUtil.PV pv1 = PropertyValueUtil.parseLongToPidVid(4294977580l);
        System.out.println(pv1.pid);
        System.out.println(pv1.vid);
        System.out.println(4294977580l >> 32);
        System.out.println((int)4294977580l);
    }
}
