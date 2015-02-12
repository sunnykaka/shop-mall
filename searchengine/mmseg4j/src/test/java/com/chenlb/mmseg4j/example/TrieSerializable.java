package com.chenlb.mmseg4j.example;

import com.kariqu.common.trie.Trie;
import com.kariqu.common.trie.TrieImpl;
import com.thoughtworks.xstream.XStream;
import org.junit.Test;

import java.util.Collection;

/**
 * User: Asion
 * Date: 12-7-31
 * Time: 下午2:06
 */
public class TrieSerializable {

    @Test
    public void test() {
        /**
         * xstream是线程安全的
         */
        XStream xstream = new XStream();
        Trie trie = new TrieImpl(false);
        trie.insert("hello");
        trie.insert("chaoguo", "炒锅");
        String result = xstream.toXML(trie);
        Trie trieDb = (Trie) xstream.fromXML(result);
        Collection<String> chaoguo = trieDb.findData("chaoguo");
        System.out.println(chaoguo.toString());
    }
}
