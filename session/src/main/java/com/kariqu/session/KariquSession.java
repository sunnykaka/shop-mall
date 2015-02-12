package com.kariqu.session;

import com.kariqu.session.config.SessionConfig;
import com.kariqu.session.config.SessionConfigEntry;
import com.kariqu.session.store.CookieStore;
import com.kariqu.session.util.BlowfishUtils;
import com.kariqu.session.util.SessionConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.*;

/**
 * kariqusession实现
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-17 下午6:40
 */
public class KariquSession implements HttpSession {

    private Log logger = LogFactory.getLog(KariquSession.class);

    private Map<StoreType, SessionStore> sessionStores;
    private SessionConfig sessionConfig;

    private long createTime;
    private String sessionId;
    private long lastAccessedTime;
    private ServletContext servletContext;

    /**
     * 半个小时过期
     */
    private int maxInactiveInterval = 1800;

    private boolean invalidate = false;


    public KariquSession(SessionConfig sessionConfig, ServletContext servletContext) {
        //todo createTime and lastAccessedTime should be the same time?
        this.createTime = System.currentTimeMillis();
        this.lastAccessedTime = createTime;
        this.sessionConfig = sessionConfig;
        this.servletContext = servletContext;
    }

    @Override
    public long getCreationTime() {
        return this.createTime;
    }

    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @Override
    @Deprecated
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String name) {
        final SessionConfigEntry sessionConfigEntry = sessionConfig.getSessionConfigEntry(name);
        if (sessionConfigEntry == null) {
            return null;
        }
        final SessionStore sessionStore = sessionStores.get(sessionConfigEntry.getStoreType());
        if (sessionStore == null) {
            return null;
        }
        return sessionStore.getAttribute(sessionConfigEntry);
    }

    @Override
    @Deprecated
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(sessionConfig.getAttributeNames());
    }

    @Override
    @Deprecated
    public String[] getValueNames() {
        final Collection<String> attributeNames = sessionConfig.getAttributeNames();
        return attributeNames.toArray(new String[attributeNames.size()]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        setAttribute(name, value, false);
    }

    @Override
    @Deprecated
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }


    @Override
    public void removeAttribute(String name) {
        setAttribute(name, null);
    }

    @Override
    @Deprecated
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        for (SessionConfigEntry configEntry : sessionConfig.getAllSessionConfigAttributes()) {
            if (configEntry.getLifeCycle() <= 0) {
                removeAttribute(configEntry.getName(), true);
            }
        }
        this.invalidate = true;
    }

    @Override
    public boolean isNew() {
        return true;
    }

    public void init() {
        initSessionStore();
        this.sessionId = getSessionId();
        generateTrackId();
    }

    private void generateTrackId() {
        String trackId = (String) getAttribute(SessionConstants.TRACK_ID);
        if (StringUtils.isBlank(trackId)) {
            trackId = generateNewTrackId();
            setAttribute(SessionConstants.TRACK_ID, trackId, true);
        }

    }

    public SessionConfig getSessionConfig() {
        return sessionConfig;
    }

    private void setAttribute(String name, Object value, boolean force) {
        final SessionConfigEntry sessionConfigEntry = sessionConfig.getSessionConfigEntry(name);
        if (sessionConfigEntry == null) {
            return;
        }
        if (!force && sessionConfigEntry.isReadOnly()) {
            throw new ModifyReadOnlyAttributeException("not support to change the readonly attribute,the name" + name);
        }
        final SessionStore sessionStore = sessionStores.get(sessionConfigEntry.getStoreType());
        sessionStore.setAttribute(sessionConfigEntry, value);
    }

    public String getSessionId() {
        String sessionId = (String) getAttribute(SessionConstants.SESSION_ID);
        if (StringUtils.isBlank(sessionId)) {
            sessionId = generateNewSessionId();
            setAttribute(SessionConstants.SESSION_ID, sessionId, true);
        }

        return sessionId;
    }

    private String generateNewTrackId() {
        return UUID.randomUUID().toString();
    }

    private String generateNewSessionId() {
        return UUID.randomUUID().toString();
    }

    public void setSessionStores(Map<StoreType, SessionStore> sessionStores) {
        this.sessionStores = sessionStores;
    }

    public void commit() {
        for (SessionStore sessionStore : sessionStores.values()) {
            sessionStore.commit();
        }
    }


    private void initSessionStore() {
        for (SessionStore sessionStore : sessionStores.values()) {
            sessionStore.init();
        }
    }

    /**
     * 用于失效session信息时使用
     *
     * @param name
     * @param force
     */
    private void removeAttribute(String name, boolean force) {
        setAttribute(name, null, true);
    }

    public String getEncryptId() {
        return BlowfishUtils.encryptBlowfish(getSessionId(), ((CookieStore) sessionStores.get(StoreType.cookie)).getBlowfishKey());
    }


}
