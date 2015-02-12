package com.kariqu.usercenter.domain;

import java.util.Date;

/**
 * 记录用户登录信息的实体 Bean.
 *
 * @author Athens(刘杰)
 * @Time 2012-09-05 15:39
 * @since 1.0.0
 */
public class StatisticsEntry {

    /** 主键 */
    private long entryId;

    /** 用户名 */
    private String userName;

    /** 登录时间 */
    private Date entryTime;

    /** 登录时的 IP */
    private String entryIP;

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    public String getEntryIP() {
        return entryIP;
    }

    public void setEntryIP(String entryIP) {
        this.entryIP = entryIP;
    }

}
