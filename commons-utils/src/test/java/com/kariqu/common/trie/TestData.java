package com.kariqu.common.trie;

import com.kariqu.common.cache.CacheService;
import com.kariqu.common.cache.impl.MemCacheServiceImpl;
import org.junit.Test;

/**
 * User: Asion
 * Date: 12-6-30
 * Time: 下午1:41
 */
public class TestData {

    @Test
    public void testData() {
        Trie trie = new TrieImpl(false);
        trie.insert("cg", "炒锅");
        trie.insert("cg", "炒股");
        trie.insert("chaoguo", "炒锅");
        System.out.println(trie.findData("cg"));
        System.out.println(trie.findData("chaoguo"));
        System.out.println(trie.findData("ccc"));
    }
    
    @Test
    public void test() {
        Trie trie = new TrieImpl(false);
        trie.insert("hello");
        System.out.println(trie.size());
        System.out.println(trie.bestMatch("hello2",1));
    }


    @Test
    public void testPutToMemcache() {
        Trie trie = new TrieImpl(false);
        trie.insert("hello","你好的");
        trie.insert("hello","你好的");
        CacheService cacheService = new MemCacheServiceImpl("localhost:11211");
        cacheService.put("Trie_Tree_Cache_Key",trie);
        CacheService cacheService2 = new MemCacheServiceImpl("localhost:11211");

        Trie key = (Trie) cacheService2.get("Trie_Tree_Cache_Key");
        System.out.println(key.findData("hello"));
    }

}
