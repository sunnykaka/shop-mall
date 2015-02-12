package com.kariqu.common.oauth.jointlogin.services.impl;

import com.kariqu.common.http.Response;
import com.kariqu.common.http.Verb;
import com.kariqu.common.oauth.jointlogin.OuterUserInfo;
import com.kariqu.common.oauth.jointlogin.services.PlatformUserInfo;
import com.kariqu.common.oauth.scribe.exceptions.OAuthException;
import com.kariqu.common.oauth.scribe.model.OAuthConstants;
import com.kariqu.common.oauth.scribe.model.OAuthRequest;
import com.kariqu.common.oauth.scribe.model.Token;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: Asion
 * Date: 13-5-23
 * Time: 下午12:34
 */
public class TaoBao implements PlatformUserInfo {

    private final Log logger = LogFactory.getLog(TaoBao.class);


    public static final String USERINFO_URL = "https://eco.taobao.com/router/rest";


    @Override
    public OuterUserInfo getUserInfo(Token accessToken) {
        OAuthRequest request = new OAuthRequest(Verb.GET, USERINFO_URL);

        request.addQuerystringParameter("method", "taobao.user.buyer.get");
        request.addQuerystringParameter("format", "json");
        request.addQuerystringParameter("v", "2.0");
        request.addQuerystringParameter("fields", "user_id,nick,avatar");
        request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
        Response response = request.send();

        String body = response.getBody();

        if (body.indexOf("error_response") != -1) {
            logger.error("调用淘宝api发生错误:" + body);
            throw new OAuthException("调用淘宝api发生错误");
        }

        try {
            JSONObject jsonObject = new JSONObject(body);
            jsonObject = jsonObject.getJSONObject("user_buyer_get_response").getJSONObject("user");

            OuterUserInfo userinfo = new OuterUserInfo();
            userinfo.setUserName(jsonObject.getString("nick"));
            /**
             * taobao.user.buyer.get API将只返回nick和avatar 两个字段
             * 淘宝昵称不可改变当作userID使用
             */
            userinfo.setOuterId(jsonObject.getString("nick"));
            userinfo.setUserHeadImg(jsonObject.getString("avatar"));
            userinfo.setFlag("TaoBao");
            userinfo.setAccessToken(accessToken.getToken());
            return userinfo;
        } catch (JSONException e) {
            logger.error("不能解析从taobao返回的json:" + body);
            throw new OAuthException(e);
        }

    }
}
