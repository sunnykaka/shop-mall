package com.kariqu.common.pinyin;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个字的拼音
 * User: Asion
 * Date: 12-7-1
 * Time: 上午10:18
 */
public class PinYinWord {

    private String data;

    private boolean first;

    public PinYinWord(String data) {
        this.data = data;
    }

    public PinYinWord(String data, boolean first) {
        this.data = data;
        this.first = first;
    }

    public String getAll(ComposeMode mode) {
        if (mode == ComposeMode.ALL) {
            return data;
        } else if (mode == ComposeMode.Single) {
            return data.substring(0, 1);
        } else if (mode == ComposeMode.SingleExcludeFirst && first) {
            return data;
        } else if (mode == ComposeMode.SingleExcludeFirst) {
            return data.substring(0, 1);
        }
        return "";
    }

    /**
     * 分析一个字符串，从开始截取子串，每次长度加1
     *
     * @param all
     * @return
     */
    public static List<String> parseAll(String all) {
        List<String> list = new ArrayList<String>();
        for (int i = 1; i < all.length(); i++) {
            list.add(all.substring(0, i));
        }
        return list;
    }

    public static enum ComposeMode {
        /**
         * 词语的全部
         */
        ALL,

        /**
         * 词语的首字母
         */
        Single,

        /**
         * 首词全拼，其他词首字母
         */
        SingleExcludeFirst
    }

}

