package com.kariqu.usercenter.service;

import com.kariqu.usercenter.domain.StatisticsEntry;
import com.kariqu.usercenter.domain.UserEntryInfo;

import java.util.List;

/**
 * 记录登录的 service.
 *
 * @author Athens(刘杰)
 * @Time 2012-09-06 11:54
 * @since 1.0.0
 */
public interface StatisticsEntryService {

    /**
     * 创建登录记录(在每次用户登陆时)
     *
     * @param entry
     * @return 创建成功后的主键
     */
    long createEntry(StatisticsEntry entry);

    /**
     * 查询用户的登录次数.
     *
     * @param userName 用户名
     * @return
     */
    int queryCountEntry(String userName);

    /**
     * 删除用户的登录记录.(标记删除)
     *
     * @param userName 用户名
     * @return
     */
    int deleteEntry(String userName);

    /**
     * 物理删除用户登录记录.
     *
     * @param userName
     * @return
     */
    int physicsDeleteEntry(String userName);

    /**
     * 查询用户的登录详情.
     *
     * @param userName 用户名
     * @param start 查询的起始位置
     * @param limit 查询条数
     * @return
     */
    List<StatisticsEntry> queryEntryByName(String userName, int start, int limit);

    /**
     * 查询最近的登录记录.
     *
     * @param userName 用户名
     * @param count 需要查询的记录条数
     * @return
     */
    List<StatisticsEntry> queryEntryWithNewCount(String userName, int count);

    /**
     * 按倒序方式(最多的放在最前面)查询用户的访问记录数(记录数相同时以 "用户名" 的自然数排序)
     *
     * @param start 查询的起始位置
     * @param limit 查询条数
     * @return 记录用户名及访问总次数.
     */
    List<UserEntryInfo> queryActiveEntry(int start, int limit);

}
