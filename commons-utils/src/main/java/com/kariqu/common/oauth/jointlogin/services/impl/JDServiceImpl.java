package com.kariqu.common.oauth.jointlogin.services.impl;

import com.kariqu.common.oauth.jointlogin.services.AbstractJoinLogin;
import com.kariqu.common.oauth.jointlogin.services.PlatformUserInfo;
import com.kariqu.common.oauth.scribe.api.Api;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;
import com.kariqu.common.oauth.scribe.platform.JDApi;

import java.util.Properties;

/**
 * 京东平台的Oauth服务
 */
public class JDServiceImpl extends AbstractJoinLogin {

    public JDServiceImpl(Properties config) {

        super(new OAuthConfig(config.getProperty("jingdong.client_ID"), config.getProperty("jingdong.client_SERCRET"), config.getProperty("jingdong.redirect_URI")));

    }

    @Override
    protected PlatformUserInfo buildPlatformUserInfo(OAuthConfig oAuthConfig) {
        return new JD();
    }

    @Override
    protected Api buildApi(OAuthConfig oAuthConfig) {
        return new JDApi(oAuthConfig);
    }
}
