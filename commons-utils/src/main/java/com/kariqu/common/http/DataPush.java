package com.kariqu.common.http;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.Map;

/**
 * http数据推送工具
 * User: Asion
 * Date: 12-4-27
 * Time: 下午5:22
 */
public class DataPush {

    private HttpClient httpClient;

    public DataPush() {
        MultiThreadedHttpConnectionManager mgr = new MultiThreadedHttpConnectionManager();
        mgr.getParams().setDefaultMaxConnectionsPerHost(10000);
        mgr.getParams().setMaxTotalConnections(10000);
        mgr.getParams().setSoTimeout(20000); //20 secs
        mgr.getParams().setConnectionTimeout(5000); //5 secs
        httpClient = new HttpClient(mgr);
    }

    public void push(String url) throws IOException {
        PostMethod post = new PostMethod(url);
        httpClient.executeMethod(post);
    }

    public void push(String url, Map<String, String> queryParams) throws IOException {
        PostMethod post = new PostMethod(url);
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            post.addParameter(param.getKey(), param.getValue());
        }
        httpClient.executeMethod(post);
    }
}
