package com.kariqu.session.config;

import com.kariqu.session.SessionConfigParseException;
import com.kariqu.session.StoreType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-6-13 下午7:06
 */
public class SessionConfigFactory {

    private static Log logger = LogFactory.getLog(SessionConfigFactory.class);

    private static SessionConfig sessionConfig;

    public static final String LIFECYCLE = "lifecycle";
    public static final String STORE_TYPE = "storeType";
    public static final String DOMAIN = "domain";
    public static final String PATH = "path";
    public static final String HTTPONLY = "httponly";
    public static final String ENCRYPT = "encrypt";
    public static final String READONLY = "readonly";


    @Nullable
    public static SessionConfig getSessionConfig() {
        return sessionConfig;
    }

    public static SessionConfig readSessionConfig(String sessionConfigFileName) throws SessionConfigParseException {
        if (null != sessionConfig)
            return sessionConfig;
        final InputStream configFileInputSteam = SessionConfigFactory.class.getClassLoader().getResourceAsStream(sessionConfigFileName);
        sessionConfig = parseSessionConfig(configFileInputSteam);
        return sessionConfig;
    }

    private static SessionConfig parseSessionConfig(InputStream configFileInputSteam) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(configFileInputSteam);
            DefaultSessionConfig sessionConfig = new DefaultSessionConfig();
            List list = document.selectNodes("/sessionConfig/defaultConfig");
            Element defaultConfig = null;
            if (null != list && !list.isEmpty()) {
                defaultConfig = (Element) list.get(0);
            }
            initCombineEntry(sessionConfig, document, defaultConfig);
            initEntries(document, sessionConfig, defaultConfig);
            initGlobalInfo(document, sessionConfig);
            return sessionConfig;
        } catch (DocumentException e) {
            logger.error("parse session config file error", e);
            throw new SessionConfigParseException("parse session config file error", e);
        }

    }

    private static void initGlobalInfo(Document document, DefaultSessionConfig sessionConfig) {
        final List<Element> globalInfos = document.selectNodes("/sessionConfig/globalinfo/key");
        for (Element globalInfo : globalInfos) {
            sessionConfig.addGlobalInfo(globalInfo.attributeValue("name"), globalInfo.getText());
        }
    }

    private static void initEntries(Document document, DefaultSessionConfig sessionConfig, Element defaultConfig) {
        String defaultDomain = null;
        String defaultLifecycle = "-1";
        String defaultStoreType = "cookie";
        if (null != defaultConfig) {
            defaultDomain = defaultConfig.elementText(DOMAIN);
            defaultLifecycle = defaultConfig.elementText(LIFECYCLE);
            defaultStoreType = defaultConfig.elementText(STORE_TYPE);
        }
        final List<Element> entries = document.selectNodes("/sessionConfig/entries/entry");
        for (Element element : entries) {
            String domain = StringUtils.isBlank(element.elementText(DOMAIN)) ? defaultDomain : element.elementText(DOMAIN);
            String lifecycle = StringUtils.isBlank(element.elementText(LIFECYCLE)) ? defaultLifecycle : element.elementText(LIFECYCLE);
            String storeType = StringUtils.isBlank(element.elementText(STORE_TYPE)) ? defaultStoreType : element.elementText(STORE_TYPE);
            SessionConfigEntry configEntry = new SessionConfigEntry();
            configEntry.setCommbine(false);
            configEntry.setDomain(domain);
            configEntry.setPath(element.elementText(PATH));
            configEntry.setLifeCycle(Integer.valueOf(lifecycle));
            configEntry.setHttpOnly("true".equals(element.elementText(HTTPONLY)));
            configEntry.setReadOnly("true".equals(element.elementText(READONLY)));
            configEntry.setEncrypt("true".equals(element.elementText(ENCRYPT)));
            configEntry.setStoreType("cookie".equals(storeType) ? StoreType.cookie : StoreType.cache);
            configEntry.setKey(element.elementText("key"));
            configEntry.setName(element.attributeValue("name"));
            sessionConfig.addSessionConfigEntry(configEntry);
        }
    }

    private static void initCombineEntry(DefaultSessionConfig sessionConfig, Document document, Element defaultConfig) {
        String defaultDomain = null;
        String defaultLifecycle = "-1";
        if (null != defaultConfig) {
            defaultDomain = defaultConfig.elementText(DOMAIN);
            defaultLifecycle = defaultConfig.elementText(LIFECYCLE);
        }
        List<Element> combines = document.selectNodes("/sessionConfig/combineConfig/combine");
        for (Element element : combines) {
            String combineKey = element.attributeValue("combineKey");
            String domain = StringUtils.isBlank(element.elementText(DOMAIN)) ? defaultDomain : element.elementText(DOMAIN);
            String lifecycle = StringUtils.isBlank(element.elementText(LIFECYCLE)) ? defaultLifecycle : element.elementText(LIFECYCLE);
            String path = element.elementText(PATH);
            String httpOnly = element.elementText(HTTPONLY);
            SessionConfigEntry combinedConfig = new SessionConfigEntry();
            combinedConfig.setDomain(domain);
            combinedConfig.setPath(path);
            combinedConfig.setLifeCycle(Integer.valueOf(lifecycle));
            combinedConfig.setHttpOnly("true".equals(httpOnly));
            combinedConfig.setStoreType(StoreType.cookie);
            combinedConfig.setCombineKey(combineKey);
            combinedConfig.setName(combineKey);
            sessionConfig.addSessionConfigEntry(combinedConfig);
            final List<Element> entries = element.selectNodes("entries/entry");
            for (Element entry : entries) {
                SessionConfigEntry configEntry = new SessionConfigEntry();
                configEntry.setCommbine(true);
                configEntry.setDomain(domain);
                configEntry.setPath(path);
                configEntry.setLifeCycle(Integer.valueOf(lifecycle));
                configEntry.setHttpOnly("true".equals(httpOnly));
                configEntry.setStoreType(StoreType.cookie);
                configEntry.setCombineKey(combineKey);

                configEntry.setName(entry.attributeValue("name"));
                configEntry.setKey(entry.elementText("key"));
                configEntry.setEncrypt("true".equals(entry.elementText(ENCRYPT)));
                configEntry.setReadOnly("true".equals(entry.elementText(READONLY)));
                sessionConfig.addSessionConfigEntry(configEntry);
            }
        }
    }
}
