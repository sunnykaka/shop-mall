package com.kariqu.common.oauth.scribe.platform;

import com.kariqu.common.oauth.scribe.api.oauth2.DefaultApi20;
import com.kariqu.common.oauth.scribe.extractors.AccessTokenExtractor;
import com.kariqu.common.oauth.scribe.extractors.JsonTokenExtractor;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;

/**
 * Renren(http://www.renren.com/) OAuth 2.0 based api.
 */
public class RenrenApi extends DefaultApi20 {


    private static final String AUTHORIZE_URL = "https://graph.renren.com/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s";
    private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

    public RenrenApi(OAuthConfig config) {
        super(config);
    }


    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new JsonTokenExtractor();
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://graph.renren.com/oauth/token?grant_type=authorization_code";
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
