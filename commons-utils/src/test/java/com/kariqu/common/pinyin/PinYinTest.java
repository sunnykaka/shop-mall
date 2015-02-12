package com.kariqu.common.pinyin;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: Asion
 * Date: 11-12-14
 * Time: 下午1:27
 */
public class PinYinTest {

    @Test
    public void test() {
        String s = PinYinUtil.getPinYin("谢中生");
        assertEquals("xiezhongsheng", s);
        System.out.println(PinYinUtil.getComposePinYin("炒锅"));
    }
}
