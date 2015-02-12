package com.kariqu.common;

import org.junit.Test;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;

/**
 * User: Asion
 * Date: 12-3-15
 * Time: 下午2:40
 */
public class DateUtilsTest {

    @Test
    public void test() {
        System.out.println(DateUtils.formatDate(null, DateUtils.DateFormatType.DATE_FORMAT_STR));
        System.out.println(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA));
        System.out.println(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR_CHINA_HOUR));
    }

    @Test
    public void testFormat() throws ParseException {
        String date = "2012-03-20T00:00:00".replace("T", " ");
        Date date1 = DateUtils.parseDate(date, DateUtils.DateFormatType.DATE_FORMAT_STR);
        System.out.println(date1);
    }

}
