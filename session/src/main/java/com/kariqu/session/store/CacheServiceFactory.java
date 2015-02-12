package com.kariqu.session.store;

import com.kariqu.common.cache.CacheService;
import com.kariqu.common.cache.impl.MemCacheServiceImpl;
import com.kariqu.session.config.SessionConfig;
import com.kariqu.session.config.SessionConfigFactory;

/**
 * CacheStore 实现对应的缓存服务工厂
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-6-7 下午1:43
 */
public class CacheServiceFactory {

    private static CacheService cacheService;

    static {
        SessionConfig sessionConfig = SessionConfigFactory.getSessionConfig();
        if (null == sessionConfig) {
            throw new RuntimeException("初始化cacheService出错，原因是session框架未初始化");
        }
        String address = sessionConfig.getGlobalConfigInfo("cacheAddress");
        cacheService = new MemCacheServiceImpl(address);
    }


    public static CacheService getCacheService() {
        return cacheService;
    }

}
