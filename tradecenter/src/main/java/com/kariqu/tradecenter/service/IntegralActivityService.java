package com.kariqu.tradecenter.service;

import com.kariqu.productcenter.domain.ProductIntegralConversion;
import com.kariqu.productcenter.domain.ProductSuperConversion;
import com.kariqu.tradecenter.domain.CanntJoinActivityResult;
import com.kariqu.usercenter.domain.User;

import java.util.List;
import java.util.Map;

/**
 * 积分活动, 包括积分兑换, 积分优惠购
 * Created by Canal.wen on 2014/7/15 16:51.
 */
public interface IntegralActivityService {

    /**
     * 用户已参加指定活动次数
     * @param user
     * @param activityType
     * @param activityId
     * @return
     */
    int userHasJoinCount(User user, String activityType, int activityId);

    CanntJoinActivityResult checkUserCanJoin(User user, String activityType, int activityId);

    /**
     * 获取 当前 积分兑换 活动的信息. 包括商品信息
     * @return
     */
    List<Map<String, Object>> fetchIntegralConversions();


    ProductIntegralConversion fetchIntegralConversionInfo(int activityId);

    Map<String,Object> fetchIntegralConversionById(int activityId);

    /**
     * 获取 当前 积分优惠购 活动的信息. 包括商品信息
     * @return
     */
    List<Map<String,Object>> fetchSuperConversion();

    ProductSuperConversion fetchSuperConversionInfo(int activityId);

    Map<String,Object> fetchSuperConversionById(int activityId);
}
