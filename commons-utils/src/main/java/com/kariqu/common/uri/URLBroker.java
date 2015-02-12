package com.kariqu.common.uri;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 此类是线程不安全的
 * <p>
 * 一个URL包括如下几个部分
 * </p>
 * <p/>
 * <pre>
 * URI         = SERVER_URL + PATH + &quot;?&quot; + QUERY_DATA + &quot;#&quot;
 * SERVER_URL = protocol://serverDomain:serverPort
 * PATH        = /path/path
 * QUERY_DATA  = queryKey1=value1&amp;queryKey2=value2
 * </pre>
 * <p>
 * </p>
 * <p/>
 * <pre>
 * http://www.zhiwen.com:8080/template/resource/100/?t=2011.04.22.css
 * </pre>
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-22 下午02:56:40
 */
public abstract class
        URLBroker {

    protected String serverUrl;

    protected String serverDomain;

    protected String serverPort;

    protected String protocol;

    protected String path;

    protected Map<String, Object> queryDatas = new LinkedHashMap<String, Object>();

    protected Set<String> tokens = new HashSet<String>();

    private boolean isRendered;

    private String renderedUrl;

    public Set<String> getTokens() {
        return tokens;
    }

    protected URLBroker(URLBroker urlBroker) {
        this.serverUrl = urlBroker.serverUrl;

        this.path = urlBroker.getPath();
        this.serverDomain = urlBroker.serverDomain;
        this.serverPort = urlBroker.serverPort;
        this.protocol = urlBroker.protocol;
        this.tokens = urlBroker.tokens;
    }

    public URLBroker() {

    }

    public String toString() {
        if (!isRendered) {
            renderedUrl = render();
            this.isRendered = true;
        }
        return renderedUrl;
    }

    /**
     * 执行url渲染,serverUrl如果以/结尾去掉/
     * path不为空才拼接到serverUrl上，path不为空要判断前面有没有/，没有要加上
     *
     * @return
     */
    private String render() {
        if (StringUtils.isNotBlank(serverUrl)) {
            StringBuilder result = new StringBuilder();

            result.append(serverUrl);
            if (StringUtils.isNotBlank(path)) {
                if (path.startsWith("/")) {
                    result.append(serverUrl.endsWith("/") ? path.replaceFirst("/", "") : path);
                } else {
                    if (!serverUrl.endsWith("/")) {
                        result.append("/");
                    }
                    result.append(path);
                }
            }
            String resultUrl = performTokens(result.toString());
            return resultUrl + renderQueryData();
        }
        return "";
    }

    private String performTokens(String serverUrl) {
        for (String token : tokens) {
            serverUrl = serverUrl.replaceAll("\\{" + token + "\\}", String.valueOf(queryDatas.get(token)));
            queryDatas.remove(token);
        }
        return serverUrl;
    }

    private String renderQueryData() {
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (String key : queryDatas.keySet()) {
            if (i == 0) {
                result.append("?");
            }
            if (i > 0) {
                result.append("&");
            }
            result.append(key);
            result.append("=");
            Object value = queryDatas.get(key);
            result.append(value == null ? "" : value);
            i++;
        }
        return result.toString();
    }

    public URLBroker addQueryData(String key, Object value) {
        this.queryDatas.put(key, value);
        return this;
    }

    public abstract URLBroker newInstance();

    public void addToken(String token) {
        tokens.add(token);
    }

    /**
     * @return the serverUrl
     */
    String getServerUrl() {
        return serverUrl;
    }

    /**
     * @param serverUrl the serverUrl to set
     */
    void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * @return the serverDomain
     */
    String getServerDomain() {
        return serverDomain;
    }

    /**
     * @param serverDomain the serverDomain to set
     */
    void setServerDomain(String serverDomain) {
        this.serverDomain = serverDomain;
    }

    /**
     * @return the serverPort
     */
    String getServerPort() {
        return serverPort;
    }

    /**
     * @param serverPort the serverPort to set
     */
    void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * @return the protocol
     */
    String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol to set
     */
    void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the path
     */
    String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    void setPath(String path) {
        this.path = path;
    }

}
