package com.kariqu.common;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Random;

/**
 * User: Asion
 * Date: 13-5-18
 * Time: 下午1:39
 */
public class LanguageTest {


    @Test
    public void testCharRandom() {
        String base = "abcdefghijklmnopqrstuvwxy";
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println(base.charAt(random.nextInt(base.length())));
        }
    }

    @Test
    public void testHtmlImgReplace() {
        String html = "<img src=\"http://img04.yijushang.com/A7DBC43EBA344E5A9D456F3AE16511EF.jpg\" alt=\"\" /><img src=\"http://img04.yijushang.com/6E8B9FE0964B45A09A48D441C0D4A8BD.jpg\" alt=\"\" />";
        html = html.replaceAll("<img.*?src=\"(.*?)(?:\") alt=\"\" />", "<img alt=\"\" class=\"lazy\" data-origina=\"$1\"/>");
        System.out.println(html);

        System.out.println(String.format("%s ddd %s", "dd-", "-gg"));
        System.out.println(String.format("%b ddd %b", "dd-", "-gg"));
        System.out.println(String.format("%h ddd %h", "3", "11"));
    }

    @Test
    public void testSplit() {
        String address = "广东,中山,";
        String[] split = address.split(",");
        System.out.println(split.length);
    }

    @Test
    public void testBigDecimal() {
        BigDecimal bigDecimal1 = new BigDecimal("1.6346666666666666666666666");
        BigDecimal bigDecimal2 = new BigDecimal("4.6346666666666666666666666");
        System.out.println(bigDecimal1.add(bigDecimal2));

        System.out.println(BigDecimal.valueOf(10l, Currency.getInstance("CNY").getDefaultFractionDigits()));
    }

    @Test
    public void testUrl() throws IOException {
        URL url = new URL("http://www.baidu.com");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        int code = urlConnection.getResponseCode();
        System.out.println(code);
    }

    @Test
    public void codePoint() {
        String str = "中国";
        System.out.println(str.length());
        System.out.println(str.codePointCount(0, str.length()));
        System.out.println(str.charAt(0));
        System.out.println("!".codePointAt(0));
        System.out.println(str.codePointAt(0));
    }

    @Test
    public void testBoolean() {
        boolean test = false;
        if (test = true) {
            System.out.println("true");
        }
    }


    @Test
    public void testArrayList() {
        List list = new ArrayList(3);
        System.out.println(list.size());
        list.add(1);
        list.add(2);
        System.out.println(list.toString());
        long[] array = new long[]{1, 23};
        System.out.println(array.toString());
    }

    @Test
    public void testNumberic() {
        System.out.println(1/2);
        System.out.println(-1/2);
        System.out.println(1/2.0);
        System.out.println(1.0/2.0);
        System.out.println(-1/2.0);
        System.out.println(1/-2.0);
        System.out.println(1e2);
        System.out.println(1/0.0);
        System.out.println(11e100);
        System.out.println(1.22222222222222222222222222222);
        System.out.println(0.2);
        System.out.println((byte)Integer.valueOf("10000000000",2).intValue());
        System.out.println(0.1d + 0.1d);
        System.out.println(1/3.0);
        System.out.println(0.01+0.09);
    }


}
