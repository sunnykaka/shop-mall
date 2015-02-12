package com.kariqu.common.http;

import org.junit.Test;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-25
 *        Time: 下午2:01
 */
public class HttpURLUtilsTest {

    @Test
    public void testKariqu() {
        String result = HttpURLUtils.doGet("http://127.0.0.1:8085");
        System.out.println(result);
    }
}
