package com.kariqu.session.config;

import javax.servlet.FilterConfig;
import java.util.*;

/**
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-18 下午7:32
 */
public class DefaultSessionConfig implements SessionConfig {

    private Map<String, SessionConfigEntry> allConfigEntries = new HashMap<String, SessionConfigEntry>();

    private Map<String, List<SessionConfigEntry>> combinedConfigEntries = new HashMap<String, List<SessionConfigEntry>>();

    private Map<String, String> globalConfigInfo = new HashMap<String, String>();

    @Override
    public SessionConfigEntry getSessionConfigEntry(String name) {
        return allConfigEntries.get(name);
    }

    public void addSessionConfigEntry(SessionConfigEntry sessionConfigEntry) {
        this.allConfigEntries.put(sessionConfigEntry.getName(), sessionConfigEntry);
        if (sessionConfigEntry.isCommbine()) {
            List<SessionConfigEntry> entries = combinedConfigEntries.get(sessionConfigEntry.getCombineKey());
            if (entries == null) {
                entries = new LinkedList<SessionConfigEntry>();
                combinedConfigEntries.put(sessionConfigEntry.getCombineKey(), entries);
            }
            entries.add(sessionConfigEntry);
        }
    }

    public void addGlobalInfo(String key, String value) {
        globalConfigInfo.put(key, value);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public Collection<String> getAttributeNames() {
        return allConfigEntries.keySet();
    }

    @Override
    public Collection<SessionConfigEntry> getAllSessionConfigAttributes() {
        return this.allConfigEntries.values();

    }

    @Override
    public Collection<SessionConfigEntry> getCombinedConfigEntries(String combineKey) {
        final List<SessionConfigEntry> combinedEntries = combinedConfigEntries.get(combineKey);
        return combinedEntries == null ? Collections.<SessionConfigEntry>emptyList() : combinedEntries;
    }

    @Override
    public String getGlobalConfigInfo(String key) {
        return globalConfigInfo.get(key);

    }
}
