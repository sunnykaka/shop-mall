package com.kariqu.common.oauth.scribe.extractors;

import com.kariqu.common.http.Utf8UrlEncoder;
import com.kariqu.common.oauth.scribe.exceptions.OAuthException;
import com.kariqu.common.oauth.scribe.model.Token;
import com.kariqu.common.oauth.scribe.utils.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of {@AccessTokenExtractor}. Conforms to OAuth 2.0
 */
public class TokenExtractor20Impl implements AccessTokenExtractor {

    private Pattern accessTokenPattern = Pattern.compile("access_token=([^&]+)");

    private Pattern expireInPattern = Pattern.compile("expires_in=([^&]+)");

    /**
     * {@inheritDoc}
     */
    public Token extract(String response) {
        Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");

        String token = extract(response, accessTokenPattern);
        String expireIn = extract(response, expireInPattern);

        return new Token(token, "", response, expireIn);
    }


    private String extract(String response, Pattern p) {
        Matcher matcher = p.matcher(response);
        if (matcher.find() && matcher.groupCount() >= 1) {
            return Utf8UrlEncoder.decode(matcher.group(1));
        } else {
            throw new OAuthException("Response body is incorrect. Can't extract token info from this: '" + response + "'", null);
        }
    }
}
