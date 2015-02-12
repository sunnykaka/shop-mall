package com.kariqu.common.oauth.jointlogin.services.impl;

import com.kariqu.common.http.Response;
import com.kariqu.common.http.Verb;
import com.kariqu.common.oauth.jointlogin.OuterUserInfo;
import com.kariqu.common.oauth.jointlogin.services.PlatformUserInfo;
import com.kariqu.common.oauth.scribe.exceptions.OAuthException;
import com.kariqu.common.oauth.scribe.model.OAuthConfig;
import com.kariqu.common.oauth.scribe.model.OAuthConstants;
import com.kariqu.common.oauth.scribe.model.OAuthRequest;
import com.kariqu.common.oauth.scribe.model.Token;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Asion
 * Date: 13-5-23
 * Time: 上午11:47
 */
class WeiXin extends AnalyzeUserMap implements PlatformUserInfo {

    private static Log logger = LogFactory.getLog(WeiXin.class);

    public static final String OPNEID_URL = "";
    public static final String USERINFO_URL = "";

    private static Map<String, String> parameter = new HashMap<String, String>();


    static {
        parameter.put("id", "idStr");
        parameter.put("name", "nickname");
        parameter.put("profile", "figureurl_2");
    }

    //微信需要用openid来调用api
    public static final String OPEN_ID = "openid";


    private OAuthConfig authConfig;

    public WeiXin(OAuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @Override
    public OuterUserInfo getUserInfo(Token accessToken) {

        return getJointLoginInfo(getUserInfo_Map(accessToken), parameter);
    }


    /**
     * 获取用户信息 微信accessToken 默认有效期为3个月
     *
     * @param accessToken
     * @return
     * @throws java.io.IOException
     */
    private Map<String, Object> getUserInfo_Map(Token accessToken) {

        String openId = getOpenId(accessToken);
        ObjectMapper mapper = new ObjectMapper();

        OAuthRequest request = new OAuthRequest(Verb.GET, USERINFO_URL);

        //微信和其他遵循规范oauth不同，需要这两个参数，一个是appid，一个是openId
        request.addQuerystringParameter(OAuthConstants.CONSUMER_KEY, authConfig.getApiKey());
        request.addQuerystringParameter(OPEN_ID, openId);

        request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());

        Response response = request.send();

        String user_json = response.getBody();

        Map<String, Object> user_map = null;
        try {
            user_map = mapper.readValue(user_json, Map.class);
        } catch (IOException e) {
            logger.error("不能解析从微信返回的json:" + user_json);
            throw new RuntimeException(e);
        }
        if (user_map.isEmpty() || (null != user_map.get("ret") && Integer.parseInt(user_map.get("ret").toString()) != 0)) {
            logger.error("微信 获取用户信息出错,result:" + user_json);
            throw new OAuthException("微信 获取用户信息出错");
        }
        user_map.put("idStr", openId);
        user_map.put("accessToken", accessToken.getToken());
        user_map.put("flag", "WeiXin");
        return user_map;
    }


    /**
     * 根据 accessToken 获取  openid
     *
     * @param accessToken
     * @return
     * @throws java.io.IOException
     */
    private String getOpenId(Token accessToken) {
        OAuthRequest request_forOpenId = new OAuthRequest(Verb.GET,
                OPNEID_URL);
        request_forOpenId.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());

        String result = request_forOpenId.send().getBody();

        return result.substring(result.lastIndexOf(":\"") + 2, result.lastIndexOf("\""));
    }

}
