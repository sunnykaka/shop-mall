package com.kariqu.designcenter.domain.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Properties;


/**
 * 模型config对象，用于配置模型对象，如果模型对象需要配置字段则继承该类
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-5-5 下午09:59:41
 */
public class ModelConfig implements Serializable {

    private static Log logger = LogFactory.getLog(ModelConfig.class);

    /**
     * 一个属性文件格式的字符串
     */
    protected String config;

    private Properties properties = new Properties();

    public String getConfigValue(String key) {
        init();
        String value = properties.getProperty(key);
        return value == null ? "" : value;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getConfig() {
        return config;
    }

    public void setConfigValue(String key, String value) {
        init();
        properties.setProperty(key, value);
        Writer writer = new StringWriter();
        try {
            properties.store(writer, "");
        } catch (IOException e) {
            logger.error("解析对象配置出错", e);
        }
        this.config = writer.toString();

    }

    private void init() {
        if (StringUtils.isNotEmpty(config) && properties.isEmpty()) {
            try {
                properties.load(new StringReader(config));
            } catch (IOException e) {
                logger.error("初始化模型对象配置出错", e);
            }
        }
    }

}
