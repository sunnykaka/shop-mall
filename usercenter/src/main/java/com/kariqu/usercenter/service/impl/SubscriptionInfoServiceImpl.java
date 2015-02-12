package com.kariqu.usercenter.service.impl;

import com.kariqu.usercenter.domain.SubscriptionInfo;
import com.kariqu.usercenter.repository.SubscriptionInfoRepository;
import com.kariqu.usercenter.service.SubscriptionInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: kyle
 * Date: 12-12-29
 * Time: 下午6:35
 */
public class SubscriptionInfoServiceImpl implements SubscriptionInfoService {

    @Autowired
    private SubscriptionInfoRepository subscriptionInfoRepository;


    @Override
    public void createSubscriptionInfo(SubscriptionInfo subscriptionInfo) {
        subscriptionInfoRepository.createSubscriptionInfo(subscriptionInfo);
    }

    @Override
    public SubscriptionInfo getSubscriptionInfo(long id) {

        return  subscriptionInfoRepository.getSubscriptionInfoById(id);
    }

    @Override
    public List<SubscriptionInfo> queryAll() {
        return subscriptionInfoRepository.queryAllSubscriptionInfo();
    }
}
