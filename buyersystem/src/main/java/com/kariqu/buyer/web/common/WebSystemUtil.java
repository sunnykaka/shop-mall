package com.kariqu.buyer.web.common;


import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-3
 *        Time: 下午1:01
 */
public class WebSystemUtil {
    /**
     * 检查邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        return checkRegex("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$", email);
    }

    /**
     * 检查邮政编码
     *
     * @param zipCode
     * @return
     */
    public static boolean checkZipCode(String zipCode) {
        return checkRegex("\\d{6}", zipCode);
    }

    /**
     * 检查手机号
     *
     * @param mobile
     * @return
     */
    public static boolean checkMobile(String mobile) {
        return checkRegex("^1(3[0-9]|5[0-35-9]|8[0-9]|4[57]|7[0678])[0-9]{8}$", mobile);
    }

    public static boolean checkRegex(String regex, String content) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }
        return Pattern.compile(regex).matcher(content).matches();
    }

    /**
     * 检查是否为正整数 不包含 0
     *
     * @param num
     * @return
     */
    public static boolean checkPositiveInteger(String num) {
        return checkRegex("^[0-9]*[1-9][0-9]*$", num);
    }


    public static void main(String[] args) {
        System.out.println(checkZipCode("123456"));
        System.out.println(checkEmail("gewg@qq.com"));
        System.out.println(checkMobile("15889529496"));
        System.out.println(checkPositiveInteger("518000"));
    }
}
