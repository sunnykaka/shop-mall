package com.kariqu.common.oauth.jointlogin.services.impl;

import com.kariqu.common.oauth.jointlogin.services.AbstractJoinLogin;
import com.kariqu.common.oauth.jointlogin.services.PlatformUserInfo;
import com.kariqu.common.oauth.scribe.api.Api;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;
import com.kariqu.common.oauth.scribe.platform.TaobaoApi;

import java.util.Properties;

/**
 * Taobao平台的Oauth服务
 * User: Alec
 * Date: 12-11-1
 * Time: 上午11:30
 */
public class TaoBaoServiceImpl extends AbstractJoinLogin {


    public TaoBaoServiceImpl(Properties config) {
        super(new OAuthConfig(config.getProperty("taobao.client_ID"), config.getProperty("taobao.client_SERCRET"), config.getProperty("taobao.redirect_URI")));
    }

    @Override
    protected PlatformUserInfo buildPlatformUserInfo(OAuthConfig oAuthConfig) {
        return new TaoBao();
    }

    @Override
    protected Api buildApi(OAuthConfig oAuthConfig) {
        return new TaobaoApi(oAuthConfig);
    }
}
