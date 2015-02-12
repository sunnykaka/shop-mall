package com.kariqu.common;

import org.apache.commons.lang.StringUtils;

/**
 * add by Athens on 14-2-24
 */
public class BCConvert {
    
    /** ASCII 表中可见字符从 ! 开始，偏移位值为 33(Decimal) */
    private static final char DBC_CHAR_START = 33; // 半角!

    /** ASCII 表中可见字符到 ~ 结束，偏移位值为 126(Decimal) */
    private static final char DBC_CHAR_END = 126; // 半角~

    /** 全角对应于 ASCII 表的可见字符从！开始，偏移值为65281 */
    private static final char SBC_CHAR_START = 65281; // 全角！

    /** 全角对应于 ASCII 表的可见字符到～结束，偏移值为65374 */
    private static final char SBC_CHAR_END = 65374; // 全角～

    /** ASCII 表中除空格外的可见字符与对应的全角字符的相对偏移 */
    private static final int CONVERT_STEP = 65248;

    /** 全角空格的值，它没有遵从与 ASCII 的相对偏移，须单独处理 */
    private static final char SBC_SPACE = 12288;

    /** 半角空格的值，在 ASCII 中为 32(Decimal) */
    private static final char DBC_SPACE = ' ';

    /**
     * 半角字符 -> 全角字符转换
     */
    public static String bj2qj(String src) {
        if (StringUtils.isEmpty(src)) {
            return src;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (char c : ca) {
            if (c == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代
                buf.append(SBC_SPACE);
            } else if ((c >= DBC_CHAR_START) && (c <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符
                buf.append((char) (c + CONVERT_STEP));
            } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * 全角字符 -> 半角字符转换
     */
    public static String qj2bj(String src) {
        if (StringUtils.isEmpty(src)) {
            return src;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (char c : ca) {
            if (c >= SBC_CHAR_START && c <= SBC_CHAR_END) {
                buf.append((char) (c - CONVERT_STEP));
            } else if (c == SBC_SPACE) {
                buf.append(DBC_SPACE);
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        System.out.println(qj2bj("，　．／"));
        System.out.println(qj2bj("６６８６６８８４６８６３４８０"));

        System.out.println(bj2qj(", ./"));
        System.out.println(bj2qj("668668846863480"));
    }

}
