package com.kariqu.common.cache.impl;

import com.kariqu.common.cache.CacheService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Ehcache缓存实现，这是单机jvm级别缓存
 * @author Tiger
 * @version 1.0.0
 * @since 2011-4-10 下午06:28:19
 */


public class EhcacheCacheServiceImpl implements CacheService {

    protected final Log logger = LogFactory.getLog(getClass());

    private Cache cache;

    @Override
    public Object get(String key) {
        Element element = cache.get(key);
        if (element == null) {
            return null;
        }
        return element.getObjectValue();
    }

    @Override
    public void put(String key, Object value, int second) {
        Element element = new Element(key,value);
        element.setTimeToLive(second);
        cache.put(element);
    }

    @Override
    public void put(String key, Object value) {
        cache.put(new Element(key, value));
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }

    @Override
    public void removeAll() {
        cache.removeAll();
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }
}
