package com.kariqu.om.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.om.domain.SystemLog;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-4
 *        Time: 下午2:10
 */
public class SystemLogRepository extends SqlMapClientDaoSupport  {

    
    public void createSystemLog(SystemLog systemLog) {
        getSqlMapClientTemplate().insert("createSystemLog", systemLog);
    }

    
    public void deleteSystemLog(int id) {
        getSqlMapClientTemplate().delete("deleteSystemLogById", id);
    }

    
    public Page<SystemLog> querySystemLogByPage(int pageNo, int pageSize, String title, String ip) {
        Page<SystemLog> page = new Page<SystemLog>(pageNo, pageSize);
        Map param = new HashMap();
        param.put("title", title);
        param.put("ip", ip);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());

        Map map = new HashMap();
        param.put("title", title);
        param.put("ip", ip);
        List<SystemLog> systemLogList = getSqlMapClientTemplate().queryForList("queryByTitleAndIp", param);
        page.setResult(systemLogList);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("queryAllByTitleAndIp", map));

        return page;
    }
}
