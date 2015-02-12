package com.kariqu.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public  class UnicodeString {
    public static void main(String[] args) {
        //String str = "中国";   
        // System.out.println(UnicodeString.StringToWebUnicode(str));
        // System.out.println(UnicodeString.WebUnicodeToString("&#20013;&#22269;"));
        System.out.println(UnicodeString.StringToUnicode("品味 尚尼威尼斯单柄24cm煎锅"));
        System.out.println(UnicodeString.UnicodeToString(""));


    }

    /*
    * 普通类型的unicode转string
    */
    public static String UnicodeToString(String input) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(input);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            input = input.replace(matcher.group(1), ch + "");
        }
        return input;
    }

    /*  
    * string转普通类型的unicode
    */
    public static String StringToUnicode(String input) {
        String str = "";
        for (char c : input.toCharArray()) {
            if ((int) c > 128)
                str += "\\u" + Integer.toHexString((int) c);
            else
                str += c;
        }
        return str;

    }

    /*  
    * string转web类型的unicode
    */
    public static String StringToWebUnicode(String input) {
        String str = "";
        for (char c : input.toCharArray()) {
            str += "&#" + (int) c + ";";
        }
        return str;
    }

    /*  
    * web类型的unicode转string
    */
    public static String WebUnicodeToString(String input) {
        String str = "";
        String[] y1 = input.split(";");
        for (String c : y1) {
            if (c.length() > 2) {
                str += (char) Integer.parseInt(c.substring(2));
            }
        }
        return str;
    }
}  

