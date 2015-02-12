package com.kariqu.common.oauth.jointlogin.services.impl;

import com.kariqu.common.oauth.jointlogin.services.AbstractJoinLogin;
import com.kariqu.common.oauth.jointlogin.services.PlatformUserInfo;
import com.kariqu.common.oauth.scribe.api.Api;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;
import com.kariqu.common.oauth.scribe.platform.RenrenApi;
import com.kariqu.common.oauth.scribe.platform.SinaWeiboApi20;

import java.util.Properties;

/**
 * 新浪平台的Oauth服务
 */
public class SinaServiceImpl extends AbstractJoinLogin {

    public SinaServiceImpl(Properties config) {

        super(new OAuthConfig(config.getProperty("sina.client_ID"), config.getProperty("sina.client_SERCRET"), config.getProperty("sina.redirect_URI")));

    }

    @Override
    protected PlatformUserInfo buildPlatformUserInfo(OAuthConfig oAuthConfig) {
        return new Sina();
    }

    @Override
    protected Api buildApi(OAuthConfig oAuthConfig) {
        return new SinaWeiboApi20(oAuthConfig);
    }
}
