package com.kariqu.common.oauth.scribe.extractors;

import com.kariqu.common.oauth.scribe.exceptions.OAuthException;
import com.kariqu.common.oauth.scribe.model.Token;
import com.kariqu.common.oauth.scribe.utils.Preconditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTokenExtractor implements AccessTokenExtractor {

    private Pattern accessTokenPattern = Pattern.compile("\"access_token\":\\s*\"(\\S*?)\"");

    private Pattern expireInPattern = Pattern.compile("\"expires_in\":\\s*(\\S*?)");

    @Override
    public Token extract(String response) {
        Preconditions.checkEmptyString(response, "Cannot extract a token from a null or empty String");

        String token = extract(response, accessTokenPattern);
        String expireIn = extract(response, expireInPattern);

        return new Token(token, "", response, expireIn);
    }

    private String extract(String response, Pattern p) {
        Matcher matcher = p.matcher(response);
        if (matcher.find() && matcher.groupCount() >= 1) {
            return matcher.group(1);
        } else {
            throw new OAuthException("Response body is incorrect. Can't extract token info from this: '" + response + "'", null);
        }
    }

}