package com.kariqu.common.oauth.jointlogin.services.impl;

import com.kariqu.common.oauth.jointlogin.services.AbstractJoinLogin;
import com.kariqu.common.oauth.jointlogin.services.PlatformUserInfo;
import com.kariqu.common.oauth.scribe.api.Api;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;
import com.kariqu.common.oauth.scribe.platform.QQApi;

import java.util.Properties;

/**
 * @author Alec  2012-07-13 16:32:20
 *         <p/>
 *         PS: QQ 联合登录
 */
public class QQServiceImpl extends AbstractJoinLogin {


    public QQServiceImpl(Properties config) {
        super(new OAuthConfig(config.getProperty("qq.client_ID"), config.getProperty("qq.client_SERCRET"), config.getProperty("qq.redirect_URI")));
    }

    @Override
    protected PlatformUserInfo buildPlatformUserInfo(OAuthConfig oAuthConfig) {
        return new QQ(oAuthConfig);
    }

    @Override
    protected Api buildApi(OAuthConfig oAuthConfig) {
        return new QQApi(oAuthConfig);
    }
}
