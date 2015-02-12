package com.kariqu.productcenter.domain;

import com.kariqu.common.json.JsonUtil;

import java.util.Map;

/**
 * 活动配置
 * User: Asion
 * Date: 13-7-30
 * Time: 下午1:27
 */
public abstract class ActivityConfig {


    private String configContent;


    public String getConfigContent() {
        return configContent;
    }

    public void setConfigContent(String configContent) {
        this.configContent = configContent;
    }


    public String getConfigValue(String key) {
        Map config = JsonUtil.json2Object(configContent, Map.class);
        return (String) config.get(key);
    }
}
