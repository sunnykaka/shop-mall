package com.kariqu.session;

import com.kariqu.session.config.SessionConfigEntry;

/**
 * SessionStore is used to store the session info,it mainly has two implement class,which are CookieStore
 * and CacheStore
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-18 下午5:57
 */
public interface SessionStore {

    /**
     * 查询属性
     *
     * @param sessionConfigEntry
     * @return
     */
    Object getAttribute(SessionConfigEntry sessionConfigEntry);

    /**
     * 设置属性
     *
     * @param sessionConfigEntry
     * @param value
     */
    void setAttribute(SessionConfigEntry sessionConfigEntry, Object value);

    /**
     * 提交SessionStore中存储的数据
     */
    void commit();

    /**
     * 初始化sessionStore
     */
    void init();
}
