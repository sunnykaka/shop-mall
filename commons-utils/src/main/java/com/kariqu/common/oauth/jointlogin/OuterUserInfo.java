package com.kariqu.common.oauth.jointlogin;

import org.apache.commons.lang.StringUtils;
import com.kariqu.common.oauth.scribe.exceptions.OAuthException;

public class OuterUserInfo {

    private String userName;

    //必须返回ID
    private String outerId;

    private String userHeadImg;

    private String flag;

    private String accessToken;

    private String expireIn;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        if (StringUtils.isEmpty(outerId)) {
            throw new OAuthException("第三方平台返回的userId为空");
        }
        this.outerId = outerId;
    }

    public String getUserHeadImg() {
        return userHeadImg;
    }

    public void setUserHeadImg(String userHeadImg) {
        this.userHeadImg = userHeadImg;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(String expireIn) {
        this.expireIn = expireIn;
    }
}
