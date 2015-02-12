package com.kariqu.session.config;

import javax.servlet.FilterConfig;
import java.util.Collection;

/**
 * Session框架配置
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-18 下午7:27
 */
public interface SessionConfig {

    SessionConfigEntry getSessionConfigEntry(String name);

    void init(FilterConfig filterConfig);

    /**
     * query all session config attribute names
     *
     * @return
     */
    Collection<String> getAttributeNames();

    Collection<SessionConfigEntry> getAllSessionConfigAttributes();

    Collection<SessionConfigEntry> getCombinedConfigEntries(String combineKey);

    /**
     * gain the global config infomation,for example the encrypt key and so on
     *
     * @param key
     * @return
     */
    String getGlobalConfigInfo(String key);
}
