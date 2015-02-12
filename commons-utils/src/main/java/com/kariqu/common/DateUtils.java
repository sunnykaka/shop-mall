package com.kariqu.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间处理工具
 * User: Asion
 * Date: 12-3-15
 * Time: 下午2:36
 */
public class DateUtils {

    /**
     * 获取当前系统时间(原始格式)
     */
    public static Date getCurrentDate() {
        Date date = new Date(System.currentTimeMillis());
        return date;
    }

    /**
     * 获取当前时间日期的字符串
     */
    public static String getCurrentDateStr(DateFormatType dateFormatType) {
        Date date = getCurrentDate();
        return (String) doOperation(date, dateFormatType.getValue());
    }

    /**
     * 时间、日期格式化成字符串
     */
    public static String formatDate(Date date, DateFormatType dateFormatType) {
        if (null == date) {
            return "";
        }
        return (String) doOperation(date, dateFormatType.getValue());
    }

    /**
     * 从字符串解析成时间、日期
     */
    public static Date parseDate(String dateStr, DateFormatType dateFormatType) {
        return (Date) doOperation(dateStr, dateFormatType.getValue());
    }


    private static Object doOperation(Object object, String formatStr) {
        if (object == null || null == formatStr || "".equals(formatStr)) {
            throw new RuntimeException("参数不能为空");
        }
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        try {
            if (object instanceof Date)
                return format.format(object);
            else
                return format.parse(object.toString());
        } catch (Exception e) {
            throw new RuntimeException("格式不正确! 使用下面的格式: " + formatStr, e);
        }

    }

    public enum DateFormatType {

        /** yyyy-MM-dd HH:mm:ss */
        DATE_FORMAT_STR("yyyy-MM-dd HH:mm:ss"),

        /** yyyy-MM-dd HH:mm */
        DATE_MINUTE_FORMAT_STR("yyyy-MM-dd HH:mm"),

        /** MM/dd/yyyy HH:mm:ss */
        DATE_USA_STYLE("MM/dd/yyyy HH:mm:ss"),

        /** yyyy年MM月dd日 HH时mm分ss秒 */
        DATE_FORMAT_STR_CHINA("yyyy年MM月dd日 HH时mm分ss秒"),

        /** yyyy年MM月dd日 HH点 */
        DATE_FORMAT_STR_CHINA_HOUR("yyyy年MM月dd日 HH点"),

        /** yy年MM月dd日 HH点 */
        DATE_FORMAT_STR_CHINA_HOUR_YY("yy年MM月dd日 HH点"),

        /** yyyyMMddHHmmss */
        SIMPLE_DATE_TIME_FORMAT_STR("yyyyMMddHHmmss"),

        /** yyyy-MM-dd */
        SIMPLE_DATE_FORMAT_STR("yyyy-MM-dd"),

        /** yyyyMMdd */
        SIMPLE_DATE_FORMAT("yyyyMMdd"),

        /** yy年MM月dd日 */
        SIMPLE_DATE_FORMAT_STR_YY("yy年MM月dd日"),

        /** yyyy年MM月dd日 */
        SIMPLE_DATE_FORMAT_STR_DAY("yyyy年MM月dd日"),

        /** MM月dd日 */
        SIMPLE_DATE_FORMAT_STR_MD("MM月dd日"),

        /** yyyy/MM/dd */
        SIMPLE_DATE_FORMAT_VIRGULE_STR("yyyy/MM/dd"),

        /** HH:mm:ss MM-dd */
        MONTH_DAY_HOUR_MINUTE_SECOND("HH:mm:ss MM-dd"),

        /** HH:mm:ss */
        HOUR_MINUTE_SECOND("HH:mm:ss"),

        /** HH:mm */
        HOUR_MINUTE("HH:mm");

        private final String value;

        DateFormatType(String formatStr) {
            this.value = formatStr;
        }

        public String getValue() {
            return value;
        }
    }
}


