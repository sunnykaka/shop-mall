package com.kariqu.common.lib;

import java.util.Date;

/**
 * This is a helper class for converting distance bewteen two dates to a human format, like:
 * "two days", "three months", etc.
 * <p/>
 * It was inspired by a Rails helper method:
 * <a href="http://api.rubyonrails.org/classes/ActionView/Helpers/DateHelper.html#M001006">distance_of_time_in_words</a>
 * <p/>
 * Created by Canal.wen on 2014/6/27 14:26.
 */
public class HumanDate {
    // private constructor because this is a utility class
    private HumanDate() {
    }


    /**
     * Generates a human representation of distance in time between two time stamps. This could take a form: "less than
     * a minute", or "about a year".
     *
     * @param fromTime start timestamp. This is a representation of time in milliseconds from January 1 1970.
     * @param toTime   end timestamp. This is a representation of time in milliseconds from January 1 1970.
     * @return human representation if distance in time between <code>fromTime</code> and <code>toTime</code>.
     */
    public static String toHumanFormat(long fromTime, long toTime) {
        if (!(toTime >= fromTime)) {
            throw new IllegalArgumentException("toTime must be >= fromTime");
        }

        long distanceInSeconds = Math.round((toTime - fromTime) / 1000);
        long distanceInMinutes = Math.round(distanceInSeconds / 60);

        if (distanceInMinutes == 0) {
            return "刚刚" /*"less than a minute"*/;
        }
        if (distanceInMinutes == 1) {
            return "1分钟前" /*"a minute"*/;
        }
        if (inRange(2, 44, distanceInMinutes)) {
            return distanceInMinutes + " 分钟" /*" minutes"*/;
        }
        if (inRange(45, 89, distanceInMinutes)) {
            return "大约1小时" /*"about 1 hour"*/;
        }
        if (inRange(90, 1439, distanceInMinutes)) {
            return "大约 "/*"about "*/ + Math.round(distanceInMinutes / 60f) + " 小时" /*" hours"*/;
        }
        if (inRange(1440, 2879, distanceInMinutes)) {
            return "1天" /*"1 day"*/;
        }
        if (inRange(2880, 43199, distanceInMinutes)) {
            return Math.round(distanceInMinutes / 1440f) + "天" /*" days"*/;
        }
        if (inRange(43200, 86399, distanceInMinutes)) {
            return "大约1个月" /*"about 1 month"*/;
        }
        if (inRange(86400, 525599, distanceInMinutes)) {
            return Math.round(distanceInMinutes / 43200f) + " 月" /*" months"*/;
        }
        if (inRange(525600, 1051199, distanceInMinutes)) {
            return "大约1年" /*"about 1 year"*/;
        }

        return "about " + Math.round(distanceInMinutes / 525600f) + " 年" /*" years"*/;
    }

    /**
     * This is a convenience method in addition to {@link #toHumanFormat(long, long)}, except the second parameter is
     * always now.
     *
     * @param fromTime start date. This is a representation of time in milliseconds from January 1 1970.
     * @return human imprecise representation of time difference between the <code>fromTime</code> and now.
     */
    public static String toHumanFormat(long fromTime) {
        return toHumanFormat(fromTime, new Date().getTime());
    }


    /**
     * Returns <code>true</code> if the <code>val</code> is between <code>min</code> and <code>max</code>,
     * inclusively. Otherwise returns <code>false</code>. This is implemented because Java does not have native
     * support for ranges.
     *
     * @param min minimum range boundary
     * @param max maximum range boundary
     * @param val value in question.
     * @return <code>true</code> if the <code>val</code> is between <code>min</code> and <code>max</code>,
     * inclusively. Otherwise returns <code>false</code>.
     */
    private static boolean inRange(long min, long max, long val) {
        return val >= min && val <= max;
    }

}
