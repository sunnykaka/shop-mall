package com.kariqu.common.oauth.scribe.platform;

import com.kariqu.common.oauth.scribe.api.oauth2.DefaultApi20;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;

/**
 * QQ OAuth 2.0 based api.
 */
public class QQApi extends DefaultApi20 {

    private static final String AUTHORIZE_URL = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s";
    private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";


    public QQApi(OAuthConfig config) {
        super(config);
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code";
    }

    @Override
    public String getAuthorizationUrl() {
        if (config.hasScope()) {
            return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), config.getCallback(), config.getScope());
        } else {
            return String.format(AUTHORIZE_URL, config.getApiKey(), config.getCallback());
        }
    }
}
