package com.kariqu.om.domain;

import java.util.Date;

/**
 * @author Athens(刘杰)
 * @Time 13-10-11 下午6:02
 */
public class Const {

    private int id;

    /** 全局常量键 */
    private String constKey;

    /** 全局常量键值 */
    private String constValue;

    /** 常量说明 */
    private String constComment;

    private Date createDate;
    private Date updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConstKey() {
        return constKey;
    }

    public void setConstKey(String constKey) {
        this.constKey = constKey;
    }

    public String getConstValue() {
        return constValue;
    }

    public void setConstValue(String constValue) {
        this.constValue = constValue;
    }

    public String getConstComment() {
        return constComment;
    }

    public void setConstComment(String constComment) {
        this.constComment = constComment;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
