package com.kariqu.common.cache.impl;

import com.kariqu.common.cache.CacheService;

/**
 * 没有缓存.考虑在开发时使用,不缓存东西,让它到真实的数据源去获取
 * Created by Canal.wen on 2014/6/27 15:23.
 */
public class NoCahceServiceImpl implements CacheService {

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void put(String key, Object value) {

    }

    @Override
    public void put(String key, Object value, int second) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public void removeAll() {

    }
}
