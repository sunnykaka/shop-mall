package com.kariqu.usercenter.service;

import com.kariqu.usercenter.domain.SubscriptionInfo;

import java.util.List;

/**
 * User: kyle
 * Date: 12-12-29
 * Time: 下午6:32
 */
public interface SubscriptionInfoService {

    public void createSubscriptionInfo(SubscriptionInfo subscriptionInfo);

    public SubscriptionInfo getSubscriptionInfo(long id);

    public List<SubscriptionInfo>  queryAll();
}
