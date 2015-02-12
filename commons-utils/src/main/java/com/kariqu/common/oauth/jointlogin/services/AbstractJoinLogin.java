package com.kariqu.common.oauth.jointlogin.services;

import com.kariqu.common.oauth.jointlogin.OuterUserInfo;
import com.kariqu.common.oauth.scribe.api.Api;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;
import com.kariqu.common.oauth.scribe.model.Token;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: Asion
 * Date: 13-5-23
 * Time: 上午11:54
 */
public abstract class AbstractJoinLogin implements JointLoginService {

    private static Log logger = LogFactory.getLog(AbstractJoinLogin.class);

    private Api api;

    private PlatformUserInfo platformUserInfo;

    protected AbstractJoinLogin(OAuthConfig oAuthConfig) {
        api = buildApi(oAuthConfig);
        platformUserInfo = buildPlatformUserInfo(oAuthConfig);
    }

    protected abstract PlatformUserInfo buildPlatformUserInfo(OAuthConfig oAuthConfig);

    protected abstract Api buildApi(OAuthConfig oAuthConfig);

    @Override
    public OuterUserInfo userInfo(String code) {
        Token accessToken = api.getAccessToken(code);
        return platformUserInfo.getUserInfo(accessToken);
    }

    @Override
    public String auth() {
        try {
            return api.getAuthorizationUrl();
        } catch (Exception ex) {
            logger.error("获取第三方登录地址出错" + ex);
            return "";
        }
    }


}
