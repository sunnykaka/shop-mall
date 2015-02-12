package com.kariqu.usercenter.repository;

import com.kariqu.usercenter.domain.StatisticsEntry;
import com.kariqu.usercenter.domain.UserEntryInfo;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Athens(刘杰)
 * @Time 2012-09-05 17:55
 * @since 1.0.0
 */
public class StatisticsEntryRepository extends SqlMapClientDaoSupport {
    
    public long createEntry(StatisticsEntry entry) {
        return (Long) getSqlMapClientTemplate().insert("insertEntry", entry);
    }

    public int deleteEntry(String userName) {
        return getSqlMapClientTemplate().update("updateStatusEntryWithUser", userName);
    }

    public int physicsDeleteEntry(String userName) {
        return getSqlMapClientTemplate().delete("deleteEntryWithUser", userName);
    }

    public int queryCountEntry(String userName) {
        return (Integer) getSqlMapClientTemplate().queryForObject("selectEntryCount", userName);
    }

    public List<StatisticsEntry> queryEntryByName(String userName, int start, int limit) {
        Map map = new HashMap();
        map.put("userName", userName);
        map.put("start", start);
        map.put("limit", limit);
        return getSqlMapClientTemplate().queryForList("selectUserEntry", map);
    }

    public List<StatisticsEntry> queryEntryWithNewCount(String userName, int count) {
        Map map = new HashMap();
        map.put("userName", userName);
        map.put("limit", count);
        return getSqlMapClientTemplate().queryForList("selectEntryWithNewCount", map);
    }

    public List<UserEntryInfo> queryActiveEntry(int start, int limit) {
        Map map = new HashMap();
        map.put("start", start);
        map.put("limit", limit);
        return getSqlMapClientTemplate().queryForList("selectActiveEntry", map);
    }

}
