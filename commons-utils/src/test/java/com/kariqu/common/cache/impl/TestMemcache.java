package com.kariqu.common.cache.impl;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;

/**
 * User: Asion
 * Date: 11-8-17
 * Time: 下午12:27
 */
public class TestMemcache implements Serializable {

    @Test
    public void testMem() throws IOException, MemcachedException, TimeoutException, InterruptedException {
        MemcachedClient client = new XMemcachedClient("172.16.0.4", 11211);
        client.flushAll();
        client.set("key", 0, 2);
        //Thread.sleep(1000);
        Object someObject = client.get("key");
        System.out.println(someObject);
        client.set("key", 0, 3);
        someObject = client.get("key", 2000);
        System.out.println(someObject);
        client.delete("key");
        someObject = client.get("key", 2000);
        System.out.println(someObject);

        System.out.println(client.getStats());
    }
}
