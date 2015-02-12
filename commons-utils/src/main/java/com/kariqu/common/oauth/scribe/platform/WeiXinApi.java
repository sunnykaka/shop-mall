package com.kariqu.common.oauth.scribe.platform;

import com.kariqu.common.oauth.scribe.api.oauth2.DefaultApi20;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;

/**
 * 微信 OAuth 2.0 based api.
 */
public class WeiXinApi extends DefaultApi20 {

    private static final String AUTHORIZE_URL = "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code";//"https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s";
    private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";


    public WeiXinApi(OAuthConfig config) {
        super(config);
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code";//"https://graph.qq.com/oauth2.0/token?grant_type=authorization_code";
    }

    /**
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     * @return
     */
    @Override
    public String getAuthorizationUrl() {
        if (config.hasScope()) {
            return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), config.getCallback(), config.getScope()) + "#wechat_redirect";
        } else {
            return String.format(AUTHORIZE_URL, config.getApiKey(), config.getCallback()) + "#wechat_redirect";
        }
    }
}
