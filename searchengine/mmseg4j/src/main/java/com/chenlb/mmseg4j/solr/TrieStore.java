package com.chenlb.mmseg4j.solr;

import com.chenlb.mmseg4j.Dictionary;
import com.kariqu.common.cache.CacheService;
import com.kariqu.common.cache.impl.MemCacheServiceImpl;
import com.kariqu.common.trie.Trie;
import com.kariqu.common.trie.TrieImpl;

/**
 * 管理trie树的类
 *
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-9-6
 *        Time: 下午4:28
 */
public class TrieStore {

    public static final String TRIE_KEY = "trieKey";

    public static final String TRIE_CACHE_ADDRESS = "trieCacheAddress";

    /* 词典 */
    private Dictionary dic = null;

    /**
     * 缓存服务
     */
    private CacheService cacheService;

    /**
     * trie的缓存key
     */
    private String trieKey;

    private Trie trie;


    public TrieStore(Dictionary dic, String trieKey, String trieCacheAddress) {
        this.trieKey = trieKey;
        this.dic = dic;
        cacheService = new MemCacheServiceImpl(trieCacheAddress);
    }

    /**
     * 加载trie到缓存
     */
    public void loadTrieToCache() {
        trie = dic.loadTrieFromDisk();
        if (trie == null) {
            trie = new TrieImpl(false);
            this.saveTrie();
        } else {
            cacheService.put(trieKey, trie);
        }
    }

    public Trie getTrie() {
        return trie;
    }

    /**
     * 保存trie树
     */
    public void saveTrie() {
        cacheService.put(trieKey, trie);
        dic.saveTrie(trie);
    }

    public CacheService getCacheService() {
        return cacheService;
    }

    public boolean isDump() {
        return cacheService.get("isDump") != null;
    }
}
