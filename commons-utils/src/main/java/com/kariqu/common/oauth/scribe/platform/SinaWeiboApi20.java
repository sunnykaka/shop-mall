package com.kariqu.common.oauth.scribe.platform;

import com.kariqu.common.oauth.scribe.api.oauth2.DefaultApi20;
import com.kariqu.common.oauth.scribe.extractors.AccessTokenExtractor;
import com.kariqu.common.oauth.scribe.extractors.JsonTokenExtractor;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;
import com.kariqu.common.http.Verb;

/**
 * SinaWeibo OAuth 2.0 api.
 */
public class SinaWeiboApi20 extends DefaultApi20 {
    private static final String AUTHORIZE_URL = "https://api.weibo.com/oauth2/authorize?client_id=%s&redirect_uri=%s&response_type=code";
    private static final String ACCESS_TOKEN_URL = "https://api.weibo.com/oauth2/access_token?grant_type=authorization_code";
    private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

    public SinaWeiboApi20(OAuthConfig config) {
        super(config);
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new JsonTokenExtractor();
    }

    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_URL;
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
