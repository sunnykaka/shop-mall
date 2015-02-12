package com.kariqu.tradecenter.helper;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @author Athens(刘杰)
 */
public class Utils {
    
    /** 检测 email 的正则 */
    private static final String EMAIL_REGEX = "^\\w\\S*@([a-zA-Z0-9\\-]+\\.)+[a-zA-Z0-9]{2,4}$";
    
    /** 检测 sql 注入的正则 */
    private static final String SQL_REGEX = "(?:')|(?:--)|;|(/\\*(?:.|[\\n\\r])*?\\*/)|((?i)\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

    /** 验证中文的 正则 */
    private static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";

    private static final String FORMAT_MONTH = "yyyy-MM-dd HH:mm";
    private static final String FORMAT_DAY = "yyyy-MM-dd HH:mm:ss";

    /**
     * 空字符串判断, <span style="color:red;">去左右空格后</span>, 如果为空返回 true<br/>
     *
     * @param param
     * @return boolean
     */
    public static boolean isNull(String param) {
        return (param == null || "".equals(param.toString().trim()));
    }

    /**
     * 空字符串判断, <span style="color:red;">去左右空格后</span>, 如果不为空返回 true<br/>
     *
     * @param param
     * @return boolean
     */
    public static boolean isNotNull(String param) {
        return !isNull(param);
    }

    /**
     * 集合若为空 或 长度为0, 返回 true
     *
     * @param list : 集合
     * @return boolean
     */
    public static boolean isNull(Collection<? extends Object> list) {
        return (list == null || list.size() == 0);
    }

    /**
     * 集合若不为空 且 长度不为 0, 返回 true
     *
     * @param list : 集合
     * @return boolean
     */
    public static boolean isNotNull(Collection<? extends Object> list) {
        return !isNull(list);
    }

    /**
     * 检测 email 格式, 匹配则返回 true<br/>
     * 格式: 下划线 数字或字母 @ 字母 数字或中横线 . 2 到 4 个字母或数字<br/>
     *
     * @param email
     * @return boolean
     */
    public static boolean checkEmail(String email) {
        return checkRegexWithStrict(email, EMAIL_REGEX);
    }

    /**
     * 验证 指定正则 是否 <span style="color:red;">全字匹配</span> 指定字符串, 匹配则返回 true <br/>
     * <br/>
     * 左右空白符 : (?m)(^\s*|\s*$)
     * 帐号输入(字母或数字开头, 长度 5-16, 可以有下划线) : ^[a-zA-Z0-9]\\w{4,15}$<br/>
     * 手机 : ^1(3[0-9]|5[0|3|5|6|7|8|9]|8[6|8|9])[0-9]{8}$<br/>
     * 空白符 : (^\\s*)|(\\s*$)<br/>
     * IP : ([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])(\\.([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])){3}<br/>
     * 日期 : (([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)
     * 匹配多行注释 : /\*\*(\s|.)*?\* /<br/>
     *
     * @param param
     * @param regex
     * @return boolean
     */
    public static boolean checkRegexWithStrict(String param, String regex) {
        return isNull(param) ? false : Pattern.compile(regex).matcher(param).matches();
    }

    /**
     * 检测 中文, 包含中文则返回 true
     *
     * @param chinese
     * @return boolean
     */
    public static boolean checkChinese(String chinese) {
        return checkRegexWithRelax(chinese, CHINESE_REGEX);
    }

    /**
     * 检测 SQL, 若有会导致注入的值则返回 true.
     * 
     * @param sql
     * @return
     */
    public static boolean checkSqlValue(String sql) {
        return checkRegexWithRelax(sql, SQL_REGEX);
    }

    /**
     * 此模式为非严格型匹配, <span style="color:red;">只要找到匹配即返回 true</span><br/>
     * <br/>
     * 中文: [\\u4e00-\\u9fa5]<br/>
     *
     * @param param
     * @param regex
     * @return boolean
     */
    public static boolean checkRegexWithRelax(String param, String regex) {
        return isNotNull(param) && Pattern.compile(regex).matcher(param).find();
    }

    /**
     * 日期类型以何种格式转换成字符串类型, 异常则返回 "exception"
     *
     * @param date java.sql.Date 和 java.sql.Timestamp 均可
     * @param param : 默认格式 "yyyy-MM-dd HH:mm"
     * @return String
     */
    public static String getStringFromDate(Date date, String param) {
        try {
            param = isNull(param) ? FORMAT_MONTH : param;
            return new SimpleDateFormat(param).format(date);
        } catch (Exception e) {
            return "exception";
        }
    }

    /**
     * 日期类型转换成字符串类型(年-月-日 时:分:秒), 异常则返回 "exception"
     *
     * @param date java.sql.Date 和 java.sql.Timestamp 均可
     * @return String
     */
    public static String formatDate(Date date) {
        return getStringFromDate(date, FORMAT_DAY);
    }

    /**
     * 字符串(年-月-日 时:分:秒)转换为日期, 异常则返回 null
     *
     * @param date 类型(年-月-日 时:分:秒)
     * @return
     */
    public static Date formatString(String date) {
        return getDateFromString(date, FORMAT_DAY);
    }

    /**
     * 字符串类型以何种格式转换成日期类型, 异常则返回 null
     *
     * @param date
     * @param param : 若为空则默认格式为 "yyyy-MM-dd HH:mm"
     * @return Date
     */
    public static Date getDateFromString(String date, String param) {
        try {
            param = isNull(param) ? FORMAT_MONTH : param;
            return new SimpleDateFormat(param).parse(date);
        } catch (Exception e) {
            return null;
        }
    }

}
