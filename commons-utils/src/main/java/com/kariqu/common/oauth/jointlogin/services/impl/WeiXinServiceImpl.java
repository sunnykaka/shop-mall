package com.kariqu.common.oauth.jointlogin.services.impl;

import com.kariqu.common.oauth.jointlogin.services.AbstractJoinLogin;
import com.kariqu.common.oauth.jointlogin.services.PlatformUserInfo;
import com.kariqu.common.oauth.scribe.api.Api;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;
import com.kariqu.common.oauth.scribe.platform.WeiXinApi;

import java.util.Properties;

/**
 * @author Alec  2012-07-13 16:32:20
 *         <p/>
 *         PS: 微信 联合登录
 */
public class WeiXinServiceImpl extends AbstractJoinLogin {


    public WeiXinServiceImpl(Properties config) {
        super(new OAuthConfig(config.getProperty("weixin.client_ID"), config.getProperty("weixin.client_SERCRET"), config.getProperty("weixin.redirect_URI")));
    }

    @Override
    protected PlatformUserInfo buildPlatformUserInfo(OAuthConfig oAuthConfig) {
        return new WeiXin(oAuthConfig);
    }

    @Override
    protected Api buildApi(OAuthConfig oAuthConfig) {
        return new WeiXinApi(oAuthConfig);
    }
}
