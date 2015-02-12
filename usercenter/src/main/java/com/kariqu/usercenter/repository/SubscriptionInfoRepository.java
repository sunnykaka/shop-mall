package com.kariqu.usercenter.repository;

import com.kariqu.usercenter.domain.SubscriptionInfo;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

/**
 * User: kyle
 * Date: 12-12-29
 * Time: 下午6:36
 */
public class SubscriptionInfoRepository extends SqlMapClientDaoSupport  {

    public void createSubscriptionInfo(SubscriptionInfo entity) {
        this.getSqlMapClientTemplate().insert("insertSubscriptionInfo",entity);

    }

    public SubscriptionInfo getSubscriptionInfoById(Long id) {
        return   (SubscriptionInfo)this.getSqlMapClientTemplate().queryForObject("querySubscriptionInfoById",id);
    }


    public void deleteSubscriptionInfoById(Long id) {
        this.getSqlMapClientTemplate().update("deleteSubscriptionInfoById", id);
    }

    public List<SubscriptionInfo> queryAllSubscriptionInfo() {
        return   this.getSqlMapClientTemplate().queryForList("querySubscriptionInfoAll");
    }
}
