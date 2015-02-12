package com.kariqu.common.oauth.scribe.model;

import com.kariqu.common.oauth.scribe.utils.Preconditions;

/**
 * Parameter object that groups OAuth config values
 *
 * @author Pablo Fernandez
 */
public class OAuthConfig {
    private final String apiKey;
    private final String apiSecret;
    private final String callback;
    private final String scope;

    public OAuthConfig(String key, String secret, String callback) {
        this(key, secret, callback, null);
    }

    public OAuthConfig(String key, String secret, String callback, String scope) {
        Preconditions.checkEmptyString(key, "You must provide an api key");
        Preconditions.checkEmptyString(secret, "You must provide an api secret");
        Preconditions.checkEmptyString(callback, "You must provide an api callback");
        this.apiKey = key;
        this.apiSecret = secret;
        this.callback = callback;
        this.scope = scope;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getCallback() {
        return callback;
    }

    public String getScope() {
        return scope;
    }

    public boolean hasScope() {
        return scope != null;
    }

}
