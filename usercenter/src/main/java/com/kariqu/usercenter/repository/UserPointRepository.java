package com.kariqu.usercenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.service.UserPointQuery;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Asion
 * Date: 13-3-18
 * Time: 上午10:25
 */
public class UserPointRepository extends SqlMapClientDaoSupport  {

    public void createUsePoint(UserPoint userPoint) {
        getSqlMapClientTemplate().insert("insertUserPoint", userPoint);
    }

    public Page<UserPoint> queryUserPoint(UserPointQuery query) {
        Page<UserPoint> page = new Page<UserPoint>(query.getPageNo(), query.getPageSize());
        Map param = new HashMap();
        param.put("userId", query.getUserId());
        param.put("type", query.getType());
        int totalCount = (Integer) getSqlMapClientTemplate().queryForObject("selectCountUserPoint", param);
        //如果输入的页数超过总页数，则页数位置为第一页
        if(page.getStart() >= totalCount){
           page.setPageNo(1);
        }
        page.setTotalCount(totalCount);
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        page.setResult(getSqlMapClientTemplate().queryForList("queryUserPoint", param));
        return page;
    }
}
