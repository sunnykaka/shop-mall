package com.kariqu.common.json;

import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;

/**
 * User: Asion
 * Date: 11-6-27
 * Time: 上午11:13
 */
public class JsonUtilTest {
    @Test
    public void testJsonUtil() throws Exception {
        int[] array = new int[1];
        array[0] = 1;
        System.out.println(JsonUtil.objectToJson(array));
    }

}
