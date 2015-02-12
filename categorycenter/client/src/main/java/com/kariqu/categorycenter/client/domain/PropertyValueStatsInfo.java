package com.kariqu.categorycenter.client.domain;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 属性值的统计信息，比如三星(20)
 * 将会用在列表页的筛选界面和和导航类目按照属性值搜索的地方
 * User: Asion
 * Date: 11-8-8
 * Time: 下午4:39
 */
public class PropertyValueStatsInfo {

    private String value;

    private long pidvid;

    private long count;

    /**
     * @param value 属性值，比如三星，金属，22寸
     * @param count 这个值的数量
     * @param pidvid 属性ID和值ID组合的long值
     */
    public PropertyValueStatsInfo(String value, long count, long pidvid) {
        this.value = value;
        this.count = count;
        this.pidvid = pidvid;
    }

    /**
     * 用属性值和pidvid构造，不统计
     * @param value
     * @param pidvid
     */
    public PropertyValueStatsInfo(String value, long pidvid) {
        this.value = value;
        this.pidvid = pidvid;
    }

    /** 若有中文则显示中文 */
    public String getDisplayValue() {
        if (StringUtils.isBlank(value)) return StringUtils.EMPTY;
        if (value.length() < 4) return value;

        Matcher matcher = Pattern.compile("(\\d|[\\u4e00-\\u9fff])").matcher(value);
        StringBuilder sbd = new StringBuilder();
        while (matcher.find()) {
            sbd.append(matcher.group());
        }
        return StringUtils.isNotBlank(sbd.toString()) ? sbd.toString() : value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getPidvid() {
        return pidvid;
    }
}
