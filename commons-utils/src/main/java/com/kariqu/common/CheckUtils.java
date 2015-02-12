package com.kariqu.common;

import java.util.regex.Pattern;

/**
 * @author Athens(刘杰)
 * @Time 13-4-28 下午1:38
 */
public class CheckUtils {

    /** 检测 email 的正则 */
    public static final String EMAIL_REGEX = "^\\w\\S*@([a-zA-Z0-9\\-]+\\.)+[a-zA-Z0-9]{2,4}$";

    /** 检测 手机号 的正则 */
    public static final String PHONE_REGEX = "^1(3[0-9]|5[0-35-9]|8[0-9]|14[57])[0-9]{8}$";

    /** 检测 sql 注入的正则 */
    public static final String SQL_REGEX = "(?:')|(?:--)|;|(/\\*(?:.|[\\n\\r])*?\\*/)|((?i)\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

    /**
     * 空字符串判断, <span style="color:red;">去左右空格后</span>, 如果为空返回 true<br/>
     *
     */
    public static boolean isNull(String param) {
        return (param == null || "".equals(param.trim()));
    }

    /**
     * 空字符串判断, <span style="color:red;">去左右空格后</span>, 如果不为空返回 true<br/>
     *
     */
    public static boolean isNotNull(String param) {
        return !isNull(param);
    }

    /**
     * 检测 email 格式, 匹配则返回 true<br/>
     * 格式: 下划线 数字或字母 @ 字母 数字或中横线 . 2 到 4 个字母或数字<br/>
     *
     */
    public static boolean checkEmail(String email) {
        return checkRegexWithStrict(email, EMAIL_REGEX);
    }

    /**
     * 检测手机号码, 匹配则返回 true<br/><br/>
     * 格式: 130-139, 150-159(除了154), 180,182,182,186-189, 145,147
     *
     */
    public static boolean checkPhone(String phone) {
        return checkRegexWithStrict(phone, PHONE_REGEX);
    }

    /**
     * 检测 SQL, 若有会导致注入的值则返回 true.
     *
     */
    public static boolean checkSqlValue(String sql) {
        return checkRegexWithRelax(sql, SQL_REGEX);
    }

    /**
     * 验证 指定正则 是否 <span style="color:red;">全字匹配</span> 指定字符串, 匹配则返回 true <br/>
     * <br/>
     * 左右空白符 : (?m)(^\s*|\s*$)
     * 帐号输入(字母或数字开头, 长度 5-16, 可以有下划线) : ^[a-zA-Z0-9]\\w{4,15}$<br/>
     * 空白符 : (^\\s*)|(\\s*$)<br/>
     * IP : ([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])(\\.([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])){3}<br/>
     * 日期 : (([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)
     * 匹配多行注释 : /\*\*(\s|.)*?\* /<br/>
     *
     */
    public static boolean checkRegexWithStrict(String param, String regex) {
        return !isNull(param) && Pattern.compile(regex).matcher(param).matches();
    }

    /**
     * 此模式为非严格型匹配, <span style="color:red;">只要找到匹配即返回 true</span><br/>
     * <br/>
     * 中文: [\\u4e00-\\u9fa5]<br/>
     *
     */
    public static boolean checkRegexWithRelax(String param, String regex) {
        return !isNull(param) && Pattern.compile(regex).matcher(param).find();
    }

}
