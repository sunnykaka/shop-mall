package com.kariqu.common.oauth.scribe.api.oauth2;

import com.kariqu.common.http.Response;
import com.kariqu.common.oauth.scribe.api.Api;
import com.kariqu.common.oauth.scribe.extractors.AccessTokenExtractor;
import com.kariqu.common.oauth.scribe.extractors.TokenExtractor20Impl;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;
import com.kariqu.common.http.Verb;
import com.kariqu.common.oauth.scribe.model.OAuthConstants;
import com.kariqu.common.oauth.scribe.model.OAuthRequest;
import com.kariqu.common.oauth.scribe.model.Token;

/**
 * Default implementation of the OAuth protocol, version 2.0 (draft 11)
 * <p/>
 * This class is meant to be extended by concrete implementations of the API,
 * providing the endpoints and endpoint-http-verbs.
 * <p/>
 * If your Api adheres to the 2.0 (draft 11) protocol correctly, you just need to extend
 * this class and define the getters for your endpoints.
 * <p/>
 * If your Api does something a bit different, you can override the different
 * extractors or services, in order to fine-tune the process. Please read the
 * javadocs of the interfaces to get an idea of what to do.
 *
 * @author Diego Silveira
 */
public abstract class DefaultApi20 implements Api {

    protected OAuthConfig config;

    protected DefaultApi20(OAuthConfig config) {
        this.config = config;
    }

    /**
     * Returns the access token extractor.
     *
     * @return access token extractor
     */
    public AccessTokenExtractor getAccessTokenExtractor() {
        return new TokenExtractor20Impl();
    }

    /**
     * Returns the verb for the access token endpoint (defaults to GET)
     *
     * @return access token endpoint verb
     */
    public Verb getAccessTokenVerb() {
        return Verb.GET;
    }

    /**
     * Returns the URL that receives the access token requests.
     *
     * @return access token URL
     */
    public abstract String getAccessTokenEndpoint();


    public Token getAccessToken(String verifier) {
        OAuthRequest request = new OAuthRequest(getAccessTokenVerb(), getAccessTokenEndpoint());
        request.addQuerystringParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
        request.addQuerystringParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
        request.addQuerystringParameter(OAuthConstants.CODE, verifier);
        request.addQuerystringParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
        if (config.hasScope()) request.addQuerystringParameter(OAuthConstants.SCOPE, config.getScope());
        Response response = request.send();
        return getAccessTokenExtractor().extract(response.getBody());
    }


}
