package com.kariqu.common.pinyin;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.*;

/**
 * 拼音工具
 * User: Asion
 * Date: 12-6-22
 * Time: 下午2:35
 */
public class PinYinUtil {

    /**
     * 将汉语句子转为拼音
     *
     * @param chinaChar
     * @return
     */
    public static String getPinYin(String chinaChar) {
        List<PinYinWord> list = parsePinYin(chinaChar);
        return getAll(list);
    }

    /**
     * 得到拼音组合，比如炒锅得到chaoguo,cg,chaog,cguo
     *
     * @param chinaChar
     * @return
     */
    public static Collection<String> getComposePinYin(String chinaChar) {
        List<PinYinWord> list = parsePinYin(chinaChar);
        Set<String> set = new HashSet<String>();

        String all = getAll(list);

        set.add(all);
        set.addAll(PinYinWord.parseAll(all));


        StringBuilder sb = new StringBuilder();
        for (PinYinWord pinYinWord : list) {
            sb.append(pinYinWord.getAll(PinYinWord.ComposeMode.Single));
        }
        set.add(sb.toString());

        sb = new StringBuilder();
        for (PinYinWord pinYinWord : list) {
            sb.append(pinYinWord.getAll(PinYinWord.ComposeMode.SingleExcludeFirst));
        }
        set.add(sb.toString());
        return set;
    }

    /**
     * 得到全组合，比如炒锅是chaoguo
     *
     * @param list
     * @return
     */
    private static String getAll(List<PinYinWord> list) {
        StringBuffer sb = new StringBuffer();
        for (PinYinWord pinyin : list) {
            sb.append(pinyin.getAll(PinYinWord.ComposeMode.ALL));
        }
        return sb.toString();
    }

    /**
     * 给定中文句子，解析出拼音列表
     *
     * @param chinaChar
     * @return
     */
    private static List<PinYinWord> parsePinYin(String chinaChar) {
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        List<PinYinWord> list = new LinkedList<PinYinWord>();
        try {
            char[] chars = chinaChar.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                String[] strings = PinyinHelper.toHanyuPinyinStringArray(chars[i], t3);
                if (strings != null && strings.length > 0) {
                    String str = strings[0];
                    if (! "none0".equalsIgnoreCase(str)) { //处理 PinyinHelper.toHanyuPinyinStringArray 返回汉字没有发音的情况
                        list.add(new PinYinWord(str, i == 0));
                    }
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }
}
