package com.kariqu.common.oauth.jointlogin.services.impl;

import com.kariqu.common.oauth.jointlogin.OuterUserInfo;

import java.util.Map;

/**
 * 解析用户信息
 * User: Alec
 * Date: 13-5-24
 * Time: 下午2:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class AnalyzeUserMap {

    protected OuterUserInfo getJointLoginInfo(Map<String, Object> userMap, Map<String, String> parameter) {
        OuterUserInfo userinfo = new OuterUserInfo();
        if (!userMap.isEmpty()) {
            userinfo.setOuterId(String.valueOf(userMap.get(parameter.get("id"))));
            userinfo.setUserName(String.valueOf(userMap.get(parameter.get("name"))));
            userinfo.setUserHeadImg(String.valueOf(userMap.get(parameter.get("profile"))));
            userinfo.setFlag(String.valueOf(userMap.get("flag")));
            userinfo.setAccessToken(String.valueOf(userMap.get("accessToken")));
        }
        return userinfo;
    }
}
