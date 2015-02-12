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
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 13-5-23
 * Time: 下午12:25
 */
class RenRen extends AnalyzeUserMap implements PlatformUserInfo {
    private static Log logger = LogFactory.getLog(QQ.class);

    public static final String USERINFO_URL = "https://api.renren.com/restserver.do";
    private static Map<String, String> parameter = new HashMap<String, String>();

    static {
        parameter.put("id", "uid");
        parameter.put("name", "name");
        parameter.put("profile", "tinyurl");
    }


    @Override
    public OuterUserInfo getUserInfo(Token accessToken) {
        return getJointLoginInfo(this.getUserInfo_Map(accessToken), parameter);
    }

    /**
     * 根据 accessToken
     *
     * @param accessToken
     * @return
     * @throws java.io.IOException
     */
    private Map<String, Object> getUserInfo_Map(Token accessToken) {
        OAuthRequest request = new OAuthRequest(Verb.POST, USERINFO_URL);

        request.addQuerystringParameter("method", "users.getInfo");
        request.addQuerystringParameter("format", "json");
        request.addQuerystringParameter("v", "1.0");
        request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, accessToken.getToken());
        Response response = request.send();

        Map<String, Object> userMap = new HashMap<String, Object>();

        ObjectMapper mapper = new ObjectMapper();

        String user_json = response.getBody();
        if (user_json.indexOf("error_code") != -1) {
            logger.error("RenRen 获取用户信息出错,result:" + user_json);
            throw new OAuthException("RenRen 获取用户信息出错");
        }
        List<LinkedHashMap<String, Object>> userlist = null;
        try {
            userlist = mapper.readValue(user_json, List.class);
        } catch (IOException e) {
            logger.error("不能解析从renren返回的json:" + user_json);
            throw new OAuthException(e);
        }
        userMap = userlist.get(0);
        userMap.put("accessToken", accessToken.getToken());
        userMap.put("flag", "RenRen");
        return userMap;
    }
}
