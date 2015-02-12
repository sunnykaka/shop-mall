package com.kariqu.usercenter;

import com.kariqu.common.http.Request;
import com.kariqu.common.http.Response;
import com.kariqu.common.http.Verb;
import com.kariqu.common.oauth.scribe.model.OAuthConstants;
import org.apache.commons.lang.math.NumberUtils;

/**
 * 每小时1000次，每4秒一次请求，一分钟15次，一小时15*60 = 900
 * User: Asion
 * Date: 13-5-16
 * Time: 下午4:49
 */
public class SinaWeiboTest {

    public static void main(String[] args) {


        String url = "https://api.weibo.com/2/statuses/update.json";

        String repostsUrl = "https://api.weibo.com/2/statuses/count.json";

        String token = "2.00bXMNEC23ciAC598a05c3976A9itC";


        Request request = new Request(Verb.GET, "https://api.weibo.com/2/users/show.json");

        request.addQuerystringParameter("uid", "1894517483");
        request.addQuerystringParameter(OAuthConstants.ACCESS_TOKEN, token);

        Response response = request.send();

        System.out.println(response.getBody());


        request = new Request(Verb.POST, url);
        request.addQuerystringParameter("status", "test");
        request.addQuerystringParameter("visible", "0");

        response = request.send();

        System.out.println(response.getBody());


        request = new Request(Verb.GET, repostsUrl);
        request.addQuerystringParameter("ids", "3578635019676956");
        response = request.send();
        System.out.println(response.getBody());

    }
}
