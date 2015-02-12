package com.kariqu.tradecenter.domain;

/**
 * 单个进度. 主要显示时间和说明
 *
 * @author Athens(刘杰)
 */
public class ProgressDetail {

    /**
     * 详情
     */
    private String detail;

    /**
     * 时间, 用字符串表示
     */
    private String date;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 详情
     */
    public String getDetail() {
        return detail;
    }

    /**
     * 详情
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * 时间, 用字符串表示
     */
    public String getDate() {
        return date;
    }

    /**
     * 时间, 用字符串表示
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 操作者
     */
    public String getOperator() {
        return operator;
    }

    /**
     * 操作者
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "时间:(" + date + ") 操作人:(" + operator + ") 详情:(" + detail + ")";
    }
}
