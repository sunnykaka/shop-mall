package com.kariqu.common.oauth.scribe.api;

import com.kariqu.common.oauth.scribe.model.Token;

/**
 * Contains all the configuration needed to instantiate a valid {@link com.kariqu.common.oauth.scribe.OAuthService}
 *
 * @author Pablo Fernandez
 */
public interface Api {


    Token getAccessToken(String verifier);


    /**
     * Returns the URL where you should redirect your users to authenticate
     * your application.
     *
     * @return the URL where you should redirect your users
     */
    String getAuthorizationUrl();


}
