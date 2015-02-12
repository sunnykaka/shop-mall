package com.kariqu.common.cache;

/**缓存服务，用于缓存页面内容的以及页面对象
 * @author Tiger
 * @since 2011-4-10 下午05:58:00 
 * @version 1.0.0
 */
public interface CacheService {
    
    Object get(String key);
    
    void put(String key,Object value);

    /**
     * 可指定存活时间的缓存接口
     * @param key
     * @param value
     * @param second
     */
    void put(String key, Object value, int second);
    
    void remove(String key);

    void removeAll();

}
