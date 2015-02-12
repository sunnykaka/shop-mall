package com.kariqu.common.oauth.jointlogin.services;

import com.kariqu.common.oauth.jointlogin.OuterUserInfo;
import com.kariqu.common.oauth.scribe.model.Token;

/**
 * User: Asion
 * Date: 13-5-23
 * Time: 上午11:43
 */
public interface PlatformUserInfo {

    OuterUserInfo getUserInfo(Token accessToken);
}
