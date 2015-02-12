package com.kariqu.common.oauth.jointlogin.services.impl;

import com.kariqu.common.oauth.jointlogin.services.AbstractJoinLogin;
import com.kariqu.common.oauth.jointlogin.services.PlatformUserInfo;
import com.kariqu.common.oauth.scribe.api.Api;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;
import com.kariqu.common.oauth.scribe.platform.QQApi;
import com.kariqu.common.oauth.scribe.platform.RenrenApi;

import java.util.Properties;


/**
 * 人人网平台的Oauth服务
 */
public class RenRenServiceImpl extends AbstractJoinLogin {


    public RenRenServiceImpl(Properties config) {
        super(new OAuthConfig(config.getProperty("renren.client_ID"), config.getProperty("renren.client_SERCRET"), config.getProperty("renren.redirect_URI")));
    }

    @Override
    protected PlatformUserInfo buildPlatformUserInfo(OAuthConfig oAuthConfig) {
        return new RenRen();
    }

    @Override
    protected Api buildApi(OAuthConfig oAuthConfig) {
        return new RenrenApi(oAuthConfig);
    }


}
