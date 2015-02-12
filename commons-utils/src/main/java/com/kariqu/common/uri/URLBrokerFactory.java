package com.kariqu.common.uri;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * URLBroker工厂类
 *
 * @author Tiger
 * @version 1.0
 * @since 2011-4-22 tiger
 */
public class URLBrokerFactory {

    private Log logger = LogFactory.getLog(URLBrokerFactory.class);

    private String urlConfigName;
    /**
     * 版本号
     */
    private Long version = System.currentTimeMillis();

    private Map<String, URLBroker> urlBrokers = new HashMap<String, URLBroker>();

    public void init() {
        if (StringUtils.isBlank(urlConfigName)) {
            throw new RuntimeException("没有配置url.xml");
        }
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(urlConfigName);
        urlBrokers = initUrlBroker(inputStream);
    }

    private Map<String, URLBroker> initUrlBroker(InputStream inputStream) {
        Map<String, URLBroker> urlBrokers = new HashMap<String, URLBroker>();
        SAXReader reader = new SAXReader();
        try {
            Document root = reader.read(inputStream);
            List<Node> urls = root.selectNodes("/urlConfig/url");
            for (Node url : urls) {

                String name = url.valueOf(("@name"));
                Node serverUrlNode = url.selectSingleNode("severUrl");
                if (serverUrlNode == null) {
                    throw new RuntimeException("服务器URL必须配置");
                }
                String severUrl = serverUrlNode.getText();
                //path默认是空字符串
                String path = "";
                Node pathNode = url.selectSingleNode("path");
                if (pathNode != null) {
                    path = pathNode.getText();
                }

                URLBroker urlBroker = new DefaultURLBroker();
                urlBroker.setServerUrl(severUrl);
                urlBroker.setPath(path);

                List<Node> tokens = url.selectNodes("tokens/token");
                for (Node token : tokens) {
                    urlBroker.addToken(token.valueOf("@name"));
                }

                urlBrokers.put(name, urlBroker);
            }
        } catch (DocumentException e) {
            logger.error("解析url.xml时发现异常:" + e);
            logger.error(e);
            return urlBrokers;
        }
        return urlBrokers;
    }

    public URLBroker getUrl(String name) {
        URLBroker urlBroker = urlBrokers.get(name);
        if (urlBroker == null)
            return null;
        else
            return urlBroker.newInstance();
    }

    /**
     * @param urlConfigName the urlConfigName to set
     */
    public void setUrlConfigName(String urlConfigName) {
        this.urlConfigName = urlConfigName;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
