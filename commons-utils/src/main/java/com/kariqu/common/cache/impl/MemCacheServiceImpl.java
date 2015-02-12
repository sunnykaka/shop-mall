package com.kariqu.common.cache.impl;

import com.kariqu.common.cache.CacheService;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * MemCache实现，可实现独立进程的分布式缓存
 * User: Asion
 * Date: 11-8-17
 * Time: 下午1:34
 */
public class MemCacheServiceImpl implements CacheService {

    private MemcachedClient client;

    private String cacheAddress = "127.0.0.1:11211";

    public MemCacheServiceImpl() {
        try {
            MemcachedClientBuilder builder = new XMemcachedClientBuilder(
                    AddrUtil.getAddresses(cacheAddress));
            client = builder.build();
        } catch (IOException e) {
            throw new RuntimeException("不能初始化Memcached客户端", e);
        }
    }

    /**
     * 地址的配置规则是如果多个地址用空格
     * 比如 "localhost:12000 localhost:12001"
     * <p/>
     * 在spring中使用时请配置构造参数
     *
     * @param address
     */
    public MemCacheServiceImpl(String address) {
        this.cacheAddress = address;
        try {
            MemcachedClientBuilder builder = new XMemcachedClientBuilder(
                    AddrUtil.getAddresses(cacheAddress));
            client = builder.build();
        } catch (IOException e) {
            throw new RuntimeException("不能初始化Memcached客户端:" + cacheAddress, e);
        }
    }

    @Override
    public Object get(String key) {
        try {
            return client.get(key);
        } catch (TimeoutException e) {
            throw new RuntimeException("Memcached访问超时" + cacheAddress, e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MemcachedException e) {
            throw new RuntimeException("Memcached出现错误" + cacheAddress, e);
        }
        return null;
    }

    @Override
    public void put(String key, Object value) {
        try {
            client.set(key, 0, value);
        } catch (TimeoutException e) {
            throw new RuntimeException("Memcached访问超时" + cacheAddress, e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MemcachedException e) {
            throw new RuntimeException("Memcached出现错误" + cacheAddress, e);
        }
    }

    @Override
    public void put(String key, Object value, int second) {
        try {
            client.set(key, second, value);
        } catch (TimeoutException e) {
            throw new RuntimeException("Memcached访问超时" + cacheAddress, e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MemcachedException e) {
            throw new RuntimeException("Memcached出现错误" + cacheAddress, e);
        }
    }

    @Override
    public void remove(String key) {
        try {
            client.delete(key);
        } catch (TimeoutException e) {
            throw new RuntimeException("Memcached访问超时" + cacheAddress, e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MemcachedException e) {
            throw new RuntimeException("Memcached出现错误" + cacheAddress, e);
        }
    }

    @Override
    public void removeAll() {
        try {
            client.flushAll();
        } catch (TimeoutException e) {
            throw new RuntimeException("Memcached访问超时" + cacheAddress, e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MemcachedException e) {
            throw new RuntimeException("Memcached出现错误" + cacheAddress, e);
        }
    }
}
