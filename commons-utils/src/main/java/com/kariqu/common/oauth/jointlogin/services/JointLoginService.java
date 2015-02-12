package com.kariqu.common.oauth.jointlogin.services;


import com.kariqu.common.oauth.jointlogin.OuterUserInfo;

import java.util.Properties;

/**
 * 供外部使用的Oauth服务
 * <p/>
 * 提供两个方法，auth用于将用户导航到第三方登陆页面
 * userInfo利用第三方平台返回的code获取用户的信息
 */
public interface JointLoginService {

    OuterUserInfo userInfo(String code);

    String auth();

}
